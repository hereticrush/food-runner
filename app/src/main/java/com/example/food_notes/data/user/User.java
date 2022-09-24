package com.example.food_notes.data.user;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.food_notes.data.foodpost.FoodPost;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

public class User implements Serializable {

        @NonNull private String user_id;
        private String userEmail;
        @ServerTimestamp
        private String createdAt;

        public User() {}

        public User(@NonNull String user_id, String userEmail) {
                this.user_id = user_id;
                this.userEmail = userEmail;
        }

        public User(@NonNull String user_id, String userEmail, String createdAt) {
                this.user_id = user_id;
                this.userEmail = userEmail;
                this.createdAt = createdAt;
        }

        @NonNull
        public String getUser_id() {
                return user_id;
        }

        public void setUser_id(@NonNull String user_id) {
                this.user_id = user_id;
        }

        public String getUserEmail() {
                return userEmail;
        }

        public void setUserEmail(String userEmail) {
                this.userEmail = userEmail;
        }

        public String getCreatedAt() {
                return createdAt;
        }

        public void setCreatedAt(String createdAt) {
                this.createdAt = createdAt;
        }

        @Exclude
        public HashMap<String, Object> getUserMap() {
                HashMap<String, Object> data = new HashMap<>();
                data.put("user_id", getUser_id());
                data.put("user_email", getUserEmail());
                data.put("created_at", FieldValue.serverTimestamp());
                return data;
        }
}
