package com.itp.walletguard.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.itp.walletguard.entity.BankAccountEntity;
import com.itp.walletguard.repository.BankRepo;
import com.itp.walletguard.repository.TransactionRepo;
import com.itp.walletguard.viewmodel.CategoryViewModel;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final CategoryViewModel categoryViewModel;
    private final BankRepo bankRepo;
    private final TransactionRepo transactionRepo;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        categoryViewModel = new CategoryViewModel(application);
        bankRepo = new BankRepo(application);
        transactionRepo=new TransactionRepo(application);
    }

    public Integer getAllCategoryCountByType(short type, Integer userID) {
        return categoryViewModel.getAllCategoryCountByType(type, userID);
    }
    public LiveData<List<BankAccountEntity>> getAllAccounts(Integer userID) {
        return bankRepo.getAllAccounts(userID);
    }

    public Integer getAllCategoryCount(Integer userID) {
        return categoryViewModel.getAllCategoryCount(userID);
    }
    public BigDecimal getTransactionValueByMonth(Date frm, Date to, short type, Integer userID) {
        return transactionRepo.getTransactionValueByMonth(frm, to, type, userID);
    }
}