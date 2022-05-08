package com.itp.walletguard.util;

import android.app.Application;

import com.itp.walletguard.entity.UserEntity;

public class WalletGuardApp extends Application {
    public static UserEntity userEntity;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public static UserEntity getUserEntity() {
        return userEntity;
    }

    public static void setUserEntity(UserEntity userEntity) {
        WalletGuardApp.userEntity = userEntity;
    }
}