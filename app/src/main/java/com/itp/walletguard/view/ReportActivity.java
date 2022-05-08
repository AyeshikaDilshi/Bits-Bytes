package com.itp.walletguard.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.itp.walletguard.R;
import com.itp.walletguard.entity.TransactionEntity;
import com.itp.walletguard.util.DialogUtil;
import com.itp.walletguard.util.PdfDocumentAdapter;
import com.itp.walletguard.util.WalletGuardApp;
import com.itp.walletguard.util.WalletGuardAppConstant;
import com.itp.walletguard.util.WalletGuardDB;
import com.itp.walletguard.viewmodel.ReportViewModel;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class ReportActivity extends AppCompatActivity {
    private static final String TAG = "ReportActivity";

    private ReportViewModel mReportViewModel;
    private ProgressDialog mLoadingBar;
    private Handler mHandler;

    private TextView txtFrmDate;
    private TextView txtToDate;
    private RadioGroup radioGroupCatType;
    private ImageButton btnFrmDate;
    private ImageButton btnToDate;

    private Date mFromDate;
    private Date mToDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        mReportViewModel = new ViewModelProvider(this).get(ReportViewModel.class);
        mHandler = new Handler(Looper.getMainLooper());
        mLoadingBar = new ProgressDialog(this);

        radioGroupCatType = findViewById(R.id.rbd_cat_type);
        txtFrmDate = findViewById(R.id.txt_frm_date);
        txtToDate = findViewById(R.id.txt_to_date);
        btnFrmDate = findViewById(R.id.btn_frm_dpt);
        btnToDate = findViewById(R.id.btn_open_dpt);

        initFrmDatePicker();
        initToDatePicker();
    }

    public void generateReport(View view) {
        Log.d(TAG, "<---- Generate Report Called ----->");

        if (WalletGuardApp.getUserEntity() == null) {
            FancyToast.makeText(this, "Unauthorized Access ,Restart App And Login Again !",
                    FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            Intent loginIntent = new Intent(ReportActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        if (mFromDate == null) {
            DialogUtil.showAlert(this, "Generate Report",
                    "Select Start Date", R.drawable.ic_error);
            return;
        }

        if (mToDate == null) {
            DialogUtil.showAlert(this, "Generate Report",
                    "Select End Date", R.drawable.ic_error);
            return;
        }
//        if ((new Date().compareTo(mFromDate) > 0) || (new Date().compareTo(mToDate) > 0)) {
//            DialogUtil.showAlert(this, "Generate Report",
//                    "Start Date Or End Date Should Before Or Equal Today ", R.drawable.ic_error);
//            return;
//        }
        if (mFromDate.compareTo(mToDate) > 0) {
            DialogUtil.showAlert(this, "Generate Report",
                    "Start Date Should Before End Date", R.drawable.ic_error);
            return;
        }
        generateReport(mFromDate, mToDate, getSelectedCatType());
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
                    case "3":
                        state = new Short("3");
                        break;
                    default:
                        state = new Short("15");
                        break;
                }
            }

        }
        return state;
    }

    private void initFrmDatePicker() {
        Log.d(TAG, "<---- Init From Date Picker Called ---->");

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("Select From Date:");

        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        btnFrmDate.setOnClickListener(
                v -> materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER"));

        materialDatePicker.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Long>) selection -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);
            txtFrmDate.setText(materialDatePicker.getHeaderText());
            mFromDate = calendar.getTime();
        });

    }

    private void initToDatePicker() {
        Log.d(TAG, "<---- Init To Date Picker Called ---->");

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("Select From Date:");

        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        btnToDate.setOnClickListener(
                v -> materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER_two"));

        materialDatePicker.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Long>) selection -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);
            txtToDate.setText(materialDatePicker.getHeaderText());
            mToDate = calendar.getTime();
        });

    }

    private void generateReport(final Date frmDate, final Date toDate, final short type) {
        mLoadingBar.setTitle("Generate Report");
        mLoadingBar.setMessage("Generating Report...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_rep);
        mLoadingBar.show();
        WalletGuardDB.databaseWriterService.execute(() -> {
            try {
                List<TransactionEntity> transactionEntities = null;
                if (type == 1) {
                    transactionEntities = mReportViewModel.getTransactionForRpt(frmDate, toDate, WalletGuardApp.getUserEntity().getUserId());
                }
                if (transactionEntities != null) {
                    if (transactionEntities.size() <= 0) {
                        mHandler.post(() -> {
                            mLoadingBar.dismiss();
                            FancyToast.makeText(this, "Couldn't Find Any Transaction to Generate Report!",
                                    FancyToast.LENGTH_SHORT,
                                    FancyToast.ERROR, false).show();
                        });
                    } else {
                        String tittle = "Income Vs Expense Report For" +
                                new SimpleDateFormat("yyyy-MMM-dd", Locale.getDefault()).format(frmDate) +
                                " To " + new SimpleDateFormat("yyyy-MMM-dd", Locale.getDefault()).format(toDate);
                        printInitReport(tittle, transactionEntities);
                    }
                } else {
                    mHandler.post(() -> {
                        mLoadingBar.dismiss();
                        FancyToast.makeText(this, "Couldn't Find Any Transaction to Generate Report!",
                                FancyToast.LENGTH_SHORT,
                                FancyToast.ERROR, false).show();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "####### Generate Report System Error Occured #######", e);
                mHandler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(this, "Generate Report Failed !",
                            "System Error Occurred\n" + e.getMessage(), R.drawable.ic_error);
                });
            }
        });
    }

    private void printInitReport(final String title, List<TransactionEntity> transactionEntities) {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String targetPdf;
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(842, 595, 1).create();
        int pageWidth = pageInfo.getPageWidth();
        int pageHeight = pageInfo.getPageHeight();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        //<----------------- Start  Report Tittle Area ------------------------>
        Paint tittlePaint = new Paint();
        tittlePaint.setColor(Color.BLACK);
        tittlePaint.setTextSize(25.5f);
        tittlePaint.setTextAlign(Paint.Align.CENTER);
        tittlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
        canvas.drawText(getString(R.string.app_name), (float) pageWidth / 2, 25.0f, tittlePaint);

        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(12.5f);
        canvas.drawText(title, (float) pageWidth / 2, 42.0f, paint);
        canvas.drawText(WalletGuardApp.getUserEntity().getEmail(), (float) pageWidth / 2, 55.0f, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        // canvas.drawText(getString(R.string.org_reg), pageWidth - 20, 25.0f, paint);
        //  canvas.drawText(getString(R.string.org_contact), pageWidth - 20, 55.0f, paint);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Create By, ".concat(WalletGuardApp.getUserEntity().getUserName()),
                20, 25.0f, paint);
        canvas.drawText("Create Date, ".concat(new SimpleDateFormat("yy-MM-dd hh:mm a", Locale.getDefault()).format(new Date())),
                20, 55.0f, paint);
        canvas.drawLine(20.0f, 61.0f, pageWidth - 20, 61.0f, tittlePaint);
        canvas.drawLine(20.0f, 66.0f, pageWidth - 20, 66.0f, tittlePaint);
        //<----------------- End  Report Tittle Area ------------------------>

        //<----------------- Start  Report Table Header Area ------------------------>


        Rect tblRec = new Rect(20, 85, pageWidth - 20, 125);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        canvas.drawRect(tblRec, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3.0f);
        paint.setColor(Color.BLACK);
        canvas.drawRect(tblRec, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(17.0f);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        canvas.drawText("Date", 45.0f, 105.0f, paint);//30
        canvas.drawText("Income", 150.0f, 105.0f, paint);//30
        canvas.drawText("Value", 360.0f, 105.0f, paint);//340
        canvas.drawText("Date", 475.0f, 105.0f, paint);//30
        canvas.drawText("Expenses", 595.0f, 105.0f, paint);//490
        canvas.drawText("Value", 735.0f, 105.0f, paint);//640

        canvas.drawLine(110, 85, 110, 125, paint);
        canvas.drawLine(292, 85, 292, 125, paint);
        canvas.drawLine(410, 85, 410, 125, paint);
        canvas.drawLine(525, 85, 525, 125, paint);
        canvas.drawLine(700, 85, 700, 125, paint);


        //<----------------- End  Report Table Header Area ------------------------>


        paint.setTextSize(12.0f);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.LEFT);
        float indent = 140.0f;
        float incomeIndent = 140.0f;
        float expIndent = 140.0f;
        float startIndent = 125.0f;
        int pageNumber = 1;
        short prevType;


        for (TransactionEntity trans : transactionEntities) {
            if ((int) indent >= (pageInfo.getPageHeight() - 60)) {
                paint.setColor(Color.BLACK);
                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawLine(20.0f, pageInfo.getPageHeight() - 50, pageWidth - 20, pageInfo.getPageHeight() - 50, paint);
                canvas.drawText(getString(R.string.rep_pg_no) + pageNumber, pageWidth - 20, pageInfo.getPageHeight() - 35, paint);

                canvas.drawLine(20.0f, startIndent, 20.0f, pageInfo.getPageHeight() - 50, tittlePaint);
                canvas.drawLine(110.0f, startIndent, 110.0f, indent, tittlePaint);
                canvas.drawLine(292.0f, startIndent, 292.0f, indent, tittlePaint);
                canvas.drawLine(410.0f, startIndent, 410.0f, indent, tittlePaint);
                canvas.drawLine(525.0f, startIndent, 525.0f, indent, tittlePaint);
                canvas.drawLine(700.0f, startIndent, 700.0f, indent, tittlePaint);
                canvas.drawLine(pageWidth - 20, startIndent, pageWidth - 20, pageInfo.getPageHeight() - 50, tittlePaint);
                startIndent = 0.0f;

                document.finishPage(page);
                indent = 40.0f;
                incomeIndent = 40.0f;
                expIndent = 40.0f;
                pageNumber++;
                pageInfo = new PdfDocument.PageInfo.Builder(842, 595, pageNumber).create();
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                paint.setTextAlign(Paint.Align.LEFT);
            }

            prevType = trans.getCategoryType();
            if (trans.getCategoryType() == WalletGuardAppConstant.INCOME_CATEG) {
                canvas.drawText(format.format(trans.getTransactionDate()), 25.0f, incomeIndent, paint);
                canvas.drawText(trans.getName(), 115.0f, incomeIndent, paint);
                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(String.format(Locale.getDefault(), "%.2f", trans.getValue()), 405.0f, incomeIndent, paint);
                incomeIndent += 25.0f;
            } else {
                canvas.drawText(format.format(trans.getTransactionDate()), 415.0f, expIndent, paint);
                canvas.drawText(trans.getName(), 530.0f, expIndent, paint);
                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(String.format(Locale.getDefault(), "%.2f", trans.getValue()), pageWidth - 25.0f, expIndent, paint);
                expIndent += 25.0f;
            }


            //canvas.drawText(String.format(Locale.getDefault(), "%.2f", stock.getPurchasePrice()), 470.0f, indent, paint);
            //  canvas.drawText(String.format(Locale.getDefault(), "%.2f", stock.getMrp()), 620.0f, indent, paint);
            //  canvas.drawText(String.format(Locale.getDefault(), "%.2f", stock.getQty()).concat(" - ").concat(stock.getUomChar()), (float) pageWidth - 25, indent, paint);
            canvas.drawLine(20.0f, indent + 5, pageWidth - 20, indent + 5, tittlePaint);
            indent += 25.0f;
            paint.setTextAlign(Paint.Align.LEFT);

        }


        // finish the page content and Title of Report
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawLine(20.0f, pageInfo.getPageHeight() - 50, pageWidth - 20, pageInfo.getPageHeight() - 50, paint);
        canvas.drawText(getString(R.string.app_slogan) + " " + getString(R.string.rep_pg_no) + pageNumber, pageWidth - 20, pageInfo.getPageHeight() - 35, paint);

        canvas.drawLine(20.0f, startIndent, 20.0f, indent, tittlePaint);
        canvas.drawLine(110.0f, startIndent, 110.0f, indent, tittlePaint);
        canvas.drawLine(292.0f, startIndent, 292.0f, indent, tittlePaint);
        canvas.drawLine(410.0f, startIndent, 410.0f, indent, tittlePaint);
        canvas.drawLine(525.0f, startIndent, 525.0f, indent, tittlePaint);
        canvas.drawLine(700.0f, startIndent, 700.0f, indent, tittlePaint);
        canvas.drawLine(pageWidth - 20, startIndent, pageWidth - 20, indent, tittlePaint);
        canvas.drawLine(20.0f, indent, pageWidth - 20, indent, tittlePaint);

        document.finishPage(page);
        // write the document content
        try {
            String dirName = new SimpleDateFormat("yy_MM_dd", Locale.getDefault()).format(new Date()) + "/";
            String directory_path = Objects.requireNonNull(this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)).getPath() + "/reports/Inc_Vs_Exp/" + dirName;
            File file = new File(directory_path);
            if (!file.exists()) {
                boolean isMkDir = file.mkdirs();
                Log.d(TAG, "Create New Directory State--> " + isMkDir);
            }
            targetPdf = directory_path + new SimpleDateFormat("yy_MM_dd_hh_mm_ss", Locale.getDefault()).format(new Date()) + ".pdf";
            File filePath = new File(targetPdf);

            document.writeTo(new FileOutputStream(filePath));
            printReport(targetPdf, title);

        } catch (IOException ie) {
            Log.e(TAG, "Document Save Error--> ", ie);
            mHandler.post(() -> {
                mLoadingBar.dismiss();
                DialogUtil.showAlert(this, title, "Error Occurred\n" + ie.getMessage(), R.drawable.ic_error);
            });
        } catch (Exception e) {
            Log.e(TAG, "Document Generate Error--> ", e);
            mHandler.post(() -> {
                mLoadingBar.dismiss();
                DialogUtil.showAlert(this, title, "Error Occurred\n" + e.getMessage(), R.drawable.ic_error);
            });
        } finally {
            document.close();
            mHandler.post(() -> {
                mLoadingBar.dismiss();
            });
        }
    }

    private void printReport(String path, final String title) {
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        try {
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(path);
            if (printManager != null) {
                PrintAttributes attrib = new PrintAttributes.Builder().
                        setMediaSize(PrintAttributes.MediaSize.ISO_A4.asLandscape()).
                        build();
                printManager.print("income_vs_expense", printDocumentAdapter, attrib);
            }
        } catch (Exception e) {
            Log.e(TAG, "Print Manager Print Error--> ", e);
            mHandler.post(() -> {
                mLoadingBar.dismiss();
                DialogUtil.showAlert(this, title, "Error Occurred\n" + e.getMessage(), R.drawable.ic_error);
            });

        }
    }

}