package com.phongbm.englock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {
    private Switch swtEnglockService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestDrawOverlayPermission();
        initViews();
        initListener();
    }

    private void requestDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivityForResult(intent, 1000);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestCode == 1000) {
                if (!Settings.canDrawOverlays(this)) {
                    finish();
                }
            }
        }
    }

    private void initViews() {
        swtEnglockService = (Switch) findViewById(R.id.swt_englock_service);

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);

        // Lấy ra trạng thái lưu trữ
        // để hiển thị lên giao diện Switch
        // Cách 1: swtEnglockService.setChecked(pref.getBoolean("status", false));
        // Cách 2
        if (pref.getBoolean("status", false)) {
            swtEnglockService.setChecked(true);
        } else {
            swtEnglockService.setChecked(false);
        }
    }

    private void initListener() {
        // Đăng ký lắng nghe sự kiện người dùng
        // chọn chế độ On hoặc Off
        // On: Cho phép bật chức năng khóa màn hình
        // Off: Hủy chức năng khóa màn hình
        swtEnglockService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { // On
                    // Kích hoạt EnglockService
                    Intent intent = new Intent(getBaseContext(), EnglockService.class);
                    startService(intent);

                    // Lưu trạng thái ON vào SharedPreferences
                    SharedPreferences pref = PreferenceManager
                            .getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("status", true);
                    editor.apply();
                } else { // Off
                    // Hủy kích hoạt EnglockService
                    Intent intent = new Intent(getBaseContext(), EnglockService.class);
                    stopService(intent);

                    SharedPreferences pref = PreferenceManager
                            .getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("status", false);
                    editor.apply();
                }
            }
        });
    }

}