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

public class FoodPostRecyclerViewAdapter extends RecyclerView.Adapter<FoodPostRecyclerViewAdapter.FoodPostViewHolder> implements EventListener<QuerySnapshot> {

    private static final String TAG = "adapter";
    private final Context mContext;
    private ArrayList<FoodPost> list;
    private Query query;
    private ArrayList<DocumentSnapshot> snapshots;
    private ListenerRegistration registration;
    private RecyclerViewItemClickListener listener;

    public FoodPostRecyclerViewAdapter(Context context, Query query, RecyclerViewItemClickListener listener) {
        this.mContext = context;
        this.list = list;
        this.listener = listener;
        this.query = query;
        snapshots = new ArrayList<>();
    }

    @NonNull
    @Override
    public FoodPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowItemBinding itemBinding = RowItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FoodPostViewHolder(itemBinding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodPostViewHolder holder, final int position) {
        /*Glide.with(mContext).load(Uri.parse(list.get(position).getImage_uri()))
                .placeholder(R.drawable.ic_baseline_choose_image_24).into(holder.itemBinding.ivPostImage);
        holder.itemBinding.tvTitle.setText(list.get(position).getTitle());
        holder.itemBinding.tvDesc.setText(list.get(position).getDescription());
        holder.itemBinding.rbPost.setRating(list.get(position).getRating());*/
        DocumentSnapshot snapshot = snapshots.get(position);
        snapshot.getReference().addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "onEvent: error", error);
                }
                if (value != null && value.exists()) {
                    Map<String, Object> data = value.getData();
                    String post_id = (String) data.get("post_id");
                    String image_uri = (String) data.get("image_uri");
                    String title = (String) data.get("title");
                    String description = (String) data.get("description");
                    Double rating = (Double) data.get("rating");

                    holder.itemBinding.ivPostImage.setImageURI(Uri.parse(image_uri));
                    holder.itemBinding.tvTitle.setText(title);
                    holder.itemBinding.tvDesc.setText(description);
                    holder.itemBinding.rbPost.setRating(rating.floatValue());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return snapshots.size();
    }

    private void startListening() {
        if (registration == null) {
            registration = query.addSnapshotListener(this);
        }
    }

    private void stopListening() {
        registration.remove();
        registration = null;

        snapshots.clear();
        notifyDataSetChanged();
    }

    public void setQuery(final Query query) {
        stopListening();
        snapshots.clear();
        notifyDataSetChanged();
        startListening();
    }

    public Query getQuery() {
        return this.query;
    }

    public DocumentSnapshot getSnapshot(int index) {
        return snapshots.get(index);
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
        if (error != null) {
            Log.w(TAG, "onEvent: error", error);
            return;
        } if (value != null) {
            for (DocumentChange change: value.getDocumentChanges()){
                switch (change.getType()) {
                    case ADDED:
                        onDocumentAdded(change);
                        break;
                    case MODIFIED:
                        onDocumentModified(change);
                        break;
                    case REMOVED:
                        onDocumentRemoved(change);
                        break;
                }
            }
        }
        notifyDataSetChanged();
    }

    private void onDocumentAdded(final DocumentChange change) {
        snapshots.add(change.getNewIndex(), change.getDocument());
        notifyItemInserted(change.getNewIndex());
    }

    private void onDocumentModified(final DocumentChange change) {
        if (change.getOldIndex() == change.getNewIndex()) {
            snapshots.set(change.getOldIndex(), change.getDocument());
            notifyItemChanged(change.getOldIndex());
        } else {
            snapshots.remove(change.getOldIndex());
            snapshots.add(change.getNewIndex(), change.getDocument());
            notifyItemMoved(change.getOldIndex(), change.getNewIndex());
        }
    }

    private void onDocumentRemoved(final DocumentChange change) {
        snapshots.remove(change.getOldIndex());
        notifyItemRemoved(change.getOldIndex());
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
