package com.example.food_notes.data.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.food_notes.data.foodpost.FoodPost;
import com.example.food_notes.data.picture.Picture;

public class FoodPostAndPicture {
    @Embedded public FoodPost foodPost;
    @Relation(
            parentColumn = "img_id",
            entityColumn = "img_id"
    )
    public Picture picture;
}
