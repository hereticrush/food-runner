package com.example.food_notes.data.foodpost;

import com.example.food_notes.data.relations.FoodPostAndPicture;

import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class FoodPostRepository implements FoodPostDataSource{

    private final FoodPostDao mFoodPostDao;

    public FoodPostRepository(FoodPostDao foodPostDao) {
        mFoodPostDao = foodPostDao;
    }

    @Override
    public Flowable<List<FoodPost>> getAllData() {
        return mFoodPostDao.getAllPosts();
    }

    @Override
    public Completable insertOrUpdateData(FoodPost foodPost) {
        return mFoodPostDao.insertOrUpdate(foodPost);
    }

    @Override
    public Flowable<FoodPost> getFoodPost(Long id) {
        return mFoodPostDao.getFoodPostById(id);
    }

    @Override
    public void deleteAllData() {
        mFoodPostDao.deleteAllPosts();
    }

    public void deleteFoodPostById(Long id) {
        mFoodPostDao.deleteFoodPostById(id);
    }

    @Override
    public Flowable<List<FoodPostAndPicture>> getPostsAndPictures() {
        return mFoodPostDao.getPostsAndPictures();
    }

    public Single<FoodPostAndPicture> getFoodPostAndPictureById(Long id) {
        return mFoodPostDao.getFoodPostAndPictureById(id);
    }

    public Flowable<List<FoodPostAndPicture>> getFoodAndImage() {
        return mFoodPostDao.getFoodAndPicture();
    }

    @Override
    public Single<Date> getDateById(Long id) {
        return mFoodPostDao.getDateByPostId(id);
    }

}
