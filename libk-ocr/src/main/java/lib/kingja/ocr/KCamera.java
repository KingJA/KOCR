package lib.kingja.ocr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunmai.android.engine.OcrEngine;
import com.yunmai.android.other.CameraManager;
import com.yunmai.android.vo.IDCard;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description：TODO
 * Create Time：2016/9/12 15:28
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class KCamera extends Activity implements SurfaceHolder.Callback, View.OnClickListener {
    private CameraManager mCameraManager;
    private List<String> flashList;
    private int flashPostion = 0;
    private byte[] idcardA = null;//身份证图片信息
    private String TAG = getClass().getSimpleName();
    private IDCard idCard;//身份证数据信息
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Log.e(TAG, "handleMessage: " + msg.what);
            if (msg.what == 1) { // 抓取图像成功
                idcardA = msg.getData().getByteArray("picData");
            } else {
                Toast.makeText(KCamera.this, R.string.camera_take_picture_error, Toast.LENGTH_SHORT).show();
            }
            mIvPhoto.setEnabled(true);
            mCameraManager.initDisplay();
            onRecogn();
        }

    };

    private ImageView mIvFlash;
    private ImageView mIvPhoto;
    private SurfaceView mCameraPreview;
    private ScreenSetting mCameraScreenSetting;
    private TextView mCameraI;
    private TextView mTip;
    private SurfaceHolder mSurfaceHolder;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kcamera);
        mCameraManager = new CameraManager(this, mHandler);
        initView();
        initData();
    }

    private void initView() {
        mIvFlash = (ImageView) findViewById(R.id.iv_flash);
        mIvPhoto = (ImageView) findViewById(R.id.iv_photo);

        mCameraPreview = (SurfaceView) findViewById(R.id.camera_preview);
        mCameraScreenSetting = (ScreenSetting) findViewById(R.id.camera_screen_setting);
        mCameraI = (TextView) findViewById(R.id.camera_i);
        mTip = (TextView) findViewById(R.id.tv_tip);
    }

    private void initData() {
        mIvFlash.setOnClickListener(this);
        mIvPhoto.setOnClickListener(this);
        mSurfaceHolder = mCameraPreview.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }


    private void onRecogn() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OcrEngine ocrEngine = new OcrEngine();
                try {
                    byte[] data = idcardA;
                    saveCardImg2SD(data);
                    String headPath = OCRUtil.getFilePath("OCR", new Date().toString(), "bmp", KCamera.this);//头像路径
                    Log.e("recognize==>", "开始识别");
                    idCard = ocrEngine.recognize(KCamera.this, data, null, headPath);//;解析身份证信息并保存头像

                    Log.e("recognize==>", "结束识别");
                    if (idCard.getRecogStatus() == OcrEngine.RECOG_OK) {
                        mResultHandler.sendMessage(mResultHandler.obtainMessage(OcrEngine.RECOG_OK, headPath));
                    } else {
                        mResultHandler.sendEmptyMessage(idCard.getRecogStatus());
                    }
                } catch (Exception e) {
                    mResultHandler.sendEmptyMessage(OcrEngine.RECOG_FAIL);
                }
            }
        }).start();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCameraManager.openCamera(holder);
            flashList = getSupportFlashModel();
            if (flashList == null || flashList.size() == 0) {
                Toast.makeText(this, "闪光灯无法设置", Toast.LENGTH_SHORT).show();
                mIvFlash.setEnabled(false);
            } else {
                setFlash(flashList.get(0));
            }
            if (!mCameraManager.isSupportAutoFocus()) {
                Toast.makeText(getBaseContext(), "不支持自动对焦！", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, R.string.camera_open_error,
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (width > height) {
            mCameraManager.setPreviewSize(width, height);
        } else {
            mCameraManager.setPreviewSize(height, width);
        }
        mCameraManager.initDisplay();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCameraManager.closeCamera();
    }

    private List<String> getSupportFlashModel() {
        List<String> list = new ArrayList<String>();
        if (mCameraManager.isSupportFlash(Camera.Parameters.FLASH_MODE_OFF)) {
            list.add(Camera.Parameters.FLASH_MODE_OFF);
        }
        if (mCameraManager.isSupportFlash(Camera.Parameters.FLASH_MODE_ON)) {
            list.add(Camera.Parameters.FLASH_MODE_ON);
        }
        if (mCameraManager.isSupportFlash(Camera.Parameters.FLASH_MODE_AUTO)) {
            list.add(Camera.Parameters.FLASH_MODE_AUTO);
        }
        return list;
    }

    private void setFlash(String flashModel) {
        mCameraManager.setCameraFlashMode(flashModel);
        if (flashModel.equals(Camera.Parameters.FLASH_MODE_ON)) {
            mIvFlash.setImageResource(R.drawable.bg_flash_on);
        } else if (flashModel.equals(Camera.Parameters.FLASH_MODE_OFF)) {
            mIvFlash.setImageResource(R.drawable.bg_flash_off);
        } else {
            mIvFlash.setImageResource(R.drawable.bg_flash_auto);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_photo) {
            mIvPhoto.setEnabled(false);
            mCameraManager.setTakeIdcardA();
            mCameraManager.requestFocuse();


        } else if (v.getId() == R.id.iv_flash) {
            flashPostion++;
            if (flashPostion < flashList.size()) {
                setFlash(flashList.get(flashPostion));
            } else {
                flashPostion = 0;
                setFlash(flashList.get(flashPostion));
            }
        }
    }


    private void saveCardImg2SD(byte[] data) {
        Bitmap cardBitmap = null;
        try {
//            cardBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);


            String cardImgPath =  OCRUtil.getFilePath("OCR", new Date().toString(), "jpg", KCamera.this);
            cardBitmap= OCRUtil.compressScaleFromF2B(data);

            File imageFile = new File(cardImgPath);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imageFile));
            cardBitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos);
            bos.flush();
            bos.close();

        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (cardBitmap != null) {
                cardBitmap.recycle();
                cardBitmap = null;
            }
        }
    }


    private Handler mResultHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OcrEngine.RECOG_FAIL:
                    Toast.makeText(KCamera.this, R.string.reco_dialog_blur, Toast.LENGTH_SHORT).show();
                    break;
                case OcrEngine.RECOG_BLUR:
                    Toast.makeText(KCamera.this, R.string.reco_dialog_blur, Toast.LENGTH_SHORT).show();
                    break;
                case OcrEngine.RECOG_OK:
                    Intent intent = new Intent();
                    intent.putExtra("name", idCard.getName());
                    intent.putExtra("card", idCard.getCardNo());
                    intent.putExtra("sex", idCard.getSex());
                    intent.putExtra("birth", idCard.getBirth());
                    intent.putExtra("address", idCard.getAddress());
                    intent.putExtra("img", url);//TODO 压缩身份证并转成Base64格式
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case OcrEngine.RECOG_IMEI_ERROR:
                    Toast.makeText(KCamera.this, R.string.reco_dialog_imei, Toast.LENGTH_SHORT).show();
                    break;
                case OcrEngine.RECOG_FAIL_CDMA:
                    Toast.makeText(KCamera.this, R.string.reco_dialog_cdma, Toast.LENGTH_SHORT).show();
                    break;
                case OcrEngine.RECOG_LICENSE_ERROR:
                    Toast.makeText(KCamera.this, R.string.reco_dialog_licens, Toast.LENGTH_SHORT).show();
                    break;
                case OcrEngine.RECOG_TIME_OUT:
                    Toast.makeText(KCamera.this, R.string.reco_dialog_time_out, Toast.LENGTH_SHORT).show();
                    break;
                case OcrEngine.RECOG_ENGINE_INIT_ERROR:
                    Toast.makeText(KCamera.this, R.string.reco_dialog_engine_init, Toast.LENGTH_SHORT).show();
                    break;
                case OcrEngine.RECOG_COPY:
                    Toast.makeText(KCamera.this, R.string.reco_dialog_fail_copy, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(KCamera.this, R.string.reco_dialog_blur, Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };
}
