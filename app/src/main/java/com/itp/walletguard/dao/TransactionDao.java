package com.itp.walletguard.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.itp.walletguard.entity.TransactionEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Dao
public interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertTransaction(TransactionEntity transactionEntity);

    @Query("UPDATE tbl_transaction SET name=:name,`desc`=:des,value=:val,tra_cat_id=:catId," +
            "tra_type=:type,create_date=:cDate WHERE transaction_id=:transId")
    void updateTransaction(String name, String des, BigDecimal val, Integer catId, short type, Date cDate, Integer transId);

    @Delete
    void delete(TransactionEntity transactionEntity);

    @Query("SELECT * FROM tbl_transaction WHERE transaction_id=:transID")
    TransactionEntity getTransactionById(Integer transID);

    @Query("SELECT SUM(value) FROM tbl_transaction WHERE (create_date Between :frm AND :to) AND (tra_type=:type AND create_by=:userID)")
    BigDecimal getTransactionValueByMonth(Date frm, Date to, short type, Integer userID);

    @Query("SELECT COUNT(transaction_id) FROM tbl_transaction WHERE tra_cat_id=:catID")
    Integer getTransactionCount(Integer catID);

    @Query("SELECT * FROM tbl_transaction WHERE (create_date Between :frm AND :to) AND (tra_type=:type AND create_by=:userID) ORDER BY tra_cat_id ASC")
    LiveData<List<TransactionEntity>> getTransactionLstByMonth(Date frm, Date to, short type, Integer userID);

    @Query("SELECT * FROM tbl_transaction WHERE tra_type=:type AND create_by=:userID ORDER BY tra_cat_id ASC")
    LiveData<List<TransactionEntity>> getTransactionLstByType(short type, Integer userID);

    @Query("SELECT * FROM tbl_transaction WHERE (create_date Between :frm AND :to) AND create_by=:userID ORDER BY tra_cat_id ASC")
    List<TransactionEntity> getTransactionForRpt(Date frm, Date to, Integer userID);
}
