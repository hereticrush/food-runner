package com.example.food_notes.data.user;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RewriteQueriesToDropUnusedColumns;
import androidx.room.Transaction;

import com.example.food_notes.data.relations.UserWithFoodPosts;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertUser(User user);

    @Query("SELECT * FROM users ORDER BY user_id ASC")
    Flowable<List<User>>getAllUsers();

    @Query("SELECT * FROM users ORDER BY username ASC")
    Flowable<List<User>> getAllUsersByUsername();

    @Query("SELECT * FROM users WHERE user_id LIKE :userId")
    Single<User> getUser(Long userId);

    @Query("SELECT * FROM users WHERE username LIKE :username AND password LIKE :password")
    Single<User> getUsernameAndPassword(String username, String password);

    @Transaction
    @Query("SELECT * FROM users JOIN food_posts ON food_posts.user_id WHERE food_posts.user_id=users.user_id AND users.user_id = :userId")
    @RewriteQueriesToDropUnusedColumns
    Flowable<List<UserWithFoodPosts>> getUserWithPosts(Long userId);

    @Query("DELETE FROM users WHERE username LIKE :username")
    void deleteUserByUsername(String username);

    @Query("DELETE FROM users")
    void deleteAllUsers();
}
