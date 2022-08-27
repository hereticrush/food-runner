package com.example.food_notes.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.food_notes.R;
import com.example.food_notes.injection.Injection;
import com.example.food_notes.ui.adapters.CustomAdapter;
import com.example.food_notes.ui.adapters.RecyclerViewItemClickListener;
import com.example.food_notes.ui.view.UserViewModel;
import com.example.food_notes.ui.view.UserViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class UserMainFragment extends Fragment {

    private static final String LOGGED_USER = "LOGGED_USER";
    private static final String LOGGED_PASSWORD = "LOGGED_PASSWORD";
    private static final String TAG = "main";
    private RecyclerView recyclerView;
    private UserViewModel mViewModel;

    private UserMainFragment() {}

    public static UserMainFragment getInstance() {
        return new UserMainFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_main, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserViewModelFactory mFactory = Injection.provideViewModelFactory(getActivity());
        mViewModel = new ViewModelProvider(this, mFactory).get(UserViewModel.class);
        if (getArguments() != null) {
            String usr = getArguments().getString(LOGGED_USER);
            String pwd = getArguments().getString(LOGGED_PASSWORD);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fab = view.findViewById(R.id.fab_delete_post);
        fab.setOnClickListener(v -> backToLoginFragment());
        recyclerView = view.findViewById(R.id.rv_user_main);
        displayUsers();

        FloatingActionButton fab_add = view.findViewById(R.id.fab_add_post);
        fab_add.setOnClickListener(f -> toAddPostFragment());

    }

    private void displayUsers() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        CustomAdapter mAdapter = new CustomAdapter(getActivity(), mViewModel);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerViewItemClickListener(getActivity(), recyclerView, new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(getActivity(), mViewModel.getAllUsers().blockingFirst().get(position).getUsername(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );
    }

    private void displayCardView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void backToLoginFragment() {
        FragmentManager manager = getParentFragmentManager();
        manager.popBackStack("login", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void toAddPostFragment() {
        FragmentManager manager = getParentFragmentManager();
        manager.beginTransaction().setReorderingAllowed(true).replace(
                R.id.fragmentContainerView, AddPostFragment.getInstance(), null
        ).addToBackStack(TAG).commit();
    }

}