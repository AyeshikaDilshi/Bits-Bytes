package com.itp.walletguard.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "tbl_user")
public class UserEntity {
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    private Integer userId;
    @ColumnInfo(name = "username")
    private String userName;
    @ColumnInfo(name = "password")
    private String password;
    @ColumnInfo(name = "email")
    private String email;
    @ColumnInfo(name = "create_date")
    private Date createDate;
    @ColumnInfo(name = "user_state")
    private short userState;

    public UserEntity() {

    }

    @Ignore
    public UserEntity(String userName, String password, String email, Date createDate, short userState) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.createDate = createDate;
        this.userState = userState;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public short getUserState() {
        return userState;
    }

    public void setUserState(short userState) {
        this.userState = userState;
    }
}