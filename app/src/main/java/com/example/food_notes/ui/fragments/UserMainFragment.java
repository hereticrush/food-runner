package com.example.food_notes.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_notes.R;
import com.example.food_notes.databinding.FragmentUserMainBinding;
import com.example.food_notes.ui.adapters.CustomAdapter;
import com.example.food_notes.ui.adapters.RecyclerViewItemClickListener;
import com.example.food_notes.ui.view.model.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

/**
 * Fragment structure that has user card items, provide functionalities
 * such as adding items and removing
 * main functionality point for
 * application
 */
public class UserMainFragment extends Fragment {

    private static final String LOGGED_USER = "LOGGED_USER";
    private static final String LOGGED_USERID = "LOGGED_USERID";
    private static final String TAG = "main";
    private FragmentUserMainBinding binding;
    private RecyclerView recyclerView;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private UserViewModel mViewModel;
    private BottomNavigationView bottomNavigationView;

    public UserMainFragment() {}

    public static UserMainFragment newInstance(String username, int user_id) {
        UserMainFragment fragment = new UserMainFragment();
        Bundle args = new Bundle();
        args.putString(LOGGED_USER, username);
        args.putInt(LOGGED_USERID, user_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserMainBinding.inflate(inflater, container, false);
        if (getArguments() != null) {
            String usr = getArguments().getString(LOGGED_USER);
            int usr_id = getArguments().getInt(LOGGED_USERID);
        }
        //TODO add ui elements to set text to args
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Override
    public void onStop() {
        disposable.clear();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // avoid memory leak
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
        requireActivity().getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(
                R.id.userMainFragment, AddPostFragment.getInstance(), null
        ).addToBackStack(TAG).commit();
    }

}