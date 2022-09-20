package com.example.food_notes.data.mapping;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

import com.example.food_notes.data.foodpost.FoodPost;
import com.example.food_notes.data.user.User;

@Entity(
        tableName = "user_and_foodpost_mapping",
        primaryKeys = {
                "user_idMapping",
                "post_idMapping"
        },
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "user_idMapping",
                        onUpdate = ForeignKey.CASCADE,
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = FoodPost.class,
                        parentColumns = "post_id",
                        childColumns = "post_idMapping",
                        onUpdate = ForeignKey.CASCADE,
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class UserAndFoodPostMapping {
    @ColumnInfo(index = true)
    @NonNull
    private String user_idMapping;
    @ColumnInfo(index = true)
    @NonNull
    private Long post_idMapping;

    public UserAndFoodPostMapping() {}

    @Ignore
    public UserAndFoodPostMapping(@NonNull String user_idMapping, @NonNull Long post_idMapping) {
        this.user_idMapping = user_idMapping;
        this.post_idMapping = post_idMapping;
    }

    @NonNull
    public String getUser_idMapping() {
        return user_idMapping;
    }

    public void setUser_idMapping(@NonNull String user_idMapping) {
        this.user_idMapping = user_idMapping;
    }

    @NonNull
    public Long getPost_idMapping() {
        return post_idMapping;
    }

    public void setPost_idMapping(@NonNull Long post_idMapping) {
        this.post_idMapping = post_idMapping;
    }
}
