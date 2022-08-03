package com.example.food_notes.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
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
public class UserMainRecyclerViewAdapter extends RecyclerView.Adapter<FoodPostViewHolder> {

    private final Context mContext;
    private final FoodPostViewModel mFoodPostViewModel;

    public UserMainRecyclerViewAdapter(Context context, FoodPostViewModel foodPostViewModel) {
        this.mContext = context;
        this.mFoodPostViewModel = foodPostViewModel;
    }


    @NonNull
    @Override
    public FoodPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FoodPostViewHolder(LayoutInflater.from(mContext).inflate(R.layout.row_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FoodPostViewHolder holder, int position) {
        List<FoodPostAndPicture> foodPostAndPictures = new ArrayList<>(
                mFoodPostViewModel.getFoodAndImage().blockingFirst());
        holder.ratingBar.setRating(foodPostAndPictures.get(position).foodPost.getRating());
        holder.textTitle.setText(foodPostAndPictures.get(position).foodPost.getTitle());
        holder.textDesc.setText(foodPostAndPictures.get(position).foodPost.getDescription());
        holder.textDate.setText(foodPostAndPictures.get(position).foodPost.getDate().toString());
        //holder.imageView.setImageBitmap();
        //TODO fetch photo from db
    }

    @Override
    public int getItemCount() {
        List<FoodPostAndPicture> postAndPictures = mFoodPostViewModel
                .getFoodAndImage().blockingFirst();
        return postAndPictures.size();
    }

}
