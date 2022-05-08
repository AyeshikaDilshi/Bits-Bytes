package com.itp.walletguard.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.itp.walletguard.entity.CategoryEntity;
import com.itp.walletguard.entity.TransactionEntity;
import com.itp.walletguard.repository.CategoryRepo;
import com.itp.walletguard.repository.TransactionRepo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class TransactionViewModel extends AndroidViewModel {
    private static final String TAG = "TransactionViewModel";
    private final CategoryRepo categoryRepo;
    private final TransactionRepo transactionRepo;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        categoryRepo = new CategoryRepo(application);
        transactionRepo = new TransactionRepo(application);
    }

    public LiveData<List<CategoryEntity>> getAllIncomeCategoriesByType(short type, Integer userID) {
        return categoryRepo.getAllIncomeCategoriesByType(type, userID);
    }

    public LiveData<List<CategoryEntity>> getAllIncomeCategories(Integer userID) {
        return categoryRepo.getAllIncomeCategories(userID);
    }

    public void insertTransaction(TransactionEntity transactionEntity) {
        transactionRepo.insertTransaction(transactionEntity);
    }

    public void updateTransaction(TransactionEntity transactionEntity) {
        transactionRepo.updateTransaction(transactionEntity);
    }

    public TransactionEntity getTransactionById(Integer transID) {
        return transactionRepo.getTransactionById(transID);
    }

    public BigDecimal getTransactionValueByMonth(Date frm, Date to, short type, Integer userID) {
        return transactionRepo.getTransactionValueByMonth(frm, to, type, userID);
    }

    public LiveData<List<TransactionEntity>> getTransactionLstByMonth(Date frm, Date to, short type, Integer userID) {
        return transactionRepo.getTransactionLstByMonth(frm, to, type, userID);
    }

    public LiveData<List<TransactionEntity>> getTransactionLstByType(short type, Integer userID) {
        return transactionRepo.getTransactionLstByType(type, userID);
    }
}