package com.example.food_notes.data.foodpost;

import com.example.food_notes.data.relations.FoodPostAndPicture;

import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface FoodPostDataSource {

    Flowable<List<FoodPost>>getAllData();

    Flowable<FoodPost> getFoodPost(Long id);

    Flowable<List<FoodPostAndPicture>> getPostsAndPictures();

    Completable insertOrUpdateData(FoodPost foodPost);

    Single<Date> getDateById(Long id);

    void deleteAllData();

    void deleteFoodPostById(Long post_id);

    Flowable<List<FoodPostAndPicture>> getFoodAndImage();
}
