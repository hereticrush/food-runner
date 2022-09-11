package com.example.food_notes.data.foodpost;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface FoodPostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrUpdate(FoodPost foodPost);

    @Query("SELECT * FROM food_posts ORDER BY post_id ASC")
    Flowable<List<FoodPost>> getAllPosts();

    @Query("SELECT * FROM food_posts WHERE post_id=:postId")
    Flowable<FoodPost> getFoodPostById(Long postId);

    @Query("SELECT img_id FROM food_posts WHERE img_id LIKE :imgId")
    Single<Long> getFoodPostImageId(Long imgId);

    @Query("DELETE FROM food_posts WHERE food_posts.post_id = :id")
    Completable deleteFoodPostById(Long id);

    @Query("DELETE FROM food_posts")
    Completable deleteAllPosts();
}
