package com.itp.walletguard.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.itp.walletguard.R;
import com.itp.walletguard.entity.UserEntity;
import com.itp.walletguard.util.DialogUtil;
import com.itp.walletguard.util.WalletGuardAppConstant;
import com.itp.walletguard.util.WalletGuardDB;
import com.itp.walletguard.viewmodel.UserViewModel;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Date;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";


    private UserViewModel mUserViewModel;
    private ProgressDialog mLoadingBar;
    private Handler handler;

    private TextInputLayout txtInpUsername;
    private TextInputLayout txtInpEmail;
    private TextInputLayout txtIntPassword;
    private TextInputLayout txtInpConPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mLoadingBar = new ProgressDialog(this);
        handler = new Handler(Looper.getMainLooper());

        txtInpUsername = findViewById(R.id.edi_username);
        txtInpEmail = findViewById(R.id.edi_email);
        txtIntPassword = findViewById(R.id.edi_password);
        txtInpConPassword = findViewById(R.id.edi_con_password);
    }

    public void doSignUp(View view) {
        Log.i(TAG, "<----- Execute Do Sign Up in Login Activity ----->");

        String username = Objects.requireNonNull(txtInpUsername.getEditText()).getText().toString();
        if (TextUtils.isEmpty(username)) {
            txtInpUsername.setError("The Username Is Required!");
            return;
        }
        txtInpUsername.setError(null);

        String email = Objects.requireNonNull(txtInpEmail.getEditText()).getText().toString();
        if (TextUtils.isEmpty(email)) {
            txtInpEmail.setError("The Email Is Required!");
            return;
        }
        txtInpEmail.setError(null);

        if (!email.matches(WalletGuardAppConstant.EMAIL_VERIFICATION)) {
            txtInpEmail.setError("Invalid Email Format !");
            return;
        }
        txtInpEmail.setError(null);

        String password = Objects.requireNonNull(txtIntPassword.getEditText()).getText().toString();
        if (TextUtils.isEmpty(password)) {
            txtIntPassword.setError("The Password Is Required!");
            return;
        }
        txtIntPassword.setError(null);

        String confirmPassword = Objects.requireNonNull(txtInpConPassword.getEditText()).getText().toString();
        if (TextUtils.isEmpty(confirmPassword)) {
            txtInpConPassword.setError("The Password Confirmation Is Required!");
            return;
        }
        txtInpConPassword.setError(null);

        if (!confirmPassword.equals(password)) {
            txtInpConPassword.setError("The Both Password Should Match!");
            return;
        }
        txtInpConPassword.setError(null);
        new MaterialAlertDialogBuilder(this).setTitle(R.string.str_siup_title)
                .setMessage(R.string.str_siup_conf)
                .setCancelable(false)
                .setIcon(R.drawable.ic_face)
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("Confirm", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    UserEntity newUserEntity = new UserEntity(username, password, email.trim(), new Date(), (short) 1);
                    confirmSignUp(newUserEntity);
                })
                .show();

    }

    private void confirmSignUp(final UserEntity newUserEntity) {
        mLoadingBar.setTitle(R.string.str_siup_title);
        mLoadingBar.setMessage("Creating Account...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_face);
        mLoadingBar.show();
        WalletGuardDB.databaseWriterService.execute(() -> {
            try {
                SystemClock.sleep(1000);
                UserEntity userEntity = mUserViewModel.getUserByName(newUserEntity.getUserName());
                if (userEntity != null) {
                    handler.post(() -> {
                        mLoadingBar.dismiss();
                        DialogUtil.showAlert(this, "Wallet Guard Sign Up Failed",
                                "Entered Username\n" + newUserEntity.getUserName() + " Already Exist", R.drawable.ic_error);
                    });
                } else {
                    mUserViewModel.insertUser(newUserEntity);
                    handler.post(() -> {
                        mLoadingBar.dismiss();
                        FancyToast.makeText(this, "Wallet Guard Sign Up Success!", FancyToast.LENGTH_SHORT,
                                FancyToast.SUCCESS, false).show();
                        Intent homeIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(homeIntent);
                        finish();
                    });

                }
            } catch (Exception e) {
                Log.e(TAG, "####### Sign Up System Error Occured #######", e);
                handler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(this, "Sign Up Failed",
                            "System Error Occurred\n" + e.getMessage(), R.drawable.ic_error);
                });
            }
        });
    }
}