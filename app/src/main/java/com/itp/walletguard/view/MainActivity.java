package com.itp.walletguard.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.itp.walletguard.R;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private static final int SPL_TIME = 3000;
    Animation topAnim, bottomAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Paper.init(this);
        ImageView appLogo = findViewById(R.id.app_logo);
        TextView txtAppName = findViewById(R.id.textAppName);
        TextView txtSLogan = findViewById(R.id.textSlogan);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        appLogo.setAnimation(topAnim);
        txtAppName.setAnimation(bottomAnim);
        txtSLogan.setAnimation(bottomAnim);

        new Handler().postDelayed(() -> {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View, String>(appLogo, "logo_image");
            pairs[1] = new Pair<View, String>(txtAppName, "txt_app_name");
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
            startActivity(loginIntent, activityOptions.toBundle());
            finish();
        }, SPL_TIME);
    }
}