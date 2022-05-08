package com.itp.walletguard.view.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.itp.walletguard.R;
import com.itp.walletguard.entity.BankAccountEntity;
import com.itp.walletguard.util.WalletGuardApp;
import com.itp.walletguard.util.WalletGuardAppConstant;
import com.itp.walletguard.util.WalletGuardDB;
import com.itp.walletguard.view.CalculatorActivity;
import com.itp.walletguard.view.CategoryActivity;
import com.itp.walletguard.view.CategoryListActivity;
import com.itp.walletguard.view.LoginActivity;
import com.itp.walletguard.view.ReportActivity;
import com.itp.walletguard.viewmodel.HomeViewModel;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private Handler mHandler;

    private HomeViewModel homeViewModel;

    private ProgressBar mCusLoadPrgBar;
    private ProgressBar mStockLoadPrgBar;
    private ProgressBar mCreditLoadPrgBar;
    private ProgressBar mSalesLoadPrgBar;

    //Income Related Text Fields
    private TextView txtTotCustomers;
    private TextView txtTotCrdCustomers;
    private TextView txtTotWishLst;

    //Sales Related Text Fields
    private TextView txtTotSales;
    private TextView txtTotCashSales;
    private TextView txtTotCrdSales;
    private TextView txtTotCrdAndCaSales;

    private TextView txtBnkOne;
    private TextView txtBnkTwo;
    private TextView txtBnkThree;

    //Stock Related Text Fields
    private TextView txtTotStockValue;
    private TextView txtStockQty;
    private TextView txtExpiredQty;
    private TextView txtRoQty;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        mHandler = new Handler(Looper.getMainLooper());
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCusLoadPrgBar = view.findViewById(R.id.cus_prg_bar);
        mStockLoadPrgBar = view.findViewById(R.id.stock_prg_bar);
        mCreditLoadPrgBar = view.findViewById(R.id.coll_prg_bar);
        mSalesLoadPrgBar = view.findViewById(R.id.sal_prg_bar);


        ImageView rptImg = view.findViewById(R.id.img_report);
        rptImg.setOnClickListener(view1 -> {
            Intent rptIntent = new Intent(requireContext(), ReportActivity.class);
            startActivity(rptIntent);
        });
        ImageView rptSetting = view.findViewById(R.id.img_setting);
        rptSetting.setOnClickListener(view1 -> {
            Intent categoryIntent = new Intent(requireContext(), CategoryActivity.class);
            startActivity(categoryIntent);
        });
        ImageView rptCalculator = view.findViewById(R.id.img_cal);
        rptCalculator.setOnClickListener(view1 -> {
            Intent calculatorIntent = new Intent(requireContext(), CalculatorActivity.class);
            startActivity(calculatorIntent);
        });

        txtTotCustomers = view.findViewById(R.id.txtTotCus);
        txtTotCrdCustomers = view.findViewById(R.id.txt_tot_cus);
        txtTotWishLst = view.findViewById(R.id.txt_non_tot_cus);

        txtTotSales = view.findViewById(R.id.txt_rec_hint);
        txtTotCashSales = view.findViewById(R.id.txt_rec_metr_val);
        txtTotCrdSales = view.findViewById(R.id.txt_rec_bill_val);
        txtTotCrdAndCaSales = view.findViewById(R.id.txt_cr_an_ca);

        txtBnkOne = view.findViewById(R.id.txt_bank_bal_hint);
        txtBnkTwo = view.findViewById(R.id.txt_cas_bal_hint);
        txtBnkThree = view.findViewById(R.id.txt_ro_qty_lb);

        txtTotStockValue = view.findViewById(R.id.txt_acc_bal);
        txtStockQty = view.findViewById(R.id.txt_bank_bal);
        txtExpiredQty = view.findViewById(R.id.txt_cas_bal);
        txtRoQty = view.findViewById(R.id.txt_ro_qty);

        ImageView imgAllCateg = view.findViewById(R.id.ic_tot_rec);
        imgAllCateg.setOnClickListener(view1 -> {
            loadCategoryLst(0);
        });

        txtTotCrdSales.setOnClickListener(view1 -> {
            loadCategoryLst(1);
        });

        txtTotCashSales.setOnClickListener(view1 -> {
            loadCategoryLst(2);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loadCategoryData();
        initBankData();
        loadTransData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //binding = null;
    }

    private void initBankData() {
        Log.d(TAG, "<========= Home Fragment Bank  Init Data Called =======>");
        if (WalletGuardApp.getUserEntity() == null) {
            FancyToast.makeText(requireContext(), "Unauthorized Access ,Restart App And Login Again !",
                    FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            Intent loginIntent = new Intent(requireContext(), LoginActivity.class);
            startActivity(loginIntent);
        }

        homeViewModel.getAllAccounts(WalletGuardApp.getUserEntity().getUserId()).observe(this, bankAccountEntities -> {
            BigDecimal totBankBal = BigDecimal.ZERO;
            if (bankAccountEntities != null) {
                if (bankAccountEntities.size() > 0) {
                    for (BankAccountEntity bnk : bankAccountEntities) {
                        totBankBal = totBankBal.add(bnk.getBalance());
                    }
                    BankAccountEntity bnkOne = bankAccountEntities.get(0);
                    BankAccountEntity bnkTwo = bankAccountEntities.get(1);
                    BankAccountEntity bnkThree = bankAccountEntities.get(2);

                    if (bnkOne != null) {
                        txtBnkOne.setText(bnkOne.getBnkName());
                        txtStockQty.setText(String.format(Locale.getDefault(), "%.2f", bnkOne.getBalance()));
                    }
                    if (bnkTwo != null) {
                        txtBnkTwo.setText(bnkTwo.getBnkName());
                        txtExpiredQty.setText(String.format(Locale.getDefault(), "%.2f", bnkTwo.getBalance()));
                    }
                    if (bnkThree != null) {
                        txtBnkThree.setText(bnkThree.getBnkName());
                        txtRoQty.setText(String.format(Locale.getDefault(), "%.2f", bnkThree.getBalance()));
                    }
                    txtTotStockValue.setText(String.format(Locale.getDefault(), "%.2f", totBankBal));
                }
            }
            mStockLoadPrgBar.setVisibility(View.GONE);
        });

    }


    private void loadCategoryData() {
        Log.d(TAG, "<========= Home Fragment Category Called =======>");
        if (WalletGuardApp.getUserEntity() == null) {
            FancyToast.makeText(requireContext(), "Unauthorized Access ,Restart App And Login Again !",
                    FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            Intent loginIntent = new Intent(requireContext(), LoginActivity.class);
            startActivity(loginIntent);
            requireActivity().finish();
            return;
        }

        txtTotSales.setText(getString(R.string.lod));
        txtTotCashSales.setText(getString(R.string.lod));
        txtTotCrdSales.setText(getString(R.string.lod));
        txtTotCrdAndCaSales.setVisibility(View.VISIBLE);
        WalletGuardDB.databaseWriterService.execute(() -> {
            try {
                SystemClock.sleep(1500);
                Integer totCategory = homeViewModel.getAllCategoryCount(WalletGuardApp.getUserEntity().getUserId());
                Integer totIncomeCategoryCount = homeViewModel.getAllCategoryCountByType(
                        WalletGuardAppConstant.INCOME_CATEG, WalletGuardApp.getUserEntity().getUserId());
                Integer totExpCategoryCount = homeViewModel.getAllCategoryCountByType(
                        WalletGuardAppConstant.EXP_CATEG, WalletGuardApp.getUserEntity().getUserId());

                mHandler.post(() -> {
                    if (totCategory != null)
                        txtTotSales.setText(String.format(Locale.getDefault(), "%03d", totCategory));
                    else
                        txtTotSales.setText(String.format(Locale.getDefault(), "%03d", 0));

                    if (totIncomeCategoryCount != null)
                        txtTotCrdSales.setText(String.format(Locale.getDefault(), "%02d", totIncomeCategoryCount));
                    else
                        txtTotCrdSales.setText(String.format(Locale.getDefault(), "%02d", 0));

                    if (totExpCategoryCount != null)
                        txtTotCashSales.setText(String.format(Locale.getDefault(), "%02d", totExpCategoryCount));
                    else
                        txtTotCashSales.setText(String.format(Locale.getDefault(), "%02d", 0));

                    txtTotCrdAndCaSales.setText(String.format(Locale.getDefault(), "%02d", 0));

                    mSalesLoadPrgBar.setVisibility(View.GONE);
                });
            } catch (Exception e) {
                Log.e(TAG, "####### Load Category Data Error #######", e);
                mHandler.post(() -> {
                    txtTotSales.setText(getString(R.string.lod_err));
                    txtTotCashSales.setText(getString(R.string.lod_err));
                    txtTotCrdSales.setText(getString(R.string.lod_err));
                    txtTotCrdAndCaSales.setText(getString(R.string.lod_err));
                    mSalesLoadPrgBar.setVisibility(View.GONE);
                    FancyToast.makeText(requireContext(), "Load  Category Data Error!\n" + e.getMessage(),
                            FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                });
            }
        });

    }

    private void loadTransData() {
        Log.d(TAG, "<========= Home Fragment Trans Called =======>");
        if (WalletGuardApp.getUserEntity() == null) {
            FancyToast.makeText(requireContext(), "Unauthorized Access ,Restart App And Login Again !",
                    FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            Intent loginIntent = new Intent(requireContext(), LoginActivity.class);
            startActivity(loginIntent);
            requireActivity().finish();
            return;
        }

        txtTotSales.setText(getString(R.string.lod));
        txtTotCashSales.setText(getString(R.string.lod));
        txtTotCrdSales.setText(getString(R.string.lod));
        txtTotCrdAndCaSales.setVisibility(View.VISIBLE);
        WalletGuardDB.databaseWriterService.execute(() -> {
            try {
                BigDecimal currEx = BigDecimal.ZERO;
                SystemClock.sleep(1500);
                Calendar gc = new GregorianCalendar();
                gc.set(Calendar.MONTH, 5);
                gc.set(Calendar.DAY_OF_MONTH, 1);
                Date monthStart = gc.getTime();
                gc.add(Calendar.MONTH, 1);
                gc.add(Calendar.DAY_OF_MONTH, -1);
                Date monthEnd = gc.getTime();
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

                Timestamp start = getStartTimestamp(11);
                Timestamp end = getEndTimestamp(11);

                currEx = homeViewModel.getTransactionValueByMonth(new Date(start.getTime()),  new Date(end.getTime()),
                        WalletGuardAppConstant.INCOME_CATEG, WalletGuardApp.getUserEntity().getUserId());

                BigDecimal finalCurrEx = currEx;
                mHandler.post(() -> {

                    Toast.makeText(requireContext(), "Exp: " + finalCurrEx, Toast.LENGTH_SHORT).show();
                    mSalesLoadPrgBar.setVisibility(View.GONE);
                });
            } catch (Exception e) {
                Log.e(TAG, "####### Load Category Data Error #######", e);
                mHandler.post(() -> {
                    txtTotSales.setText(getString(R.string.lod_err));
                    txtTotCashSales.setText(getString(R.string.lod_err));
                    txtTotCrdSales.setText(getString(R.string.lod_err));
                    txtTotCrdAndCaSales.setText(getString(R.string.lod_err));
                    mSalesLoadPrgBar.setVisibility(View.GONE);
                    FancyToast.makeText(requireContext(), "Load  Category Data Error!\n" + e.getMessage(),
                            FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                });
            }
        });

    }


    private void loadCategoryLst(final int catType) {
        Intent myIntent = new Intent(requireContext(), CategoryListActivity.class);
        myIntent.putExtra("CAT_TYPE", catType);
        startActivity(myIntent);
    }


    public static Timestamp getStartTimestamp(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Timestamp getEndTimestamp(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }


}