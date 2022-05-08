package com.itp.walletguard.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.itp.walletguard.dao.CategoryDao;
import com.itp.walletguard.entity.CategoryEntity;
import com.itp.walletguard.util.WalletGuardDB;

import java.util.List;

public class CategoryRepo {

    private static final String TAG = "UserRepo";
    private final CategoryDao categoryDao;

    public CategoryRepo(Application application) {
        categoryDao = WalletGuardDB.getDatabse(application).categoryDao();
    }

    public void insertCategory(CategoryEntity categoryEntity) {
        Log.d(TAG, "<-------- Execute Insert Category In Category Repo ---->Category Name: " + categoryEntity.getCategoryName());
        categoryDao.insertCategory(categoryEntity);

    }

    public void updateCategory(CategoryEntity categoryEntity) {
        Log.d(TAG, "<-------- Execute Update Category In Category Repo ---->Category Name: " + categoryEntity.getCategoryName());
        categoryDao.updateCategory(categoryEntity);

    }
    public  void deleteCategory(CategoryEntity categoryEntity){
        categoryDao.deleteCategory(categoryEntity);
    }

    public Integer getAllCategoryCountByType(short type, Integer userID) {
        return categoryDao.getAllCategoryCountByType(type, userID);
    }


    public Integer getAllCategoryCount(Integer userID) {
        return categoryDao.getAllCategoryCount(userID);
    }

    public LiveData<List<CategoryEntity>> getAllIncomeCategoriesByType(short type, Integer userID) {
        return categoryDao.getAllIncomeCategoriesByType(type, userID);
    }


    public LiveData<List<CategoryEntity>> getAllIncomeCategories(Integer userID) {
        return categoryDao.getAllIncomeCategories(userID);
    }
}