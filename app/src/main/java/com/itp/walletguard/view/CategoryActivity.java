package com.itp.walletguard.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.itp.walletguard.R;
import com.itp.walletguard.entity.CategoryEntity;
import com.itp.walletguard.util.DialogUtil;
import com.itp.walletguard.util.WalletGuardApp;
import com.itp.walletguard.util.WalletGuardAppConstant;
import com.itp.walletguard.util.WalletGuardDB;
import com.itp.walletguard.viewmodel.CategoryViewModel;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Date;
import java.util.Objects;

public class CategoryActivity extends AppCompatActivity {
    private static final String TAG = "CategoryActivity";

    private CategoryViewModel mCategoryViewModel;
    private ProgressDialog mLoadingBar;
    private Handler mHandler;

    private RadioGroup radioGroupCatType;
    private TextInputLayout txtInpCatname;
    private TextInputLayout txtInpCatDesc;

    private CategoryEntity mHavUpdCategoryEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        mCategoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        mHandler = new Handler(Looper.getMainLooper());
        mLoadingBar = new ProgressDialog(this);

        radioGroupCatType = findViewById(R.id.rbd_cat_type);
        txtInpCatname = findViewById(R.id.edi_cat_name);
        txtInpCatDesc = findViewById(R.id.edi_cat_desc);

        Button btnSave = findViewById(R.id.btn_save_cat);
        Button btnSaveExist = findViewById(R.id.btn_save_and_ext);
        MaterialButton btnUpdate = findViewById(R.id.btn_update_cate);

