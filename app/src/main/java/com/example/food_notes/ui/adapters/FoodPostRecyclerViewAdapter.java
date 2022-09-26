package com.example.food_notes.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.food_notes.R;
import com.example.food_notes.data.foodpost.FoodPost;
import com.example.food_notes.databinding.RowItemBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class FoodPostRecyclerViewAdapter extends RecyclerView.Adapter<FoodPostRecyclerViewAdapter.FoodPostViewHolder> {

    private static final String TAG = "adapter";
    private final Context mContext;
    private ArrayList<FoodPost> list;
    private RecyclerViewItemClickListener listener;
    private String item_id;

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
        item_id = list.get(position).getPost_id();
        Glide.with(mContext).load(Uri.parse(list.get(position).getImage_uri()))
                .placeholder(R.drawable.ic_baseline_choose_image_24).into(holder.itemBinding.ivPostImage);
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

    public String getItem_id() {
        return item_id;
    }
}
