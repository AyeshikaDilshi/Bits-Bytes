package com.itp.walletguard.util;

import android.content.Context;
import android.graphics.drawable.Icon;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DialogUtil {
    public static void showAlert(Context context, final String title, final String msg, final int icon) {
        new MaterialAlertDialogBuilder(context).setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setIcon(icon)
                .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();

    }

    public static void showAlert(Context context, final int title, final int msg, Icon icon) {

    }
}