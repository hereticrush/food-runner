package com.example.food_notes.ui.fragments;

import android.animation.Animator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
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
import kotlinx.coroutines.flow.StateFlow;

/**
 * Fragment structure that has user card items, provide functionalities
 * such as adding items and removing
 * main functionality point for
 * application
 */
public class UserMainFragment extends Fragment {

    private static final String LOGGED_USERID = "LOGGED_USERID";
    private static final boolean LOGIN_STATE = true;
    private static int USER_ID;

    // view binding
    private FragmentUserMainBinding binding;

    private RecyclerView recyclerView;

    private final CompositeDisposable disposable = new CompositeDisposable();

    // view models
    private UserViewModel mUserViewModel;
    private UserViewModelFactory mFactory;

    // bottom nav view
    private BottomNavigationView bottomNavigationView;

    // navigation components
    private NavHostFragment navHostFragment;
    private NavController navController;
    private NavBackStackEntry navBackStackEntry;
    private SavedStateHandle savedStateHandle;

    public UserMainFragment() {}

    @Nullable
    public static UserMainFragment newInstance(int user_id) {
        UserMainFragment fragment = new UserMainFragment();
        Bundle args = new Bundle();
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
        navBackStackEntry = navController.getPreviousBackStackEntry();
        savedStateHandle = navBackStackEntry.getSavedStateHandle();
        /*savedStateHandle.getLiveData(LoginFragment.LOGIN_SUCCESSFUL)
                .observe(navBackStackEntry,(Observer<? super Object>) success -> {
                    if (!success.equals()) {
                        int startDestination = navController.getGraph().getStartDestinationId();
                        NavOptions navOptions = new NavOptions.Builder()
                                .setPopUpTo(startDestination, true)
                                .build();
                        navController.navigate(startDestination, null, navOptions);
                    }
                });*/

        // user id
        StateFlow<Integer> stateFlow_userId = savedStateHandle
                .getStateFlow(LoginFragment.USER_ID, USER_ID);
        System.out.println("user_main_flow id:"+stateFlow_userId.getValue());
        try {
            setLoggedUserid(stateFlow_userId.getValue());
        } catch (Exception e) {
            setLoggedUserid(navBackStackEntry.getArguments().getInt(LoginFragment.USER_ID));
            Log.e("ERROR", e.getLocalizedMessage());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserMainBinding.inflate(inflater, container, false);

        bottomNavigationView = binding.bottomNavView;
        // bottom bar navigation setting
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // clicking on items on bottomNavView
               if (item.getItemId() == R.id.action_add_card_item) {
                    toAddPostFragment(getUserId());
                   Log.d("ADD-CARD", "id:"+getUserId());
                    return true;
                } else if (item.getItemId() == R.id.action_settings) {
                    Toast.makeText(requireActivity().getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (item.getItemId() == R.id.action_logout_user) {
                   navBackStackEntry = navController.getCurrentBackStackEntry();
                   int destination = navController.getGraph().getStartDestinationId();
                   NavOptions navOptions = new NavOptions.Builder().setPopUpTo(destination, true, true).build();
                   navController.navigate(destination, null, navOptions);
                   savedStateHandle.set(LoginFragment.LOGIN_SUCCESSFUL, false);
                   return true;
               }
                return false;
            }
        });

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
    private void toAddPostFragment(final int id) {
        Bundle args = new Bundle();
        args.putInt(LOGGED_USERID, id);
        args.putBoolean("LOGGED_IN", isLoginState());
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.addPostFragment, true, true)
                        .build();
        navController.navigate(R.id.addPostFragment, args, navOptions);
    }

    public static boolean isLoginState() {
        return LOGIN_STATE;
    }

    public static int getUserId() {
        return USER_ID;
    }

    public void setLoggedUserid(int id) {
        UserMainFragment.USER_ID = id;
    }
}