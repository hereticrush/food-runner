package com.example.food_notes.ui.view.model;

import androidx.lifecycle.ViewModel;

import com.example.food_notes.data.foodpost.FoodPost;
import com.example.food_notes.data.foodpost.FoodPostDataSource;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

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

    /**
     * Returns title of FoodPost object
     * @param id Long type FoodPost.post_id
     * @return title String wrapped in a Flowable
     */
    public Flowable<String> getTitle(final Long id) {
        return getAllFoodPosts()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .flatMap(Flowable::fromIterable)
                .filter(i -> i.getPost_id().equals(id))
                .concatMap(foodPost -> Flowable.just(foodPost.getTitle()));
    }

    public Flowable<String> getDescription() {
        return mDataSource.getFoodPost(mFoodPost.getPost_id())
                .map(FoodPost::getDescription);
    }

    public void loadCardItems() {

    }

    public Flowable<List<FoodPost>> getAllFoodPosts() {
        return mDataSource.getAllData();
    }

    public void deleteItem(final Long id) {
        mDataSource.deleteFoodPostById(id);
    }
}
