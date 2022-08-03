package com.example.food_notes.data.picture;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.food_notes.data.foodpost.FoodPost;

@Entity(
        tableName = "image_table",
        foreignKeys = {
                @ForeignKey(
                        entity = FoodPost.class,
                        parentColumns = {"img_id"},
                        childColumns = {"img_id"},
                        onUpdate = ForeignKey.CASCADE,
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class Picture {

    @PrimaryKey(autoGenerate = true)
    private Long img_id;

    @ColumnInfo(name = "post_id") private Long post_id;

    @ColumnInfo(name = "uri_string") private String uri_string;

    public Picture() {}

    @Ignore
    public Picture(Long post_id, String uri_string) {
        this.post_id = post_id;
        this.uri_string = uri_string;
        img_id = 0L;
    }

    public void setImg_id(Long img_id) {
        this.img_id = img_id;
    }

    public Long getImg_id() {
        return img_id;
    }

    public Long getPost_id() {
        return post_id;
    }

    public void setPost_id(Long post_id) {
        this.post_id = post_id;
    }

    public String getUri_string() {
        return uri_string;
    }

    public void setUri_string(String uri_string) {
        this.uri_string = uri_string;
    }
}
