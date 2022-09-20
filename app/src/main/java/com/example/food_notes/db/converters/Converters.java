package com.example.food_notes.db.converters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Date;

public class Converters {

    @TypeConverter
    public static String BitmapToStr(@NonNull Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] b = stream.toByteArray();
        return Base64.getEncoder().encodeToString(b);
    }

    @TypeConverter
    public static Bitmap StrToBitmap(String str) {
        try {
            byte[] b = Base64.getDecoder().decode(str);
            return BitmapFactory.decodeByteArray(
                    b, 0, b.length
            );
        } catch (Exception e) {
            Log.d("CONVERTER_ERROR", "StrToBitmap: "+e.getLocalizedMessage());
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
