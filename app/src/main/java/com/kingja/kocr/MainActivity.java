package com.kingja.kocr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.kingja.ocr.KCamera;
import lib.kingja.ocr.OCRUtil;

public class MainActivity extends AppCompatActivity {

    private TextView tv_result;
    private ImageView iv_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_result = (TextView) findViewById(R.id.tv_result);
        iv_img = (ImageView) findViewById(R.id.iv_img);
    }

    public void onOCR(View view) {
        KCamera.GoCamera(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == RESULT_OK) {
            String result = "";
            String name = data.getStringExtra("name");
            String card = data.getStringExtra("card");
            String imgBase64 = data.getStringExtra("img");
            if (name != null) {
                result += name + "\n";
            }
            if (card != null) {
                result += card + "\n";
            }
            if (imgBase64 != null) {
                iv_img.setImageBitmap(OCRUtil.base64ToBitmap(imgBase64));
            }
            tv_result.setText(result);
        }
    }
}
