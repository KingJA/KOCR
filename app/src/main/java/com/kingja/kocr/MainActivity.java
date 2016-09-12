package com.kingja.kocr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import lib.kingja.ocr.KCamera;

public class MainActivity extends AppCompatActivity {

    private TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_result = (TextView) findViewById(R.id.tv_result);
    }

    public void onOCR(View view) {
        Intent intent = new Intent(this, KCamera.class);
        startActivityForResult(intent, 100);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == RESULT_OK) {
            String result = "";
            String name = data.getStringExtra("name");
            String card = data.getStringExtra("card");
            if (name != null) {
                result+=name+"\n";
            }
            if (card != null) {
                result+=card+"\n";
            }
            tv_result.setText(result);
        }
    }
}
