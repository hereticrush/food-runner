package com.example.food_notes.db.dbviews;

import androidx.room.DatabaseView;

@DatabaseView("SELECT * FROM users")
public class UserDetails {
    public Integer user_id;
    public String username;
    public String password;
}
