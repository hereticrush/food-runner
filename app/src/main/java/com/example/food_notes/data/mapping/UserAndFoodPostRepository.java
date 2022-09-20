package com.example.food_notes.data.mapping;

import com.example.food_notes.data.foodpost.FoodPost;
import com.example.food_notes.data.relations.UserWithFoodPosts;
import com.example.food_notes.data.user.User;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;

public class UserAndFoodPostRepository implements UserAndFoodPostDataSource{

    private final UserAndFoodPostDao mUserAndFoodPostDao;

    public UserAndFoodPostRepository(UserAndFoodPostDao allDao) {
        mUserAndFoodPostDao = allDao;
    }

    @Override
    public Flowable<List<UserWithFoodPosts>> getAllData() {
        return mUserAndFoodPostDao.getUserWithFoodPosts();
    }

    /*@Override
    public Maybe<User> getUser(int id) {
        return mUserAndFoodPostDao.getUserWithFoodPosts()
                .flatMap(Flowable::fromIterable)
                .singleElement().flatMap(userWithFoodPosts -> userWithFoodPosts.user)
                .map(o -> Flowable.just(o));
    }*/
    @Override
    public Completable deleteAllData() {
        return mUserAndFoodPostDao.deleteUsers();
    }

    @Override
    public Completable insertUser(User user) {
        return mUserAndFoodPostDao.insertUser(user);
    }

    @Override
    public Maybe<Long> insertFoodPost(FoodPost foodPost) {
        return mUserAndFoodPostDao.insertFoodPost(foodPost);
    }
}
