package com.example.food_notes.ui.view.model;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.food_notes.data.foodpost.FoodPost;
import com.example.food_notes.data.foodpost.FoodPostDataSource;
import com.example.food_notes.db.converters.Converters;
import com.example.food_notes.ui.view.ApiClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * FoodPostViewModel deals with operations
 * involving FoodPost entity and connects with ui
 */
public class FoodPostViewModel extends ViewModel implements ApiClient {

    private final FoodPostDataSource mDataSource;
    private FoodPost mFoodPost;
    private final CompositeDisposable disposable = new CompositeDisposable();

    public FoodPostViewModel(FoodPostDataSource repository) {
        mDataSource = repository;
    }

    // TODO not done yet
    public void loadCardItems() {
        getAllFoodPosts();
    }

    /**
     * Returns a uri string from a bitmap
     * @param bitmap image
     * @return image uri string
     */
    public String fromBitmap(Bitmap bitmap) {
        return Converters.BitmapToStr(bitmap);
    }

    /**
     * Returns a bitmap from an image string resource
     * @param image_str image string
     * @return bitmap
     */
    public Bitmap getBitmap(final String image_str) {
        return Converters.StrToBitmap(image_str);
    }

    /**
     * Attempts to add a FoodPost item to database with given attributes.
     * This function also sets the time
     * of posting of this object and subscribes a CompletableObserver to it.
     * @param imageUriString String type image file uri path
     * @param title String type title
     * @param description String type description
     * @param rating float type rating
     * @param latitude Double type latitude
     * @param longitude Double type longitude
     */
    public void addItem(final String imageUriString, final String title, final String description,
            final float rating, final Double latitude, final Double longitude) {
        Date currentDate = Calendar.getInstance().getTime();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:s", Locale.getDefault());
        String dateString = format.format(currentDate);
        // TODO latitude and longitude are hardcoded right now. Need GoogleAPI locations to set them
        mFoodPost = new FoodPost(imageUriString, title, description, rating, latitude, longitude);
        mFoodPost.setSent_at(dateString);
        mDataSource.insertOrUpdate(mFoodPost)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .delaySubscription(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(
                        this::whenCompleted,
                        throwable -> onFailed(throwable.getLocalizedMessage()),
                        disposable
                );
    }

    /**
     * Gets all FoodPost items from database
     * @return Flowable list of FoodPost objects
     */
    public Flowable<List<FoodPost>> getAllFoodPosts() {
        return mDataSource.getAllData().subscribeOn(Schedulers.computation())
                .onBackpressureBuffer(50, true);
    }

    /**
     * Returns the size of the list that contains the data
     * @return int type decimal number
     */
    public int getListSize() {
        AtomicInteger n = new AtomicInteger();
        getAllFoodPosts().flatMap(Flowable::fromIterable).forEach(
                foodPost -> n.getAndIncrement()
        );
        return n.get();
    }

    /**
     * Deletes a card item
     * @param id Long type FoodPost_id
     * @return Completable response, either completed or an error
     */
    public void deleteItem(final Long id) {
        mDataSource.deleteFoodPostById(id).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).delaySubscription(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(
                        this::whenCompleted,
                        throwable -> onFailed(throwable.getLocalizedMessage()),
                        disposable
                );
    }

    @Override
    public void onSuccess() {
        Log.d("SUCCESS", "onSuccess: Transaction was successful");
    }

    @Override
    public void onFailed(String log) {
        Log.e("ERROR", log);
    }

    @Override
    public void whenCompleted() {
        Log.d("COMPLETED", "done");
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
