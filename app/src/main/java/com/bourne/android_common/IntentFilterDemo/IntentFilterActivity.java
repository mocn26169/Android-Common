package com.bourne.android_common.IntentFilterDemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bourne.android_common.R;

public class IntentFilterActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_filter);
    }

    public void onClickAction(View view) {
        String ACTION_PACKAGEMANAGER="com.bourne.ACTION_PACKAGEMANAGER";
        String CATEGORY_PACKAGEMANAGER="com.bourne.CATEGORY_PACKAGEMANAGER";

        Intent intent = new Intent();
        intent.setAction(ACTION_PACKAGEMANAGER);
        intent.addCategory(CATEGORY_PACKAGEMANAGER);
        intent.setDataAndType(Uri.parse("xiazdong://www.xiazdong.com/xia"), "text/plain");  //匹配了text/*

        startActivity(intent);
    }
}
