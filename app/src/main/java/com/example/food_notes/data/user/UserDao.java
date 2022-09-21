package com.example.food_notes.data.user;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;


import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;

@Dao
public interface UserDao {

    @Insert
    Completable insertUser(User user);

    @Query("SELECT * FROM users ORDER BY user_id ASC")
    Flowable<List<User>>getAllUsers();

    @Query("SELECT * FROM users WHERE user_id LIKE :userId")
    Maybe<User> getUserById(String userId);

    @Delete
    Completable deleteUser(User user);

    @Query("DELETE FROM users")
    Completable deleteAllUsers();
}
