package com.example.food_notes.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.food_notes.R;
import com.example.food_notes.data.foodpost.FoodPost;
import com.example.food_notes.ui.view.model.FoodPostViewModel;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

public class FoodPostRecyclerViewAdapter extends RecyclerView.Adapter<FoodPostViewHolder> {

    private final Context mContext;
    private final FoodPostViewModel mFoodPostViewModel;
    private Flowable<List<FoodPost>> items;

    public FoodPostRecyclerViewAdapter(Context context, FoodPostViewModel foodPostViewModel) {
        this.mContext = context;
        this.mFoodPostViewModel = foodPostViewModel;
    }

    @NonNull
    @Override
    public FoodPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item, parent, false);
        return new FoodPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodPostViewHolder holder, final int position) {
        items = mFoodPostViewModel.getAllFoodPosts();
        Flowable<FoodPost> item = items.map(foodPosts -> foodPosts.get(position));
        Glide.with(mContext)
                        .asBitmap()
                                .load(item.map(FoodPost::getImg_str))
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .into(holder.getImageView());
        holder.getTextTitle().setText(item.map(FoodPost::getTitle).toString());
        holder.getTextDesc().setText(item.map(FoodPost::getDescription).toString());
        holder.getTextDate().setText(item.map(FoodPost::getSent_at).toString());
        holder.getRatingBar().setRating(item.map(FoodPost::getRating).map(Float::floatValue).blockingSingle());

    }

    @Override
    public int getItemCount() {
        return mFoodPostViewModel.getListSize();
    }

}
