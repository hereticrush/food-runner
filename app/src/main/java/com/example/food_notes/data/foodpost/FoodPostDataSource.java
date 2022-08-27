package com.example.food_notes.data.foodpost;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface FoodPostDataSource {

    Flowable<List<FoodPost>> getAllData();

    Flowable<FoodPost> getFoodPost(Long id);

    Completable insertOrUpdate(FoodPost foodPost);

    Single<String> getDateById(Long id);

    void deleteAllData();

    void deleteFoodPostById(Long post_id);

}
