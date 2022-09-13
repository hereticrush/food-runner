package com.example.food_notes.db.converters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Date;

public class Converters {

    @TypeConverter
    public static String BitmapToStr(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] b = stream.toByteArray();
        String temp = Base64.getEncoder().encodeToString(b);
        return temp;
    }

    @TypeConverter
    public static Bitmap StrToBitmap(String str) {
        try {
            byte[] b = Base64.getDecoder().decode(str);
            return BitmapFactory.decodeByteArray(
                    b, 0, b.length
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long fromDateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }


}
