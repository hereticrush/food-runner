package com.example.food_notes.data.foodpost;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.food_notes.data.user.User;

@Entity(
        tableName = "food_posts",
        indices = {
                @Index(value = "post_id", unique = true),
                @Index(value = "user_id", unique = true),
                @Index(value = "img_id", unique = true),
        },
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "post_id",
                        onUpdate = ForeignKey.CASCADE,
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class FoodPost {
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "post_id") private Long post_id = 0L;
        @ColumnInfo(name = "user_id") private Long user_id;
        @ColumnInfo(name = "img_id") private Long img_id = 0L;
        @ColumnInfo(name = "title") private String title;
        @ColumnInfo(name = "description") private String description;
        @ColumnInfo(name = "rating") private float rating;
        @ColumnInfo(name = "sent_at") private String sent_at;
        @ColumnInfo(name = "latitude") private Double latitude;
        @ColumnInfo(name = "longitude") private Double longitude;

        public FoodPost() {}

        @Ignore
        public FoodPost(String title, String description, float rating, String sent_at) {
                this.sent_at = sent_at;
                this.title = title;
                this.description = description;
                this.rating = rating;
        }

        @Ignore
        public FoodPost(Long post_id, Long user_id, Long img_id, String title, String description, float rating) {
                this.post_id = post_id;
                this.user_id = user_id;
                this.img_id = img_id;
                this.title = title;
                this.description = description;
                this.rating = rating;
        }

        @Ignore
        public FoodPost(Long post_id, Long user_id, Long img_id, String title, String description, float rating, String sent_at, Double latitude, Double longitude) {
                this.post_id = post_id;
                this.user_id = user_id;
                this.img_id = img_id;
                this.title = title;
                this.description = description;
                this.rating = rating;
                this.sent_at = sent_at;
                this.latitude = latitude;
                this.longitude = longitude;
        }

        public Long getPost_id() {
                return post_id;
        }

        public void setPost_id(Long post_id) {
                this.post_id = post_id;
        }

        public Long getUser_id() {
                return user_id;
        }

        public void setUser_id(Long user_id) {
                this.user_id = user_id;
        }

        public Long getImg_id() {
                return img_id;
        }

        public void setImg_id(Long img_id) {
                this.img_id = img_id;
        }

        public String getTitle() {
                return title;
        }

        public void setTitle(String title) {
                this.title = title;
        }

        public String getDescription() {
                return description;
        }

        public void setDescription(String description) {
                this.description = description;
        }

        public float getRating() {
                return rating;
        }

        public void setRating(float rating) {
                this.rating = rating;
        }

        public String getSent_at() {
                return sent_at;
        }

        public void setSent_at(String sent_at) {
                this.sent_at = sent_at;
        }

        public Double getLatitude() {
                return latitude;
        }

        public void setLatitude(Double latitude) {
                this.latitude = latitude;
        }

        public Double getLongitude() {
                return longitude;
        }

        public void setLongitude(Double longitude) {
                this.longitude = longitude;
        }
}
