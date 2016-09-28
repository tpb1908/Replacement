package com.tpb.timetable.Home;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.tpb.timetable.Setup.Collectors.TermCollector;
import com.tpb.timetable.Utils.UIHelper;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIHelper.getColorResources(this, null);
        getWindow().getDecorView().getBackground().setColorFilter(
                UIHelper.getPrimary(),
                PorterDuff.Mode.SRC_ATOP);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Splash.this, TermCollector.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, 150);
    }
}
