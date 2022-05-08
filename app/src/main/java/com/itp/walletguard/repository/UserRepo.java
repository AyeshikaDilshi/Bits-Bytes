package com.itp.walletguard.repository;

import android.app.Application;
import android.util.Log;

import com.itp.walletguard.dao.UserDao;
import com.itp.walletguard.entity.UserEntity;
import com.itp.walletguard.util.WalletGuardDB;

public class UserRepo {
    private static final String TAG = "UserRepo";
    private final UserDao userDao;

    public UserRepo(Application application) {
        userDao = WalletGuardDB.getDatabse(application).userDao();
    }

    public void insertUser(UserEntity userEntity) {
        Log.d(TAG, "<-------- Execute Insert User In User Repo ---->Username: " + userEntity.getUserName());
        userDao.insertUser(userEntity);
    }

    public UserEntity getUserByName(final String username) {
        Log.d(TAG, "<--- Execute Get User By Username In User repo ----> " + username);
        return userDao.getUserByName(username);
    }
}