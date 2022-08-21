package com.example.food_notes.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Flow;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_notes.R;
import com.example.food_notes.db.converters.Converters;
import com.example.food_notes.data.relations.FoodPostAndPicture;
import com.example.food_notes.ui.view.FoodPostViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableConverter;
//TODO implement foodpost adapter for user main rv
public class FoodPostRecyclerViewAdapter extends RecyclerView.Adapter<FoodPostViewHolder> {

    private final Context mContext;
    private final FoodPostViewModel mFoodPostViewModel;

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
        List<FoodPostAndPicture> foodPostAndPictures = new ArrayList<>(
                mFoodPostViewModel.getFoodAndImage().blockingFirst());
        holder.getRatingBar().setRating(foodPostAndPictures.get(position).foodPost.getRating());
        holder.getTextTitle().setText(foodPostAndPictures.get(position).foodPost.getTitle());
        holder.getTextDesc().setText(foodPostAndPictures.get(position).foodPost.getDescription());
        holder.getTextDate().setText(foodPostAndPictures.get(position).foodPost.getDate().toString());
        // careful with below line
        holder.getImageView().setImageURI(Uri.parse(foodPostAndPictures.get(position).picture.getUri_string()));
    }

    @Override
    public int getItemCount() {
        List<FoodPostAndPicture> postAndPictures = mFoodPostViewModel
                .getFoodAndImage().blockingFirst();
        return postAndPictures.size();
    }

}
