package com.example.food_notes.ui.view.model;

import androidx.lifecycle.ViewModel;

import com.example.food_notes.data.foodpost.FoodPost;
import com.example.food_notes.data.foodpost.FoodPostDataSource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
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

    public Single<Long> getImageId(final Long id) {
        return mDataSource.getFoodPost(id)
                .map(FoodPost::getImg_id)
                .toObservable().singleOrError();
    }

    public Flowable<String> getDescription() {
        return mDataSource.getFoodPost(mFoodPost.getPost_id())
                .map(FoodPost::getDescription);
    }

    public Flowable<Long> getFoodPostId(final Long id) {
        return Flowable.just(id);
    }

    public void loadCardItems() {

    }

    /**
     * Adds a new item or updates an existing item
     * @param user_id FoodPost foreign_key user_id int
     * @param title String type title
     * @param description String type description
     * @param latitude Double type latitude of the location
     * @param longitude Double type longitude of the location
     * @param rating float type rating range(0, 5) inclusive
     * @return Completable response, either completed or an error
     */
    public Completable addOrUpdateItem(final int user_id, final String title, final String description, final Double latitude, final Double longitude,
                               final float rating) {
        Date currentDate = Calendar.getInstance().getTime();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:s", Locale.getDefault());
        String dateString = format.format(currentDate);
        mFoodPost = mFoodPost == null ? new FoodPost(user_id, title, description, rating, latitude, longitude)
        : new FoodPost(mFoodPost.getPost_id(), mFoodPost.getUser_id(), mFoodPost.getImg_id(), title, description,
                rating, dateString, latitude, longitude);
        return mDataSource.insertOrUpdate(mFoodPost).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).delaySubscription(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread());
    }

    /**
     * Gets all FoodPost items from database
     * @return Flowable list of FoodPost objects
     */
    public Flowable<List<FoodPost>> getAllFoodPosts() {
        return mDataSource.getAllData().subscribeOn(Schedulers.computation());
    }

    /**
     * Deletes a card item
     * @param id Long type FoodPost_id
     * @return Completable response, either completed or an error
     */
    public Completable deleteItem(final Long id) {
        return mDataSource.deleteFoodPostById(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).delaySubscription(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread());
    }
}
