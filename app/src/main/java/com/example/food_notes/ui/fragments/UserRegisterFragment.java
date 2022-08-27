package com.example.food_notes.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.food_notes.R;
import com.example.food_notes.databinding.FragmentUserRegisterBinding;
import com.example.food_notes.injection.Injection;
import com.example.food_notes.ui.view.ApiRegister;
import com.example.food_notes.ui.view.UserViewModel;
import com.example.food_notes.ui.view.UserViewModelFactory;
import com.example.food_notes.ui.view.model.RegisterViewModel;
import com.example.food_notes.ui.view.util.regex.RegexValidation;
import com.example.food_notes.ui.view.util.text.CustomToastMessage;

import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserRegisterFragment extends Fragment implements ApiRegister {

    private FragmentUserRegisterBinding binding;
    private RegisterViewModel mRegisterViewModel;
    private CustomToastMessage toaster;

    public UserRegisterFragment() {}

    public static UserRegisterFragment getInstance() { return new UserRegisterFragment(); }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this.getActivity(), R.layout.fragment_user_register);
        mRegisterViewModel = new ViewModelProvider(this.getActivity()).get(RegisterViewModel.class);
        binding.setRegViewModel(mRegisterViewModel);
        mRegisterViewModel.getApiRegister().onHold();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void toLoginActivity() {
        //Bundle bundle = new Bundle();
        //bundle.putString("USERNAME", binding);
        //bundle.putString("PASSWORD", Objects.requireNonNull(editTextPassword.getText()).toString().trim());
        LoginFragment fragment = LoginFragment.getInstance(); //this can get params from previous fragment and pass the data
        //fragment.setArguments(bundle);
        FragmentManager manager = getParentFragmentManager();
        manager.beginTransaction().setReorderingAllowed(true).replace(
                R.id.fragmentContainerView, fragment, null
        ).addToBackStack("register").commit();
    }

    private void addUserToDatabase() {
        /*mDisposable.add(mViewModel.updateUsername(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(getActivity(), "Successfully added user.", Toast.LENGTH_SHORT).show();
                        toLoginActivity();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "Registration failed.", Toast.LENGTH_SHORT).show();
                    }
                }));*/
    }

    @Override
    public void onHold() {

    }

    @Override
    public void onSuccess() {
        toaster.toast("Registered successfully.");
        toLoginActivity();
    }

    @Override
    public void onFailed(String log) {
        toaster.toast(log);
    }
}