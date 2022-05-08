package com.itp.walletguard.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.itp.walletguard.entity.TransactionEntity;
import com.itp.walletguard.repository.TransactionRepo;

import java.util.Date;
import java.util.List;

public class ReportViewModel extends AndroidViewModel {
    private static final String TAG = "ReportViewModel";

    private final TransactionRepo transactionRepo;
    public ReportViewModel(@NonNull Application application) {
        super(application);
        transactionRepo=new TransactionRepo(application);
    }

    public List<TransactionEntity> getTransactionForRpt(Date frm, Date to, Integer userID) {
        return transactionRepo.getTransactionForRpt(frm, to, userID);
    }
}