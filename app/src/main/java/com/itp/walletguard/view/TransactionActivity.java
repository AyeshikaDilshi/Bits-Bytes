package com.itp.walletguard.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.itp.walletguard.R;
import com.itp.walletguard.adapters.AutoCompleteCategoryAdapter;
import com.itp.walletguard.entity.CategoryEntity;
import com.itp.walletguard.entity.TransactionEntity;
import com.itp.walletguard.util.DialogUtil;
import com.itp.walletguard.util.WalletGuardApp;
import com.itp.walletguard.util.WalletGuardAppConstant;
import com.itp.walletguard.util.WalletGuardDB;
import com.itp.walletguard.viewmodel.TransactionViewModel;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class TransactionActivity extends AppCompatActivity {
    private static final String TAG = "TransactionActivity";

    private TransactionViewModel mTransactionViewModel;
    private ProgressDialog mLoadingBar;
    private Handler mHandler;
    private AutoCompleteCategoryAdapter autoCompleteCategoryAdapter;

    private List<CategoryEntity> mCategoryEntities;

    private AutoCompleteTextView ediSelectCategory;
    private ImageButton mPickDateButton;
    private TextView mShowSelectedDateText;
    private RadioGroup radioGroupCatType;
    private TextInputLayout txtInpCategory;
    private TextInputLayout txtInpDesc;
    private TextInputLayout txtInpValue;
    private RadioButton radioButtonIncome;
    private RadioButton radioButtonExp;
    private MaterialButton btnSave;
    private MaterialButton btnSaveExist;
    private MaterialButton btnUpdate;

    private Date mDate;
    private CategoryEntity mSelectedCategory;
    private TransactionEntity mSelectedTrans;

    private boolean isUpdate = false;
    private boolean firstLod = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        mTransactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        mHandler = new Handler(Looper.getMainLooper());
        mLoadingBar = new ProgressDialog(this);

        ediSelectCategory = findViewById(R.id.txt_select_category);
        mPickDateButton = findViewById(R.id.btn_open_dpt);
        mShowSelectedDateText = findViewById(R.id.txt_crd_date);
        radioGroupCatType = findViewById(R.id.rbd_cat_type);
        txtInpCategory = findViewById(R.id.input_layout_sel_categ);
        txtInpDesc = findViewById(R.id.edi_cat_desc);
        txtInpValue = findViewById(R.id.input_value);

        radioButtonIncome = findViewById(R.id.radio_income);
        radioButtonExp = findViewById(R.id.radio_exp);
        btnSave = findViewById(R.id.btn_bill_add_cart);
        btnSaveExist = findViewById(R.id.btn_add_to_wish_list);
        btnUpdate = findViewById(R.id.btn_update_trans);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mSelectedTrans = bundle.getParcelable(WalletGuardAppConstant.UPDATE_TRANS);
                firstLod = true;
            }
        }

        txtInpCategory.setEndIconOnClickListener(view -> {
            ediSelectCategory.getText().clear();
            clear(0);
        });

        ediSelectCategory.setOnItemClickListener((arg0, view, arg2, arg3) -> {
            mSelectedCategory = (CategoryEntity) arg0.getAdapter().getItem(arg2);
            InputMethodManager in = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (in != null)
                //   in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                if (mSelectedCategory != null) {
                    ediSelectCategory.setText(mSelectedCategory.getCategoryName());
                    txtInpCategory.setError(null);
                    Objects.requireNonNull(txtInpDesc.getEditText()).requestFocus();
                }
        });

        initDatePicker();
        initData((short) 1);
    }

    public void setIncome(View view) {
        //clear(0);
        initData((short) 1);
    }

    public void setExp(View view) {
        clear(0);
        initData((short) 2);
    }

    public void createTransaction(View view) {
        Log.d(TAG, "<----- Execute Create New Category ----->");

        if (mDate == null) {
            DialogUtil.showAlert(this, "Create New Transaction",
                    "Select Transaction Date Is Required !", R.drawable.ic_error);
            return;
        }

        String catName = Objects.requireNonNull(txtInpCategory.getEditText()).getText().toString();
        if (TextUtils.isEmpty(catName)) {
            txtInpCategory.setError("The Select Category Is Required!");
            return;
        }
        txtInpCategory.setError(null);

        String tranDesc = Objects.requireNonNull(txtInpDesc.getEditText()).getText().toString();
        if (TextUtils.isEmpty(tranDesc)) {
            txtInpDesc.setError("The Transaction Description Is Required!");
            return;
        }
        txtInpDesc.setError(null);

        String tranVal = Objects.requireNonNull(txtInpValue.getEditText()).getText().toString();
        if (TextUtils.isEmpty(tranVal)) {
            txtInpValue.setError("The Transaction Value Is Required!");
            return;
        }
        txtInpValue.setError(null);

        if (WalletGuardApp.getUserEntity() == null) {
            FancyToast.makeText(this, "Unauthorized Access ,Restart App And Login Again !",
                    FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            Intent loginIntent = new Intent(TransactionActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        new MaterialAlertDialogBuilder(this).setTitle(R.string.str_tran_title)
                .setMessage(R.string.str_tran_co)
                .setCancelable(false)
                .setIcon(R.drawable.ic_income)
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("Confirm", (dialogInterface, i) -> {
                    TransactionEntity transactionEntity = new TransactionEntity(
                            mSelectedCategory.getCategoryName(), tranDesc, BigDecimal.valueOf(Double.parseDouble(tranVal))
                            , mSelectedCategory.getCategoryID(), mSelectedCategory.getType(), mDate, WalletGuardApp.getUserEntity().getUserId()
                    );
                    confirmNewTransaction(transactionEntity, view.getTag().toString());
                    dialogInterface.dismiss();
                })
                .show();
    }

    public void updateTransaction(View view) {
        Log.d(TAG, "<----- Execute Update Category ----->");

        if (mSelectedTrans == null) {
            DialogUtil.showAlert(this, "Update New Transaction",
                    "Couldn't Find Any Transaction To Update!", R.drawable.ic_error);
            return;
        }

        if (mDate == null) {
            DialogUtil.showAlert(this, "Update New Transaction",
                    "Select Transaction Date Is Required !", R.drawable.ic_error);
            return;
        }

        String catName = Objects.requireNonNull(txtInpCategory.getEditText()).getText().toString();
        if (TextUtils.isEmpty(catName)) {
            txtInpCategory.setError("The Select Category Is Required!");
            return;
        }
        txtInpCategory.setError(null);

        String tranDesc = Objects.requireNonNull(txtInpDesc.getEditText()).getText().toString();
        if (TextUtils.isEmpty(tranDesc)) {
            txtInpDesc.setError("The Transaction Description Is Required!");
            return;
        }
        txtInpDesc.setError(null);

        String tranVal = Objects.requireNonNull(txtInpValue.getEditText()).getText().toString();
        if (TextUtils.isEmpty(tranVal)) {
            txtInpValue.setError("The Transaction Value Is Required!");
            return;
        }
        txtInpValue.setError(null);

        new MaterialAlertDialogBuilder(this).setTitle("Update Transaction")
                .setMessage("Do You Confirm Update Transaction ?")
                .setCancelable(false)
                .setIcon(R.drawable.ic_update)
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("Confirm", (dialogInterface, i) -> {
                    mSelectedTrans.setName(mSelectedCategory.getCategoryName());
                    mSelectedTrans.setDesc(tranDesc);
                    mSelectedTrans.setValue(BigDecimal.valueOf(Double.parseDouble(tranVal)));
                    mSelectedTrans.setCategoryID(mSelectedCategory.getCategoryID());
                    mSelectedTrans.setCategoryType(mSelectedCategory.getType());
                    mSelectedTrans.setTransactionDate(mDate);
                    confirmUpdateTransaction(mSelectedTrans);
                    dialogInterface.dismiss();
                })
                .show();
    }

//    private Short getSelectedCatType() {
//        int selectedState = radioGroupCatType.getCheckedRadioButtonId();
//        Short state = Short.valueOf("1");
//        if (selectedState != -1) {
//            RadioButton radioButton = findViewById(selectedState);
//            if (radioButton != null) {
//                switch ((String) radioButton.getTag()) {
//                    case "1":
//                        state = new Short("1");
//                        break;
//                    case "2":
//                        state = new Short("2");
//                        break;
//                    default:
//                        state = new Short("15");
//                        break;
//                }
//            }
//
//        }
//        return state;
//    }

    private void initDatePicker() {
        Log.d(TAG, "<---- Init Date Picker Called ---->");

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("Select Date:");

        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        mPickDateButton.setOnClickListener(
                v -> materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER"));

        materialDatePicker.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Long>) selection -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);
            mShowSelectedDateText.setText(getString(R.string.txt_date).concat(materialDatePicker.getHeaderText()));
            mDate = calendar.getTime();
        });

    }

    private void initData(final short categoryType) {
        Log.d(TAG, "<========= Transaction Activity Init Data Called =======>");

        if (WalletGuardApp.getUserEntity() == null) {
            FancyToast.makeText(this, "Unauthorized Access ,Restart App And Login Again !",
                    FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            Intent loginIntent = new Intent(TransactionActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }

        mLoadingBar = new ProgressDialog(this);
        mLoadingBar.setTitle("Init Data");
        mLoadingBar.setMessage("Please Waite Retrieving Data...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_category);
        mLoadingBar.show();

        mTransactionViewModel.getAllIncomeCategoriesByType(categoryType,
                WalletGuardApp.getUserEntity().getUserId()).observe(this, categoryEntities -> {
            mCategoryEntities = categoryEntities;

            autoCompleteCategoryAdapter = new AutoCompleteCategoryAdapter(this, mCategoryEntities);
            ediSelectCategory.setAdapter(autoCompleteCategoryAdapter);

            if (mLoadingBar.isShowing()) {
                mLoadingBar.dismiss();
                if (mSelectedTrans != null && firstLod)
                    getTransactionObj();
            }
        });

    }

    private void clear(int state) {
        if (state == 1) {
            mShowSelectedDateText.setText(getString(R.string.txt_date));
            mDate = null;
        }

        mSelectedCategory = null;

        if (!isUpdate) {
            Objects.requireNonNull(txtInpDesc.getEditText()).getText().clear();
            Objects.requireNonNull(txtInpValue.getEditText()).getText().clear();
        }

        ediSelectCategory.getText().clear();
        Objects.requireNonNull(txtInpCategory.getEditText()).requestFocus();
    }

    private void confirmNewTransaction(final TransactionEntity transactionEntity, final String tag) {
        mLoadingBar.setTitle(R.string.str_tran_title);
        mLoadingBar.setMessage("Creating Transaction...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_category);
        mLoadingBar.show();
        WalletGuardDB.databaseWriterService.execute(() -> {
            try {
                SystemClock.sleep(1000);

                mTransactionViewModel.insertTransaction(transactionEntity);
                mHandler.post(() -> {
                    mLoadingBar.dismiss();
                    FancyToast.makeText(this, "Transaction Create Success!", FancyToast.LENGTH_SHORT,
                            FancyToast.SUCCESS, false).show();
                    if (tag.equalsIgnoreCase("save")) {
                        clear(1);
                    } else {
                        clear(1);
                        Intent homeIntent = new Intent(TransactionActivity.this, Home.class);
                        startActivity(homeIntent);
                        finish();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "####### Create New Transaction System Error Occurred #######", e);
                mHandler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(this, "Create Transaction Failed !",
                            "System Error Occurred\n" + e.getMessage(), R.drawable.ic_error);
                });
            }
        });
    }

    private void confirmUpdateTransaction(final TransactionEntity transactionEntity) {
        mLoadingBar.setTitle("Update Transaction");
        mLoadingBar.setMessage("Updating Transaction...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_category);
        mLoadingBar.show();
        WalletGuardDB.databaseWriterService.execute(() -> {
            try {
                SystemClock.sleep(1000);

                mTransactionViewModel.updateTransaction(transactionEntity);
                mHandler.post(() -> {
                    mLoadingBar.dismiss();
                    FancyToast.makeText(this, "Transaction Update Success!", FancyToast.LENGTH_SHORT,
                            FancyToast.SUCCESS, false).show();
                    Intent homeIntent = new Intent(TransactionActivity.this, Home.class);
                    startActivity(homeIntent);
                    finish();
                });
            } catch (Exception e) {
                Log.e(TAG, "####### Update Transaction System Error Occurred #######", e);
                mHandler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(this, "Update Transaction Failed !",
                            "System Error Occurred\n" + e.getMessage(), R.drawable.ic_error);
                });
            }
        });
    }


    private void getTransactionObj() {
        mLoadingBar.setTitle("Get Transaction Entity");
        mLoadingBar.setMessage("Geting Transaction Entity...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_delete);
        mLoadingBar.show();
        WalletGuardDB.databaseWriterService.execute(() -> {
            try {
                SystemClock.sleep(1500);
                if (mSelectedTrans != null)
                    mSelectedTrans = mTransactionViewModel.getTransactionById(mSelectedTrans.getTransactionId());

                mHandler.post(() -> {
                    mLoadingBar.dismiss();
                    setData();
                });

            } catch (Exception e) {
                Log.e(TAG, "####### Fetch Transaction OBJ System Error Occurred #######", e);
                mHandler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(this, "Fetch  Transaction OBJ Failed !",
                            "System Error Occurred\n" + e.getMessage(), R.drawable.ic_error);
                });
            }
        });
    }


    private void setData() {
        if (mSelectedTrans != null) {
            btnSave.setVisibility(View.GONE);
            btnSaveExist.setVisibility(View.GONE);
            btnUpdate.setVisibility(View.VISIBLE);
            mDate = mSelectedTrans.getTransactionDate();

            if (mSelectedTrans.getCategoryType() == WalletGuardAppConstant.INCOME_CATEG) {
                radioButtonIncome.setChecked(true);
            } else {
                radioButtonExp.setChecked(true);
            }

            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setCategoryID(mSelectedTrans.getCategoryID());
            categoryEntity.setCategoryName(mSelectedTrans.getName());
            categoryEntity.setType(mSelectedTrans.getCategoryType());
            mSelectedCategory = categoryEntity;

            ediSelectCategory.setText(mSelectedTrans.getName());
            mShowSelectedDateText.setText(new SimpleDateFormat("yyyy-MMM-dd", Locale.getDefault()).format(mDate));
            Objects.requireNonNull(txtInpDesc.getEditText()).setText(mSelectedTrans.getDesc());
            Objects.requireNonNull(txtInpValue.getEditText()).setText(
                    String.format(Locale.getDefault(), "%.2f", mSelectedTrans.getValue()));

            isUpdate = true;
            firstLod = false;

        }
    }
}