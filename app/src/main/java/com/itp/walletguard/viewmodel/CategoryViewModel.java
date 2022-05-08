package com.itp.walletguard.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.itp.walletguard.entity.CategoryEntity;
import com.itp.walletguard.repository.CategoryRepo;
import com.itp.walletguard.repository.TransactionRepo;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private static final String TAG = "CategoryViewModel";
    private final CategoryRepo categoryRepo;
    private final TransactionRepo transactionRepo;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        categoryRepo = new CategoryRepo(application);
        transactionRepo = new TransactionRepo(application);
    }

    public void insertCategory(CategoryEntity categoryEntity) {
        Log.d(TAG, "<-------- Execute Insert Category In Category ViewModel ---->Category Name: " + categoryEntity.getCategoryName());
        categoryRepo.insertCategory(categoryEntity);

    }

    public void updatetCategory(CategoryEntity categoryEntity) {
        Log.d(TAG, "<-------- Execute update Category In Category ViewModel ---->Category Name: " + categoryEntity.getCategoryName());
        categoryRepo.updateCategory(categoryEntity);

    }

    public void deleteCategory(CategoryEntity categoryEntity) {
        categoryRepo.deleteCategory(categoryEntity);
    }

    public Integer getAllCategoryCountByType(short type, Integer userID) {
        return categoryRepo.getAllCategoryCountByType(type, userID);
    }


    public Integer getAllCategoryCount(Integer userID) {
        return categoryRepo.getAllCategoryCount(userID);
    }

    public LiveData<List<CategoryEntity>> getAllIncomeCategoriesByType(short type, Integer userID) {
        return categoryRepo.getAllIncomeCategoriesByType(type, userID);
    }

    public Integer getTransactionCount(Integer catID) {
        return transactionRepo.getTransactionCount(catID);
    }

    public LiveData<List<CategoryEntity>> getAllIncomeCategories(Integer userID) {
        return categoryRepo.getAllIncomeCategories(userID);
    }
}