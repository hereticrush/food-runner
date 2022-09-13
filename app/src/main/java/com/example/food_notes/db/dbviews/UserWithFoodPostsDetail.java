package com.example.food_notes.db.dbviews;

import androidx.room.DatabaseView;

@DatabaseView(
        "SELECT user_id, username, food_posts.post_id, food_posts.sent_at AS pid FROM users " +
                "INNER JOIN food_posts ON users.user_id=food_posts.user_id"
)
public class UserWithFoodPostsDetail {
    long pid;
    int user_id;
    String username;
    String sent_at;
}
