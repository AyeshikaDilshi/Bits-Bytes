package com.itp.walletguard.util;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.itp.walletguard.dao.BankDao;
import com.itp.walletguard.dao.CategoryDao;
import com.itp.walletguard.dao.TransactionDao;
import com.itp.walletguard.dao.UserDao;
import com.itp.walletguard.entity.BankAccountEntity;
import com.itp.walletguard.entity.BnkTransEnt;
import com.itp.walletguard.entity.CategoryEntity;
import com.itp.walletguard.entity.TransactionEntity;
import com.itp.walletguard.entity.UserEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {UserEntity.class, CategoryEntity.class, TransactionEntity.class, BankAccountEntity.class,
        BnkTransEnt.class}
        , version = 1, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class WalletGuardDB extends RoomDatabase {

    public static final int NUMBER_OF_THREAD = 4;
    public static final String DATABASE_NAME = "wallet_guard_db";
    private static volatile WalletGuardDB INSTANCE;
    public static final ExecutorService databaseWriterService = Executors.newFixedThreadPool(NUMBER_OF_THREAD);
    public static final RoomDatabase.Callback sRoomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriterService.execute(() -> {
                //TODO Database Callback Operation Hear
            });
        }
    };

    public static WalletGuardDB getDatabse(final Application context) {
        if (INSTANCE == null) {
            synchronized (WalletGuardDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(), WalletGuardDB.class, DATABASE_NAME
                    ).addCallback(sRoomCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract UserDao userDao();

    public abstract CategoryDao categoryDao();

    public abstract TransactionDao transactionDao();

    public abstract BankDao bankDao();
}