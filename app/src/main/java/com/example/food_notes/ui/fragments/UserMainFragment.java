package com.example.food_notes.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_notes.R;
import com.example.food_notes.databinding.FragmentUserMainBinding;
import com.example.food_notes.injection.Injection;
import com.example.food_notes.ui.adapters.CustomAdapter;
import com.example.food_notes.ui.adapters.RecyclerViewItemClickListener;
import com.example.food_notes.ui.view.factory.UserViewModelFactory;
import com.example.food_notes.ui.view.model.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

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

    private UserViewModel mUserViewModel;
    private UserViewModelFactory mFactory;

    private BottomNavigationView bottomNavigationView;

    private NavHostFragment navHostFragment;
    private NavController navController;

    public UserMainFragment() {}

    @Nullable
    public static UserMainFragment newInstance(String username, int user_id) {
        UserMainFragment fragment = new UserMainFragment();
        Bundle args = new Bundle();
        args.putString(LOGGED_USER, username);
        args.putInt(LOGGED_USERID, user_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFactory = Injection.provideUserViewModelFactory(requireActivity().getApplicationContext());
        mUserViewModel = mFactory.create(UserViewModel.class);
        navController = NavHostFragment.findNavController(this);

        // experimental ...
        NavBackStackEntry navBackStackEntry = navController.getPreviousBackStackEntry();
        SavedStateHandle savedStateHandle = navBackStackEntry.getSavedStateHandle();
        savedStateHandle.getLiveData(LoginFragment.LOGIN_SUCCESSFUL)
                .observe(navBackStackEntry,(Observer<? super Object>) success -> {
                    if (!success.equals(true)) {
                        int startDestination = navController.getGraph().getStartDestinationId();
                        NavOptions navOptions = new NavOptions.Builder()
                                .setPopUpTo(startDestination, true)
                                .build();
                        navController.navigate(startDestination, null, navOptions);
                    }
                });

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
        bottomNavigationView = binding.bottomNavView;

        // bottom bar navigation setting
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               if (item.getItemId() == R.id.action_add_card_item) {
                    toAddPostFragment();
                    return true;
                } else if (item.getItemId() == R.id.action_settings) {
                    Toast.makeText(requireActivity().getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        //TODO add ui elements to set text to args
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

    private void displayCardItems() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        CustomAdapter mAdapter = new CustomAdapter(getActivity(), mUserViewModel);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerViewItemClickListener(getActivity(), recyclerView, new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(getActivity(), mUserViewModel.getAllUsers().blockingFirst().get(position).getUsername(), Toast.LENGTH_LONG).show();
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

    /**
     * Navigates user to the AddPostFragment
     */
    private void toAddPostFragment() {
        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                .findFragmentById(R.id.main_nav_host_fragment);
        navController = navHostFragment.getNavController();
        navController.navigate(UserMainFragmentDirections.actionUserMainFragmentToAddPostFragment());
    }

}