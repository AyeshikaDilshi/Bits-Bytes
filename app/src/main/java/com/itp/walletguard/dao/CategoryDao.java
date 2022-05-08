package com.itp.walletguard.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.itp.walletguard.entity.CategoryEntity;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCategory(CategoryEntity categoryEntity);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateCategory(CategoryEntity categoryEntity);

    @Delete
    void deleteCategory(CategoryEntity categoryEntity);

    @Query("SELECT COUNT(cat_id) FROM tbl_category WHERE cat_type=:type AND create_by=:userID")
    Integer getAllCategoryCountByType(short type, Integer userID);

    @Query("SELECT COUNT(cat_id) FROM tbl_category WHERE create_by=:userID")
    Integer getAllCategoryCount(Integer userID);

    @Query("SELECT * FROM tbl_category WHERE cat_type=:type AND create_by=:userID ORDER BY cat_id ASC")
    LiveData<List<CategoryEntity>> getAllIncomeCategoriesByType(short type, Integer userID);

    @Query("SELECT * FROM tbl_category WHERE create_by=:userID ORDER BY cat_id ASC")
    LiveData<List<CategoryEntity>> getAllIncomeCategories(Integer userID);


}
