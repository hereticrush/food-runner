package com.example.food_notes.data.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.food_notes.data.foodpost.FoodPost;
import com.example.food_notes.data.user.User;

import java.util.ArrayList;
import java.util.List;


public class UserWithFoodPosts {
    @Embedded public User user;
    @Embedded public ArrayList<FoodPost> list;

    public UserWithFoodPosts(User user, ArrayList<FoodPost> list) {
        this.user = user;
        this.list = list;
    }
}
