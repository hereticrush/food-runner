package com.example.food_notes.data.user;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.food_notes.data.foodpost.FoodPost;

import java.util.Objects;

@Entity(
        tableName = "users",
        primaryKeys = "user_id",
        indices = {
                @Index(value = "user_id", unique = true)
        }
)
public class User {
        @ColumnInfo(name = "user_id") @NonNull private String user_id;
        @ColumnInfo(name = "user_email") private String userEmail;
        @ColumnInfo(name = "user_password") private String userPassword;
        @ColumnInfo(name = "created_at") private String createdAt;

        public User(@NonNull String user_id) {
                this.user_id = user_id;
        }

        @Ignore
        public User(@NonNull String user_id, String userEmail) {
                this.user_id = user_id;
                this.userEmail = userEmail;
        }

        @Ignore
        public User(@NonNull String user_id, String userEmail, String userPassword) {
                this.user_id = user_id;
                this.userEmail = userEmail;
                this.userPassword = userPassword;
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

        public String getUserPassword() {
                return userPassword;
        }

        public void setUserPassword(String userPassword) {
                this.userPassword = userPassword;
        }

        public String getCreatedAt() {
                return createdAt;
        }

        public void setCreatedAt(String createdAt) {
                this.createdAt = createdAt;
        }

}
