package com.example.food_notes.data.foodpost;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;


public class FoodPost implements Serializable {
        private String user_id;
        private String image_uri;
        private String title;
        private String description;
        private float rating;
        @ServerTimestamp
        private Date sent_at;
        private Double latitude;
        private Double longitude;

        public FoodPost() {}

        public FoodPost(String user_id, String img_str, String title, String description, float rating, Double latitude, Double longitude) {
                this.user_id = user_id;
                this.image_uri = img_str;
                this.title = title;
                this.description = description;
                this.rating = rating;
                this.latitude = latitude;
                this.longitude = longitude;
        }

        public FoodPost(String user_id, String img_str, String title, String description, float rating, Date sent_at, Double latitude, Double longitude) {
                this.user_id = user_id;
                this.image_uri = img_str;
                this.title = title;
                this.description = description;
                this.rating = rating;
                this.sent_at = sent_at;
                this.latitude = latitude;
                this.longitude = longitude;
        }

        public String getUser_id() {
                return user_id;
        }

        public void setUser_id(String user_id) {
                this.user_id = user_id;
        }

        public String getImage_uri() {
                return image_uri;
        }

        public void setImage_uri(String image_uri) {
                this.image_uri = image_uri;
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

        public Date getSent_at() {
                return sent_at;
        }

        public void setSent_at(Date sent_at) {
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

        @Exclude
        public HashMap<String, Object> getFoodPostMap() {
                HashMap<String, Object> data = new HashMap<>();
                data.put("user_id", getUser_id());
                data.put("image_uri", getImage_uri());
                data.put("title", getTitle());
                data.put("description", getDescription());
                data.put("rating", getRating());
                data.put("sent_at", FieldValue.serverTimestamp());
                data.put("latitude", getLatitude());
                data.put("longitude", getLongitude());
                return data;
        }
}
