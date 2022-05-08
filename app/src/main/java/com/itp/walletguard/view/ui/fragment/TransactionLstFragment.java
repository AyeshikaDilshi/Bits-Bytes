package com.itp.walletguard.view.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itp.walletguard.R;
import com.itp.walletguard.adapters.TransactionViewAdapter;
import com.itp.walletguard.entity.TransactionEntity;
import com.itp.walletguard.util.DialogUtil;
import com.itp.walletguard.util.WalletGuardApp;
import com.itp.walletguard.util.WalletGuardAppConstant;
import com.itp.walletguard.util.WalletGuardDB;
import com.itp.walletguard.view.CategoryActivity;
import com.itp.walletguard.view.LoginActivity;
import com.itp.walletguard.view.TransactionActivity;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class TransactionLstFragment extends Fragment {
    private static final String TAG = "TransactionLstFragment";

    private TransactionLstViewModel mViewModel;
    private Handler mHandler;

    private ProgressDialog mLoadingBar;

    private RecyclerView mRecyclerView;
    private TransactionViewAdapter mTransactionViewAdapter;
    private List<TransactionEntity> mTransactionEntityList;

    private TransactionEntity mSelectedTransactionEntity;
    private int mSwPosition;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(TransactionLstViewModel.class);
        mHandler = new Handler(Looper.getMainLooper());
        return inflater.inflate(R.layout.transaction_lst_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SwitchCompat switchCompat = view.findViewById(R.id.categ_switch);

        //mViewModel = new ViewModelProvider(this).get(TransactionLstViewModel.class);
        mRecyclerView = view.findViewById(R.id.tra_lst_rec_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(requireContext());
        mRecyclerView.setLayoutManager(linearLayout);

        mLoadingBar = new ProgressDialog(requireContext());
        mLoadingBar.setTitle("Fetch Transaction List/s");
        mLoadingBar.setMessage("Please Waite Retrieving Transaction List/s...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_list);

        fetchData(WalletGuardAppConstant.EXP_CATEG);

        switchCompat.setOnClickListener(view1 -> {
            if (switchCompat.isChecked()) {
                fetchData(WalletGuardAppConstant.INCOME_CATEG);
            } else {
                fetchData(WalletGuardAppConstant.EXP_CATEG);
            }
        });
    }

    private void fetchData(final short type) {
        if (WalletGuardApp.getUserEntity() == null) {
            FancyToast.makeText(requireContext(), "Unauthorized Access ,Restart App And Login Again !",
                    FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            Intent loginIntent = new Intent(requireContext(), LoginActivity.class);
            startActivity(loginIntent);
        }

        mLoadingBar.show();

        mViewModel.getTransactionLstByType(type, WalletGuardApp.getUserEntity().getUserId()).
                observe(getViewLifecycleOwner(), transactionEntities -> {
                  //  SystemClock.sleep(1000);
                    mTransactionEntityList = transactionEntities;

                    mTransactionViewAdapter = new TransactionViewAdapter(mTransactionEntityList, requireContext());
                    mRecyclerView.setAdapter(mTransactionViewAdapter);
                    mTransactionViewAdapter.setTransactionEntities(mTransactionEntityList);

                    if (mLoadingBar.isShowing())
                        mLoadingBar.dismiss();
                });

        swipeRemove();
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
                mSelectedTransactionEntity = mTransactionViewAdapter.getTransactionEnt(mSwPosition);
                switch (direction) {
                    case ItemTouchHelper.LEFT:

                        deleteTransaction();
                        break;

                    case ItemTouchHelper.RIGHT:
                        updateTransaction();
                        break;
                }

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .addSwipeLeftLabel("Remove From List")
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(requireContext(), R.color.teal_200))
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

    private void deleteTransaction() {
        if (mSelectedTransactionEntity == null) {
            DialogUtil.showAlert(requireContext(), "Delete Transaction",
                    "Select Transaction Is Required !", R.drawable.ic_delete);
            return;
        }
        new MaterialAlertDialogBuilder(requireContext()).setTitle("Delete Transaction")
                .setMessage("Are You Confirm To Delete ?")
                .setCancelable(false)
                .setIcon(R.drawable.ic_delete)
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    mTransactionViewAdapter.notifyDataSetChanged();
                })
                .setPositiveButton("Confirm", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    confirmDeleteTransaction(mSelectedTransactionEntity);
                })
                .show();
    }

    private void updateTransaction() {
        if (mSelectedTransactionEntity != null) {
            Intent transIntent = new Intent(requireContext(), TransactionActivity.class);
            //Bundle bundle = new Bundle();
            transIntent.putExtra(WalletGuardAppConstant.UPDATE_TRANS, mSelectedTransactionEntity);
            startActivity(transIntent);
            mTransactionViewAdapter.notifyDataSetChanged();
        } else {
            DialogUtil.showAlert(requireContext(), "Update Transaction !",
                    "Select Transaction Is Required", R.drawable.ic_update);
        }
    }

    private void confirmDeleteTransaction(final TransactionEntity transactionEntity) {
        mLoadingBar.setTitle("Delete Transaction");
        mLoadingBar.setMessage("Deleting Transaction...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_delete);
        mLoadingBar.show();
        WalletGuardDB.databaseWriterService.execute(() -> {
            try {
                SystemClock.sleep(1000);

                mViewModel.delete(transactionEntity);
                mHandler.post(() -> {
                    mLoadingBar.dismiss();
                    FancyToast.makeText(requireContext(), "Transaction Delete Success!", FancyToast.LENGTH_SHORT,
                            FancyToast.SUCCESS, false).show();
                });
            } catch (Exception e) {
                Log.e(TAG, "####### Delete Transaction System Error Occurred #######", e);
                mHandler.post(() -> {
                    mLoadingBar.dismiss();
                    mTransactionViewAdapter.notifyDataSetChanged();
                    DialogUtil.showAlert(requireContext(), "Delete Transaction Failed !",
                            "System Error Occurred\n" + e.getMessage(), R.drawable.ic_error);
                });
            }
        });
    }

}
