package com.example.food_notes.ui.adapters;


import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_notes.R;

public class FoodPostViewHolder extends RecyclerView.ViewHolder {

    private final TextView textTitle, textDesc, textDate;
    private final RatingBar ratingBar;
    private final ImageView imageView;

    public FoodPostViewHolder(@NonNull View itemView) {
        super(itemView);
        textTitle = itemView.findViewById(R.id.tv_title);
        textDesc = itemView.findViewById(R.id.tv_desc);
        textDate = itemView.findViewById(R.id.tv_date);
        ratingBar = itemView.findViewById(R.id.rb_post);
        imageView = itemView.findViewById(R.id.iv_post_image);
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
