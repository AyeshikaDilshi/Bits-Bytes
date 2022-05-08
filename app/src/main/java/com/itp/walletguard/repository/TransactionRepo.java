package com.itp.walletguard.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.itp.walletguard.dao.TransactionDao;
import com.itp.walletguard.entity.TransactionEntity;
import com.itp.walletguard.util.WalletGuardDB;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class TransactionRepo {
    private static final String TAG = "TransactionRepo";
    private final TransactionDao transactionDao;

    public TransactionRepo(Application application) {
        this.transactionDao = WalletGuardDB.getDatabse(application).transactionDao();
    }

    public void insertTransaction(TransactionEntity transactionEntity) {
        transactionDao.insertTransaction(transactionEntity);
    }

    public void delete(TransactionEntity transactionEntity) {
        transactionDao.delete(transactionEntity);
    }

    public void updateTransaction(TransactionEntity transactionEntity) {
        Log.d(TAG, "######## Value " + transactionEntity.getValue());
        transactionDao.updateTransaction(
                transactionEntity.getName(), transactionEntity.getDesc(),
                transactionEntity.getValue(), transactionEntity.getCategoryID(),
                transactionEntity.getCategoryType(), transactionEntity.getTransactionDate(), transactionEntity.getTransactionId()

        );
    }

    public TransactionEntity getTransactionById(Integer transID) {
        return transactionDao.getTransactionById(transID);
    }

    public Integer getTransactionCount(Integer catID) {
        return transactionDao.getTransactionCount(catID);
    }

    public BigDecimal getTransactionValueByMonth(Date frm, Date to, short type, Integer userID) {
        return transactionDao.getTransactionValueByMonth(frm, to, type, userID);
    }

    public LiveData<List<TransactionEntity>> getTransactionLstByMonth(Date frm, Date to, short type, Integer userID) {
        return transactionDao.getTransactionLstByMonth(frm, to, type, userID);
    }

    public LiveData<List<TransactionEntity>> getTransactionLstByType(short type, Integer userID) {
        return transactionDao.getTransactionLstByType(type, userID);
    }

    public List<TransactionEntity> getTransactionForRpt(Date frm, Date to, Integer userID) {
        return transactionDao.getTransactionForRpt(frm, to, userID);
    }
}