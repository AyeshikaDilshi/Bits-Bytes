package com.itp.walletguard.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.math.BigDecimal;
import java.util.Date;

@Entity(tableName = "tbl_transaction")
public class TransactionEntity implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "transaction_id")
    private Integer transactionId;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "desc")
    private String desc;
    @ColumnInfo(name = "value")
    private BigDecimal value;
    @ColumnInfo(name = "tra_cat_id")
    private Integer categoryID;
    @ColumnInfo(name = "tra_type")
    private short categoryType;
    @ColumnInfo(name = "create_date")
    private Date transactionDate;
    @ColumnInfo(name = "create_by")
    private Integer createBy;

    public TransactionEntity() {
    }

    @Ignore
    public TransactionEntity(String name, String desc, BigDecimal value,
                             Integer categoryID, short categoryType, Date transactionDate, Integer createBy) {
        this.name = name;
        this.desc = desc;
        this.value = value;
        this.categoryID = categoryID;
        this.categoryType = categoryType;
        this.transactionDate = transactionDate;
        this.createBy = createBy;
    }

    protected TransactionEntity(Parcel in) {
        if (in.readByte() == 0) {
            transactionId = null;
        } else {
            transactionId = in.readInt();
        }
        name = in.readString();
        desc = in.readString();
        if (in.readByte() == 0) {
            categoryID = null;
        } else {
            categoryID = in.readInt();
        }
        categoryType = (short) in.readInt();
        if (in.readByte() == 0) {
            createBy = null;
        } else {
            createBy = in.readInt();
        }
    }

    public static final Creator<TransactionEntity> CREATOR = new Creator<TransactionEntity>() {
        @Override
        public TransactionEntity createFromParcel(Parcel in) {
            return new TransactionEntity(in);
        }

        @Override
        public TransactionEntity[] newArray(int size) {
            return new TransactionEntity[size];
        }
    };

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Integer getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(Integer categoryID) {
        this.categoryID = categoryID;
    }

    public short getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(short categoryType) {
        this.categoryType = categoryType;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (transactionId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(transactionId);
        }
        parcel.writeString(name);
        parcel.writeString(desc);
        if (categoryID == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(categoryID);
        }
        parcel.writeInt((int) categoryType);
        if (createBy == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(createBy);
        }
    }
}