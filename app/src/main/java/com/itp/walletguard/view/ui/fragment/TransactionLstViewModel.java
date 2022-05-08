package com.itp.walletguard.view.ui.fragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.itp.walletguard.entity.TransactionEntity;
import com.itp.walletguard.repository.TransactionRepo;

import java.util.List;

public class TransactionLstViewModel extends AndroidViewModel {
    private static final String TAG = "TransactionLstViewModel";
    private final TransactionRepo transactionRepo;
    public TransactionLstViewModel(@NonNull Application application) {
        super(application);
        transactionRepo=new TransactionRepo(application);
    }
   public void delete(TransactionEntity transactionEntity){
        transactionRepo.delete(transactionEntity);
    }
    public LiveData<List<TransactionEntity>> getTransactionLstByType(short type, Integer userID) {
        return transactionRepo.getTransactionLstByType(type, userID);
    }
}