package com.lukag.voznired.ui;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jude.swipbackhelper.SwipeBackHelper;
import com.lukag.voznired.R;

public class DisplayAppInfo extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        SwipeBackHelper.onCreate(this);

        Toolbar toolbar = findViewById(R.id.VozniRedToolbar);
        toolbar.setTitle(R.string.about);
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.ToolbarTitle);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);

        TextView version = findViewById(R.id.verzijaTW);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String ver = pInfo.versionName;
            version.setText(ver);
        } catch (Exception e) {
            e.printStackTrace();
            version.setText("err");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeBackHelper.onPostCreate(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SwipeBackHelper.onDestroy(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
