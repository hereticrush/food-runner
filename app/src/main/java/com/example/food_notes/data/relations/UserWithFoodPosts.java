package com.example.food_notes.data.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.food_notes.data.foodpost.FoodPost;
import com.example.food_notes.data.user.User;

import java.util.List;

public class UserWithFoodPosts {
    @Embedded public User user1;
    @Relation(
            parentColumn = "user_id",
            entityColumn = "user_id",
            entity = FoodPost.class
    )
    public List<FoodPost> foodPostList;
}