        Intent intent = getIntent();

        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mHavUpdCategoryEntity = bundle.getParcelable(WalletGuardAppConstant.UPDATE_CAT);
                if (mHavUpdCategoryEntity != null) {
                    btnSave.setVisibility(View.GONE);
                    btnSaveExist.setVisibility(View.GONE);
                    btnUpdate.setVisibility(View.VISIBLE);

                    Objects.requireNonNull(txtInpCatname.getEditText()).setText(mHavUpdCategoryEntity.getCategoryName());
                    Objects.requireNonNull(txtInpCatDesc.getEditText()).setText(mHavUpdCategoryEntity.getDesc());
                }
            }
        }

    }

    public void createNewCategory(View view) {
        Log.d(TAG, "<----- Execute Create New Category ----->");

        String catName = Objects.requireNonNull(txtInpCatname.getEditText()).getText().toString();
        if (TextUtils.isEmpty(catName)) {
            txtInpCatname.setError("The Category Name Is Required!");
            return;
        }
        txtInpCatname.setError(null);

        String catDesc = Objects.requireNonNull(txtInpCatDesc.getEditText()).getText().toString();
        if (TextUtils.isEmpty(catDesc)) {
            txtInpCatDesc.setError("The Category Description Is Required!");
            return;
        }
        txtInpCatDesc.setError(null);

        if (WalletGuardApp.getUserEntity() == null) {
            FancyToast.makeText(this, "Unauthorized Access ,Restart App And Login Again !",
                    FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            Intent loginIntent = new Intent(CategoryActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        new MaterialAlertDialogBuilder(this).setTitle(R.string.cre_cat_title)
                .setMessage(R.string.cre_cat_con)
                .setCancelable(false)
                .setIcon(R.drawable.ic_category)
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("Confirm", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    CategoryEntity categoryEntity = new CategoryEntity(
                            catName.trim(), catDesc.trim(), WalletGuardApp.getUserEntity().getUserId(), new Date(), getSelectedCatType()
                    );
                    confirmNewCategory(categoryEntity, view.getTag().toString());
                })
                .show();
    }

    public void updateCategory(View view) {
        Log.d(TAG, "<----- Execute Update Category ----->");

        if (mHavUpdCategoryEntity == null) {
            DialogUtil.showAlert(this, "Update Category !",
                    "Select Category Is Required", R.drawable.ic_update);
            return;
        }

        if (mHavUpdCategoryEntity.getType() != getSelectedCatType()) {
            DialogUtil.showAlert(this, "Update Category !",
                    "Update Category Type Is Not Allowed !", R.drawable.ic_update);
            return;
        }

        String catName = Objects.requireNonNull(txtInpCatname.getEditText()).getText().toString();
        if (TextUtils.isEmpty(catName)) {
            txtInpCatname.setError("The Category Name Is Required!");
            return;
        }
        txtInpCatname.setError(null);

        String catDesc = Objects.requireNonNull(txtInpCatDesc.getEditText()).getText().toString();
        if (TextUtils.isEmpty(catDesc)) {
            txtInpCatDesc.setError("The Category Description Is Required!");
            return;
        }
        txtInpCatDesc.setError(null);

        new MaterialAlertDialogBuilder(this).setTitle("Update Category")
                .setMessage("Are You Sure Update Category ?")
                .setCancelable(false)
                .setIcon(R.drawable.ic_update)
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("Confirm", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    mHavUpdCategoryEntity.setCategoryName(catName);
                    mHavUpdCategoryEntity.setDesc(catDesc);
                    confirmUpdateCategory(mHavUpdCategoryEntity);
                })
                .show();
    }


    private Short getSelectedCatType() {
        int selectedState = radioGroupCatType.getCheckedRadioButtonId();
        Short state = Short.valueOf("1");
        if (selectedState != -1) {
            RadioButton radioButton = findViewById(selectedState);
            if (radioButton != null) {
                switch ((String) radioButton.getTag()) {
                    case "1":
                        state = new Short("1");
                        break;
                    case "2":
                        state = new Short("2");
                        break;
                    default:
                        state = new Short("15");
                        break;
                }
            }

        }
        return state;
    }


    private void confirmNewCategory(final CategoryEntity categoryEntity, final String tag) {
        mLoadingBar.setTitle(R.string.cre_cat_title);
        mLoadingBar.setMessage("Creating Category...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_category);
        mLoadingBar.show();
        WalletGuardDB.databaseWriterService.execute(() -> {
            try {
                SystemClock.sleep(1000);

                mCategoryViewModel.insertCategory(categoryEntity);
                mHandler.post(() -> {
                    mLoadingBar.dismiss();
                    FancyToast.makeText(this, "Category Create Success!", FancyToast.LENGTH_SHORT,
                            FancyToast.SUCCESS, false).show();
                    if (tag.equalsIgnoreCase("save")) {
                        clear();
                    } else {
                        Intent homeIntent = new Intent(CategoryActivity.this, Home.class);
                        startActivity(homeIntent);
                        finish();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "####### Create New Category System Error Occured #######", e);
                mHandler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(this, "Create Category Failed !",
                            "System Error Occurred\n" + e.getMessage(), R.drawable.ic_error);
                });
            }
        });
    }

    private void confirmUpdateCategory(final CategoryEntity categoryEntity) {
        mLoadingBar.setTitle("Update Category");
        mLoadingBar.setMessage("Updating Category...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_update);
        mLoadingBar.show();
        WalletGuardDB.databaseWriterService.execute(() -> {
            try {
                SystemClock.sleep(1000);

                mCategoryViewModel.updatetCategory(categoryEntity);
                mHandler.post(() -> {
                    mLoadingBar.dismiss();
                    FancyToast.makeText(this, "Update Category Success!", FancyToast.LENGTH_SHORT,
                            FancyToast.SUCCESS, false).show();
                    clear();
                    Intent homeIntent = new Intent(CategoryActivity.this, Home.class);
                    startActivity(homeIntent);
                    finish();
                });
            } catch (Exception e) {
                Log.e(TAG, "####### Update Category System Error Occured #######", e);
                mHandler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(this, "Update Category Failed !",
                            "System Error Occurred\n" + e.getMessage(), R.drawable.ic_error);
                });
            }
        });
    }


    private void clear() {
        Objects.requireNonNull(txtInpCatname.getEditText()).getText().clear();
        Objects.requireNonNull(txtInpCatDesc.getEditText()).getText().clear();
        Objects.requireNonNull(txtInpCatname.getEditText()).requestFocus();
    }
}