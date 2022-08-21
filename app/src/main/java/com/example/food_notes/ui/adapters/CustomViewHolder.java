package com.example.food_notes.ui.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_notes.R;

public class CustomViewHolder extends RecyclerView.ViewHolder {

    private final TextView textId, textUsername, textPassword;

    public CustomViewHolder(@NonNull View itemView) {
        super(itemView);
        textId = itemView.findViewById(R.id.row_user_tv1);
        textUsername = itemView.findViewById(R.id.row_user_tv2);
        textPassword = itemView.findViewById(R.id.row_user_tv3);
    }

    public TextView getTextId() {
        return textId;
    }

    public TextView getTextUsername() {
        return textUsername;
    }

    public TextView getTextPassword() {
        return textPassword;
    }
}
