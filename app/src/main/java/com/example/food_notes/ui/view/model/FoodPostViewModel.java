package com.example.food_notes.ui.view.model;

import androidx.lifecycle.ViewModel;

import com.example.food_notes.data.foodpost.FoodPost;
import com.example.food_notes.data.foodpost.FoodPostDataSource;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

/**
 * FoodPostViewModel deals with operations
 * involving FoodPost entity and connects with ui
 */
public class FoodPostViewModel extends ViewModel {

    private final FoodPostDataSource mDataSource;
    private FoodPost mFoodPost;

    public FoodPostViewModel(FoodPostDataSource repository) {
        mDataSource = repository;
    }


    public Flowable<String> getTitle(Long id) {
        return getAllFoodPosts()
                .flatMap(Flowable::fromIterable)
                .filter(i -> i.getPost_id().equals(id))
                .concatMap(foodPost -> Flowable.just(foodPost.getTitle()));
    }

    public Flowable<String> getDescription() {
        return mDataSource.getFoodPost(mFoodPost.getPost_id())
                .map(FoodPost::getDescription);
    }

    public Flowable<List<FoodPost>> getAllFoodPosts() {
        return mDataSource.getAllData();
    }

    public void deleteFoodPost() {
        mDataSource.deleteFoodPostById(mFoodPost.getPost_id());
    }
}
