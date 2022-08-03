package com.example.food_notes.data.picture;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface PictureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrUpdate(Picture picture);

    @Query("SELECT * FROM image_table")
    Flowable<List<Picture>> getAllPictures();

    @Query("SELECT * FROM image_table WHERE img_id=:picId")
    Single<Picture> getImageById(long picId);

    @Query("SELECT image_table.img_id, food_posts.img_id, image_table.uri_string FROM image_table INNER JOIN food_posts ON food_posts.post_id WHERE food_posts.post_id=image_table.post_id")
    Flowable<List<Picture>> getImageWithPostId();

    @Query("DELETE FROM image_table")
    void deleteAll();
}
