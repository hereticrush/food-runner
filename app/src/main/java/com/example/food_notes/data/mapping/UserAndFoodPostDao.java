package com.example.food_notes.data.mapping;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RewriteQueriesToDropUnusedColumns;
import androidx.room.RoomWarnings;
import androidx.room.Transaction;

import com.example.food_notes.data.foodpost.FoodPost;
import com.example.food_notes.data.relations.UserWithFoodPosts;
import com.example.food_notes.data.user.User;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;

@Dao
public interface UserAndFoodPostDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertUser(User user);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Maybe<Long> insertFoodPost(FoodPost foodPost);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM users JOIN user_and_foodpost_mapping ON users.user_id = user_idMapping JOIN food_posts ON post_id = post_idMapping")
    Flowable<List<UserWithFoodPosts>> getUserWithFoodPosts();

    @Query("DELETE FROM user_and_foodpost_mapping")
    Completable deleteMappingData();

    @Query("DELETE FROM users")
    Completable deleteUsers();

    @Query("DELETE FROM food_posts")
    Completable deleteFoodPosts();

}
