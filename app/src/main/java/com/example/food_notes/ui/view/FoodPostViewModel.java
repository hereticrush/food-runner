package com.example.food_notes.ui.view;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.example.food_notes.data.foodpost.FoodPost;
import com.example.food_notes.data.foodpost.FoodPostDataSource;
import com.example.food_notes.data.foodpost.FoodPostRepository;
import com.example.food_notes.data.relations.FoodPostAndPicture;
import com.example.food_notes.db.converters.Converters;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class FoodPostViewModel extends AndroidViewModel {

    private final FoodPostDataSource mDataSource;
    private FoodPost mFoodPost;

    public FoodPostViewModel(FoodPostDataSource repository, Application application) {
        super(application);
        mDataSource = repository;
    }

    //TODO be careful with this query
    public Flowable<List<FoodPostAndPicture>> getFoodAndImage() {
        return mDataSource.getFoodAndImage();
    }

    public Flowable<String> getTitle() {
        return mDataSource.getFoodPost(mFoodPost.getPost_id())
                .map(FoodPost::getTitle);
    }

    public Flowable<String> getDescription() {
        return mDataSource.getFoodPost(mFoodPost.getPost_id())
                .map(FoodPost::getDescription);
    }

    //TODO check this in the future
    public Single<String> getDateString() {
        return mDataSource.getDateById(mFoodPost.getPost_id()).doOnSuccess(
                Converters::fromDateToTimestamp).map(date -> toString());
    }

    public void deleteFoodPost() {
        mDataSource.deleteFoodPostById(mFoodPost.getPost_id());
    }
}
