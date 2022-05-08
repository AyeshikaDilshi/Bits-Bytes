package com.itp.walletguard.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.itp.walletguard.entity.UserEntity;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertUser(UserEntity userEntity);

    @Query("SELECT * FROM tbl_user WHERE username=:username")
    public abstract UserEntity getUserByName(String username);
}
