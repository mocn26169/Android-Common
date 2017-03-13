package com.bourne.android_common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bourne.android_common.NetworkRequestDemo.NetworkRequestActivity;
import com.bourne.android_common.PackageManagerDemo.PackagerManagerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toPackagerManagerActivity(null);
    }

    private void toActivity(Context _context, Class<? extends Activity> _class) {
        Intent intent = new Intent(_context, _class);
        startActivity(intent);
    }

    public void toNetworkRequestActivity(View view) {
        toActivity(this, NetworkRequestActivity.class);
    }

    public void toPackagerManagerActivity(View view) {
        toActivity(this, PackagerManagerActivity.class);
    }


}
