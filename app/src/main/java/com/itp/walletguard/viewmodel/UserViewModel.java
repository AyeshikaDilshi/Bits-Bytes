package com.itp.walletguard.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.itp.walletguard.entity.UserEntity;
import com.itp.walletguard.repository.UserRepo;

public class UserViewModel extends AndroidViewModel {
    private static final String TAG = "UserViewModel";
    private final UserRepo userRepo;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepo = new UserRepo(application);
    }

    public void insertUser(UserEntity userEntity) {
        Log.d(TAG, "<-------- Execute Insert User In User ViewModel ---->Username: " + userEntity.getUserName());
        userRepo.insertUser(userEntity);
    }

    public UserEntity getUserByName(final String username) {
        Log.d(TAG, "<--- Execute Get User By Username In User View model ----> " + username);
        return userRepo.getUserByName(username);
    }
}