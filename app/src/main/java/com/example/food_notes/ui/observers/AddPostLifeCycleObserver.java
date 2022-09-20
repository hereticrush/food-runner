package com.example.food_notes.ui.observers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.food_notes.BuildConfig;
import com.example.food_notes.ui.fragments.AddPostFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class AddPostLifeCycleObserver implements DefaultLifecycleObserver {
    private static final String TAG = "addPostObserver";
    private static final int GALLERY = 188;
    private final ActivityResultRegistry mRegistry;
    private ActivityResultLauncher<String> mGetContent;
    private ActivityResultLauncher<Uri> mTakePicture;
    private ActivityResultLauncher<String[]> mOpenDocument;
    private ActivityResultLauncher<Void> mPreviewImage;
    private ActivityResultLauncher<String> mGalleryPermission;
    private final Context mContext;
    private final MutableLiveData<Bitmap> mBitmapLiveData = new MutableLiveData<>();
    private Uri mUri;

    public AddPostLifeCycleObserver(@NonNull ActivityResultRegistry registry, Context context) {
        mRegistry = registry;
        mContext = context;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onCreate(owner);

        mGalleryPermission = mRegistry.register("permission_gallery", owner,
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result) {
                        mGetContent.launch("image/*");
                        mGetContent.getContract().getSynchronousResult(mContext.getApplicationContext(), "image/*");
                        Log.d(TAG, "onCreate: FromObserverPermissions:RESULT?");
                    } else {
                        Log.d(TAG, "onCreate: FromObserverPermissions:FAILED_RESULT");
                    }
                });

        mGetContent = mRegistry.register("content", owner,
                new ActivityResultContracts.GetContent(),
                resultUri -> {
                if (resultUri != null) {
                    mUri = Uri.parse(resultUri.getEncodedPath());
                    Log.d(TAG, "fromObserverGetContent:"+resultUri.getPath());
                    Log.d("FromObs_URI", "mUri?:" + mUri.getPath());
                }
                });

        mTakePicture = mRegistry.register("picture", owner,
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (!success) {
                        return;
                    }
                    mContext.grantUriPermission(
                            "com.example.food_notes",
                            mUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    );
                    Log.d(TAG, "onCreate: success_from_observer_uri:");
                });

        mPreviewImage = mRegistry.register("preview", owner,
                new ActivityResultContracts.TakePicturePreview(),
                new ActivityResultCallback<Bitmap>() {
                    @Override
                    public void onActivityResult(Bitmap result) {
                        mBitmapLiveData.setValue(result);
                        Log.d(TAG, "onActivityResult: LIVEDATA?");
                    }
                });

        mOpenDocument = mRegistry.register("document", owner,
                new ActivityResultContracts.OpenDocument(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            mUri = Uri.parse(result.getEncodedPath());
                            Log.d(TAG, "From_Observer:"+result.getPath());
                            Log.d(TAG, "FromObserverParsing"+mUri.getPath());
                        }
                    }
                });

    }

    public void askForStoragePermission() {
        mGalleryPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public void previewImage() {
        mPreviewImage.launch(null);
    }


    public void getImageDocument() {
        mOpenDocument.launch(new String[]{"image/*"});
    }

    public void takePicture() {mTakePicture.launch(mUri);}

    public ActivityResultRegistry getRegistry() {
        return mRegistry;
    }

    public MutableLiveData<Bitmap> getBitmapLiveData() {
        return mBitmapLiveData;
    }
}
