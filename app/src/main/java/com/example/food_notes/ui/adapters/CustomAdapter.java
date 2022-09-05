package com.example.food_notes.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_notes.R;
import com.example.food_notes.data.user.User;
import com.example.food_notes.ui.view.model.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomViewHolder> {

    private final Context mContext;
    private final UserViewModel mViewModel;

    public CustomAdapter(Context mContext, UserViewModel viewModel) {
        this.mContext = mContext;
        this.mViewModel = viewModel;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(mContext).inflate(R.layout.row_user_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        List<User> users = new ArrayList<>(mViewModel.getAllUsers().blockingFirst());
        holder.getTextId().setText(String.valueOf(users.get(position).getUser_id()));
        holder.getTextUsername().setText(users.get(position).getUsername());
        holder.getTextPassword().setText(users.get(position).getPassword());
    }


    @Override
    public int getItemCount() {
        //TODO make this function work
        List<User> users = mViewModel.getAllUsers().blockingFirst();
        return users.size();
    }
}
