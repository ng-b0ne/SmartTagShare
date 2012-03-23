package me.b0ne.android.hackathon2.app.smartagshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditTextActivity extends Activity {
    private EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_text_layout);
        
        editText = (EditText)findViewById(R.id.user_edit_text);
        
        Button button = (Button)findViewById(R.id.text_input_ok);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("TEXT", editText.getText().toString());
                setResult(MainActivity.REQUEST_EDITTEXT, intent);
                finish();
            }
        });
    }
}
