package com.example.food_notes.data.user;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface UserDataSource {

    Single<User> getUser(Long id);

    Flowable<List<User>> getAllUsers();

    Completable insertOrUpdateUser(User user);

    Completable deleteUserByUsername(User user);

    Completable deleteAllUsers();
}
