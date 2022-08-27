package com.example.food_notes.data.user;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.food_notes.data.relations.UserWithFoodPosts;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrUpdateUser(User user);

    @Query("SELECT * FROM users ORDER BY user_id ASC")
    Flowable<List<User>>getAllUsers();

    @Query("SELECT * FROM users WHERE user_id LIKE :userId")
    Single<User> getUser(Long userId);

    @Query("SELECT * FROM users WHERE username LIKE :username")
    Single<User> getUsername(String username);

    @Transaction
    @Query("SELECT * FROM users JOIN food_posts ON food_posts.user_id WHERE food_posts.user_id=users.user_id AND users.user_id = :userId")
    Flowable<List<UserWithFoodPosts>> getUserWithPosts(Long userId);

    @Delete
    Completable deleteUserByUsername(User user);

    @Delete
    Completable deleteAllUsers(User ... users);
}
