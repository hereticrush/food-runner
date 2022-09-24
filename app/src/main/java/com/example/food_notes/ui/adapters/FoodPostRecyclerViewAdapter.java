package com.example.food_notes.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.food_notes.data.foodpost.FoodPost;
import com.example.food_notes.databinding.RowItemBinding;

import java.util.ArrayList;

public class FoodPostRecyclerViewAdapter extends RecyclerView.Adapter<FoodPostRecyclerViewAdapter.FoodPostViewHolder> {

    private final Context mContext;
    private ArrayList<FoodPost> list;
    private RecyclerViewItemClickListener listener;

    public FoodPostRecyclerViewAdapter(Context context, ArrayList<FoodPost> list, RecyclerViewItemClickListener listener) {
        this.mContext = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowItemBinding itemBinding = RowItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FoodPostViewHolder(itemBinding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodPostViewHolder holder, final int position) {
        Glide.with(mContext).load(Uri.parse(list.get(position).getImage_uri())).into(holder.itemBinding.ivPostImage);
        holder.itemBinding.tvTitle.setText(list.get(position).getTitle());
        holder.itemBinding.tvDesc.setText(list.get(position).getDescription());
        holder.itemBinding.rbPost.setRating(list.get(position).getRating());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // holder class
    public static class FoodPostViewHolder extends RecyclerView.ViewHolder {

        RowItemBinding itemBinding;

        /**
         * Constructs the holder class of FoodPost
         * @param itemBinding RowItemBinding view binding for holder
         */
        public FoodPostViewHolder(RowItemBinding itemBinding, RecyclerViewItemClickListener listener) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;

            itemBinding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            listener.onLongItemClick(pos);
                        }
                    }
                    return true;
                }
            });
        }

    }

}
