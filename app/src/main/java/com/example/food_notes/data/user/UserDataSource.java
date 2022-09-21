package com.example.food_notes.data.user;

import java.util.List;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

public interface UserDataSource {

    Maybe<User> getUserById(String id);

    Flowable<List<User>> getAllUsers();

    Completable insertUser(User user);

    Completable deleteUser(User user);

    Completable deleteAllUsers();
}
