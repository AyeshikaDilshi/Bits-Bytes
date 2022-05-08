package com.itp.walletguard.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "tbl_category", indices = {@Index(value = {"name"},
        unique = true)})
public class CategoryEntity implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "cat_id")
    private Integer categoryID;
    @ColumnInfo(name = "name")
    private String categoryName;
    @ColumnInfo(name = "desc")
    private String desc;
    @ColumnInfo(name = "create_by")
    private Integer createBy;
    @ColumnInfo(name = "create_date")
    private Date createDate;
    @ColumnInfo(name = "cat_type")
    private short type;

    public CategoryEntity() {

    }

    @Ignore
    public CategoryEntity(String categoryName, String desc, Integer createBy, Date createDate, short type) {
        this.categoryName = categoryName;
        this.desc = desc;
        this.createBy = createBy;
        this.createDate = createDate;
        this.type = type;
    }

    protected CategoryEntity(Parcel in) {
        if (in.readByte() == 0) {
            categoryID = null;
        } else {
            categoryID = in.readInt();
        }
        categoryName = in.readString();
        desc = in.readString();
        if (in.readByte() == 0) {
            createBy = null;
        } else {
            createBy = in.readInt();
        }
        type = (short) in.readInt();
    }

    public static final Creator<CategoryEntity> CREATOR = new Creator<CategoryEntity>() {
        @Override
        public CategoryEntity createFromParcel(Parcel in) {
            return new CategoryEntity(in);
        }

        @Override
        public CategoryEntity[] newArray(int size) {
            return new CategoryEntity[size];
        }
    };

    public Integer getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(Integer categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (categoryID == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(categoryID);
        }
        parcel.writeString(categoryName);
        parcel.writeString(desc);
        if (createBy == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(createBy);
        }
        parcel.writeInt((int) type);
    }
}