package com.example.food_notes.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.food_notes.R;
import com.example.food_notes.data.foodpost.FoodPost;
import com.example.food_notes.ui.view.model.FoodPostViewModel;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

public class FoodPostRecyclerViewAdapter extends RecyclerView.Adapter<FoodPostRecyclerViewAdapter.FoodPostViewHolder> {

    private final Context mContext;
    private final FoodPostViewModel mFoodPostViewModel;
    private Flowable<List<FoodPost>> items;

    // holder class
    public static class FoodPostViewHolder extends RecyclerView.ViewHolder {
        private final TextView textTitle, textDesc, textDate;
        private final RatingBar ratingBar;
        private final ImageView imageView;

        /**
         * Constructs the holder class of FoodPost
         * @param itemView view
         */
        public FoodPostViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = (TextView) itemView.findViewById(R.id.tv_title);
            textDesc = (TextView) itemView.findViewById(R.id.tv_desc);
            textDate = (TextView) itemView.findViewById(R.id.tv_date);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rb_post);
            imageView = (ImageView) itemView.findViewById(R.id.iv_post_image);
        }

        public TextView getTextTitle() {
            return textTitle;
        }

        public TextView getTextDesc() {
            return textDesc;
        }

        public TextView getTextDate() {
            return textDate;
        }

        public RatingBar getRatingBar() {
            return ratingBar;
        }

        public ImageView getImageView() {
            return imageView;
        }
    }


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
