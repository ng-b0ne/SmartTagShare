package me.b0ne.android.hackathon2.app.smartagshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void gotoGallery(View v) {
        Intent intent = new Intent(this, SmartTagAppActivity.class);
        startActivity(intent);
    }

    public void gotoPutText(View v) {
        
    }
}
