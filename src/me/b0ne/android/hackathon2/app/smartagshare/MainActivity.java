package me.b0ne.android.hackathon2.app.smartagshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
    public static final int REQUEST_EDITTEXT = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void gotoGallery(View v) {
        Intent intent = new Intent(this, SmartTagAppActivity.class);
        intent.putExtra("TYPE", "image");
        startActivity(intent);
    }

    public void gotoPutText(View v) {
        Intent intent = new Intent(this, EditTextActivity.class);
        startActivityForResult(intent, REQUEST_EDITTEXT);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == REQUEST_EDITTEXT) {
            if (data != null) {
                String text = data.getStringExtra("TEXT");
                Intent intent = new Intent(this, SmartTagAppActivity.class);
                intent.putExtra("TYPE", "text");
                intent.putExtra("TEXT", text);
                startActivity(intent);
            }
        }
    }
}
