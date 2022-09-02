package com.example.food_notes.data.user;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface UserDataSource {

    Maybe<User> getUserById(int id);

    Flowable<List<User>> getAllUsers();

    Completable insertUser(User user);

    Completable deleteUserByUsername(User user);

    Completable deleteAllUsers();
}
