package com.example.food_notes.ui.view;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.example.food_notes.data.foodpost.FoodPost;
import com.example.food_notes.data.foodpost.FoodPostDataSource;
import com.example.food_notes.db.converters.Converters;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class FoodPostViewModel extends AndroidViewModel {

    private final FoodPostDataSource mDataSource;
    private FoodPost mFoodPost;

    public FoodPostViewModel(FoodPostDataSource repository, Application application) {
        super(application);
        mDataSource = repository;
    }


    public Flowable<String> getTitle() {
        return mDataSource.getFoodPost(mFoodPost.getPost_id())
                .map(FoodPost::getTitle);
    }

    public Flowable<String> getDescription() {
        return mDataSource.getFoodPost(mFoodPost.getPost_id())
                .map(FoodPost::getDescription);
    }

    public void deleteFoodPost() {
        mDataSource.deleteFoodPostById(mFoodPost.getPost_id());
    }
}
