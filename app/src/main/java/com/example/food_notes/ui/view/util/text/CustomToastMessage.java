package com.example.food_notes.ui.view.util.text;

import android.content.Context;
import android.widget.Toast;

public class CustomToastMessage {

    private Context mContext;

    public void toast(String message) {
        Toast.makeText(this.mContext, message, Toast.LENGTH_SHORT).show();
    }
}
