package com.example.food_notes.data.picture;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class PictureRepository  {

    private final PictureDao mPictureDao;

    public PictureRepository(PictureDao pictureDao) {
        mPictureDao = pictureDao;
    }


    public Flowable<List<Picture>> getAllData() {
        return mPictureDao.getAllPictures();
    }

    public Completable insertOrUpdateData(Picture picture) {
        return mPictureDao.insertOrUpdate(picture);
    }

    public void deleteAllData() {
        mPictureDao.deleteAll();
    }

    public Single<Picture> getSingleData(Long id) {
        return mPictureDao.getImageById(id);
    }

    public Flowable<List<Picture>> getPictureWithPostId() {
        return mPictureDao.getImageWithPostId();
    }
}
