package com.example.food_notes.data.mapping;

import com.example.food_notes.data.foodpost.FoodPost;
import com.example.food_notes.data.relations.UserWithFoodPosts;
import com.example.food_notes.data.user.User;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;

public interface UserAndFoodPostDataSource {

    Flowable<List<UserWithFoodPosts>> getAllData();

    //Maybe<User> getUser(int id);

    Completable deleteAllData();

    Completable insertUser(User user);

    Maybe<Long> insertFoodPost(FoodPost foodPost);
}
