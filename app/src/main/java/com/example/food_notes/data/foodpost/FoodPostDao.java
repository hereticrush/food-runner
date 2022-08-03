package com.example.food_notes.data.foodpost;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.food_notes.data.relations.FoodPostAndPicture;

import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface FoodPostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrUpdate(FoodPost foodPost);

    @Transaction
    @Query("SELECT * FROM food_posts WHERE post_id=:postId")
    Single<FoodPostAndPicture> getFoodPostAndPictureById(long postId);

    @Query("SELECT * FROM food_posts ORDER BY post_id ASC")
    Flowable<List<FoodPost>> getAllPosts();

    @Transaction
    @Query("SELECT food_posts.user_id, food_posts.post_id, image_table.img_id, date, title, description, rating, latitude, longitude FROM food_posts INNER JOIN image_table ON " +
            "food_posts.img_id WHERE food_posts.img_id=image_table.img_id")
    Flowable<List<FoodPostAndPicture>> getPostsAndPictures();

    @Query("SELECT * FROM food_posts WHERE post_id=:postId")
    Flowable<FoodPost> getFoodPostById(Long postId);

    @Query("SELECT date FROM food_posts WHERE post_id=:id")
    Single<Date> getDateByPostId(Long id);

    @Query("DELETE FROM food_posts WHERE food_posts.post_id = :id")
    void deleteFoodPostById(long id);

    @Query("DELETE FROM food_posts")
    void deleteAllPosts();

    @Transaction
    @Query("SELECT * FROM food_posts, image_table WHERE food_posts.img_id=image_table.img_id ORDER BY food_posts.post_id")
    Flowable<List<FoodPostAndPicture>> getFoodAndPicture();
}
