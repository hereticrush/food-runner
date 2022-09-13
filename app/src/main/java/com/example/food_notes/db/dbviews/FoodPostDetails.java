package com.example.food_notes.db.dbviews;

import androidx.room.DatabaseView;

@DatabaseView(
        "SELECT post_id, title, description, rating, sent_at, img_str " +
                "FROM food_posts "
)
public class FoodPostDetails {
    public Long pic_id;
    public Long post_id;
    public String img_str;
}
