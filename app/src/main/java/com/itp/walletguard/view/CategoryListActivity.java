package com.itp.walletguard.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itp.walletguard.R;
import com.itp.walletguard.adapters.CategoryListAdapter;
import com.itp.walletguard.entity.CategoryEntity;
import com.itp.walletguard.util.DialogUtil;
import com.itp.walletguard.util.WalletGuardApp;
import com.itp.walletguard.util.WalletGuardAppConstant;
import com.itp.walletguard.util.WalletGuardDB;
import com.itp.walletguard.viewmodel.CategoryViewModel;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class CategoryListActivity extends AppCompatActivity {
    private static final String TAG = "WishLstFragment";
    private short categoryType;
    private ProgressDialog mLoadingBar;
    private Handler mHandler;
    private CategoryViewModel mCategoryViewModel;

    private RecyclerView mRecyclerView;
    private CategoryListAdapter mCategoryListAdapter;
    private List<CategoryEntity> mCategoryEntityList;

    private CategoryEntity mSelectedCategoryEntity;
    private int mSwPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        mHandler = new Handler(Looper.getMainLooper());
        mCategoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        TextView categoryLstTitle = findViewById(R.id.txt_cat_title);
        mRecyclerView = findViewById(R.id.cat_lst_rec_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayout);

        Intent intent = getIntent();
        if (intent != null) {
            categoryType = (short) intent.getIntExtra("CAT_TYPE", 0);
            switch (categoryType) {
                case 1:
                    categoryLstTitle.setText("All Income Category");
                    break;
                case 2:
                    categoryLstTitle.setText("All Expenditure Category");
                    break;
                default:
                    categoryLstTitle.setText("All Category");

            }
        }
        mLoadingBar = new ProgressDialog(this);
        mLoadingBar.setTitle("Fetch Category List/s");
        mLoadingBar.setMessage("Please Waite Retrieving Category List/s...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_category);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (WalletGuardApp.getUserEntity() == null) {
            FancyToast.makeText(this, "Unauthorized Access ,Restart App And Login Again !",
                    FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            Intent loginIntent = new Intent(CategoryListActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
        mLoadingBar.show();
        if (categoryType == 0) {
            mCategoryViewModel.getAllIncomeCategories(WalletGuardApp.getUserEntity().getUserId())
                    .observe(this, categoryEntities -> {
                        mCategoryEntityList = categoryEntities;

                        mCategoryListAdapter = new CategoryListAdapter(mCategoryEntityList, this);
                        mRecyclerView.setAdapter(mCategoryListAdapter);
                        mCategoryListAdapter.setCategoryList(mCategoryEntityList);

                        if (mLoadingBar.isShowing())
                            mLoadingBar.dismiss();
                    });
            swipeRemove();
        } else {
            mCategoryViewModel.getAllIncomeCategoriesByType(categoryType,
                    WalletGuardApp.getUserEntity().getUserId()).observe(this, categoryEntities -> {
                mCategoryEntityList = categoryEntities;

                mCategoryListAdapter = new CategoryListAdapter(mCategoryEntityList, this);
                mRecyclerView.setAdapter(mCategoryListAdapter);
                mCategoryListAdapter.setCategoryList(mCategoryEntityList);

                if (mLoadingBar.isShowing())
                    mLoadingBar.dismiss();
            });
            swipeRemove();
        }
    }

    private void swipeRemove() {
        ItemTouchHelper.Callback touchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mSwPosition = viewHolder.getBindingAdapterPosition();
                mSelectedCategoryEntity = mCategoryListAdapter.getCategoryEntity(mSwPosition);
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        deleteCategory();
                        break;

                    case ItemTouchHelper.RIGHT:
                        updateCategory();
                        break;
                }

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(CategoryListActivity.this, R.color.red))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .addSwipeLeftLabel("Remove From List")
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(CategoryListActivity.this, R.color.teal_200))
                        .addSwipeRightActionIcon(R.drawable.ic_update)
                        .addSwipeRightLabel("Add To Bill")
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelperCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void deleteCategory() {
        if (mSelectedCategoryEntity == null) {
            DialogUtil.showAlert(this, "Delete Category",
                    "Select Category Is Required !", R.drawable.ic_delete);
            return;
        }
        new MaterialAlertDialogBuilder(this).setTitle("Delete Category")
                .setMessage("Are You Confirm To Delete ?")
                .setCancelable(false)
                .setIcon(R.drawable.ic_delete)
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    mCategoryListAdapter.notifyDataSetChanged();
                })
                .setPositiveButton("Confirm", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    confirmDeleteCategory(mSelectedCategoryEntity);
                })
                .show();
    }

    private void updateCategory() {
        if (mSelectedCategoryEntity != null) {
            Intent catIntent = new Intent(this, CategoryActivity.class);
           // Bundle bundle = new Bundle();
            catIntent.putExtra(WalletGuardAppConstant.UPDATE_CAT, mSelectedCategoryEntity);
            startActivity(catIntent);
        } else {
            DialogUtil.showAlert(this, "Update Category !",
                    "Select Category Is Required", R.drawable.ic_update);
        }
    }

    private void confirmDeleteCategory(final CategoryEntity categoryEntity) {
        mLoadingBar.setTitle("Delete Category");
        mLoadingBar.setMessage("Deleting Category...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_delete);
        mLoadingBar.show();
        WalletGuardDB.databaseWriterService.execute(() -> {
            try {
                SystemClock.sleep(1000);
                Integer count;
                count = mCategoryViewModel.getTransactionCount(categoryEntity.getCategoryID());

                if (count != null) {
                    if (count > 0) {
                        mHandler.post(() -> {
                            mLoadingBar.dismiss();
                            mCategoryListAdapter.notifyDataSetChanged();
                            FancyToast.makeText(this, "Cant  Delete Category This Category Belongs To Transaction/s!",
                                    FancyToast.LENGTH_SHORT,
                                    FancyToast.ERROR, false).show();

                        });
                    }else{
                        mCategoryViewModel.deleteCategory(categoryEntity);
                        mHandler.post(() -> {
                            mLoadingBar.dismiss();
                            FancyToast.makeText(this, "Category Delete Success!", FancyToast.LENGTH_SHORT,
                                    FancyToast.SUCCESS, false).show();
                        });
                    }
                } else {
                    mCategoryViewModel.deleteCategory(categoryEntity);
                    mHandler.post(() -> {
                        mLoadingBar.dismiss();
                        FancyToast.makeText(this, "Category Delete Success!", FancyToast.LENGTH_SHORT,
                                FancyToast.SUCCESS, false).show();
                    });
                }


            } catch (Exception e) {
                Log.e(TAG, "####### Delete Category System Error Occurred #######", e);
                mHandler.post(() -> {
                    mLoadingBar.dismiss();
                    mCategoryListAdapter.notifyDataSetChanged();
                    DialogUtil.showAlert(this, "Delete Category Failed !",
                            "System Error Occurred\n" + e.getMessage(), R.drawable.ic_error);
                });
            }
        });
    }


}