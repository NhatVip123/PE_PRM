package com.example.se1751_net_he163424_tourmgtapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se1751_net_he163424_tourmgtapp.R;
import com.example.se1751_net_he163424_tourmgtapp.dao.TourDatabaseHelper;
import com.example.se1751_net_he163424_tourmgtapp.entity.Tour;

import java.util.ArrayList;

public class TourAdapter extends RecyclerView.Adapter<TourAdapter.TourViewHolder> {

    private ArrayList<Tour> tourList;
    private Context context;
    private TourDatabaseHelper dbHelper;
    private OnTourDeletedListener onTourDeletedListener;

    public interface OnTourDeletedListener {
        void onTourDeleted();
    }

    public TourAdapter(Context context, ArrayList<Tour> tourList, TourDatabaseHelper dbHelper, OnTourDeletedListener listener) {
        this.context = context;
        this.tourList = tourList;
        this.dbHelper = dbHelper;
        this.onTourDeletedListener = listener;
    }

    @NonNull
    @Override
    public TourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tour_item, parent, false);
        return new TourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TourViewHolder holder, int position) {
        Tour tour = tourList.get(position);

        holder.indexTextView.setText(String.valueOf(position + 1));
        holder.codeTextView.setText(tour.getCode());
        holder.titleTextView.setText(tour.getTitle());
        holder.membersTextView.setText(String.valueOf(tour.getMembers()));
        holder.priceTextView.setText(String.format("$%.2f", tour.getPrice()));

        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirmation")
                    .setMessage("Are you sure you want to delete?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (dbHelper.deleteTour(tour.getCode())) {
                            tourList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, tourList.size());
                            onTourDeletedListener.onTourDeleted();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return tourList.size();
    }

    public static class TourViewHolder extends RecyclerView.ViewHolder {
        TextView indexTextView, codeTextView, titleTextView, membersTextView, priceTextView;
        Button deleteButton;

        public TourViewHolder(@NonNull View itemView) {
            super(itemView);
            indexTextView = itemView.findViewById(R.id.indexTextView);
            codeTextView = itemView.findViewById(R.id.codeTextView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            membersTextView = itemView.findViewById(R.id.membersTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}