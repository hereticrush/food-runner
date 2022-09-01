package com.example.food_notes.data.user;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(
        tableName = "users",
        indices = {
                @Index(value = "user_id", unique = true),
                @Index(value = "username", unique = true),
        }
)
public class User {
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "user_id") private int user_id = 0;
        @ColumnInfo(name = "username") private String username;
        @ColumnInfo(name = "password") private String password;
        @ColumnInfo(name = "created_at") private String created_at;

        public User() {}

        @Ignore
        public User(String username) {
                this.username = username;
        }

        @Ignore
        public User(int user_id, String username, String password) {
                this.user_id = user_id;
                this.username = username;
                this.password = password;
        }

        @Ignore
        public User(String username, String password) {
                this.username = username;
                this.password = password;
        }

        @Ignore
        public User(int user_id, String username, String password, String created_at) {
                this.user_id = user_id;
                this.username = username;
                this.password = password;
                this.created_at = created_at;
        }

        public int getUser_id() {
                return user_id;
        }

        public void setUser_id(int user_id) {
                this.user_id = user_id;
        }

        public String getUsername() {
                return username;
        }

        public void setUsername(String username) {
                this.username = username;
        }

        public String getPassword() {
                return password;
        }

        public void setPassword(String password) {
                this.password = password;
        }

        public String getCreated_at() {
                return created_at;
        }

        public void setCreated_at(String created_at) {
                this.created_at = created_at;
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof User)) return false;
                User user = (User) o;
                return getUser_id() == user.getUser_id() && Objects.equals(getUsername(), user.getUsername()) && Objects.equals(getPassword(), user.getPassword()) && Objects.equals(getCreated_at(), user.getCreated_at());
        }

        @Override
        public int hashCode() {
                return Objects.hash(getUser_id(), getUsername(), getPassword(), getCreated_at());
        }
}
