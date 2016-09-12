//package lib.kingja.ocr;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.hardware.Camera.Parameters;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.yunmai.android.other.CameraManager;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
///**
// *
// */
//public class ACamera extends Activity implements SurfaceHolder.Callback {
//    /* 识别*/
//    public static final int REQUEST_CODE_RECOG = 113;
//    /*识别成功*/
//    public static final int RESULT_RECOG_SUCCESS = 103;
//    /* 识别失败*/
//    public static final int RESULT_RECOG_FAILED = 104;
//    private SurfaceView mSurfaceView;
//    private SurfaceHolder mSurfaceHolder;
//    private ImageView camera_shutter_a;
//    private Button camera_recog;
//    private ImageView camera_flash;
//    private CameraManager mCameraManager;
//    private List<String> flashList;
//    private int flashPostion = 0;
//    private byte[] idcardA = null;
//
//    private Handler mHandler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == 1) { // 抓取图像成功
//                idcardA = msg.getData().getByteArray("picData");
//            } else {
//                Toast.makeText(ACamera.this, R.string.camera_take_picture_error, Toast.LENGTH_SHORT).show();
//            }
//            camera_shutter_a.setEnabled(true);
//            mCameraManager.initDisplay();
//        }
//
//    };
//    private ProgressDialog dialogProgress;
//
//    /**
//     * Called when the activity is first created.
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_kcamera);
//        mCameraManager = new CameraManager(ACamera.this, mHandler);
//        initViews();
//    }
//
//    @Override
//    protected void onResume() {
//        // TODO Auto-generated method stub
//        idcardA = null;
//        super.onResume();
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // TODO Auto-generated method stub
//        mCameraManager.initDisplay();
//    }
//
//    private void initViews() {
//        // find view
//        camera_shutter_a = (ImageView) findViewById(R.id.iv_photo);
//        camera_recog = (Button) findViewById(R.id.camera_recog);
//        camera_flash = (ImageView) findViewById(R.id.iv_flash);
//        camera_shutter_a.setOnClickListener(mLsnClick);
//        camera_recog.setOnClickListener(mLsnClick);
//        camera_flash.setOnClickListener(mLsnClick);
//
//        mSurfaceView = (SurfaceView) findViewById(R.id.camera_preview);
//        mSurfaceHolder = mSurfaceView.getHolder();
//        mSurfaceHolder.addCallback(ACamera.this);
//        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        dialogProgress = new ProgressDialog(this);
//    }
//
//    private OnClickListener mLsnClick = new OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            if (v.getId() == R.id.iv_photo) {
//                camera_shutter_a.setEnabled(false);
//                mCameraManager.setTakeIdcardA();
//                mCameraManager.requestFocuse();
//                dialogProgress.show();
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        dialogProgress.dismiss();
//                        if (idcardA == null) {
//                            Toast.makeText(ACamera.this, "请拍摄证件正面", Toast.LENGTH_LONG).show();
//                            return;
//                        }
//                        Intent aRecognize2 = new Intent();
//                        aRecognize2.putExtra("idcardA", idcardA);
//                        setResult(RESULT_OK, aRecognize2);
//                        finish();
//                        Log.e("ACamera", idcardA.toString());
//                    }
//                }, 2000);
//            }
//
//            if (v.getId() == R.id.iv_flash) {
//                flashPostion++;
//                if (flashPostion < flashList.size()) {
//                    setFlash(flashList.get(flashPostion));
//                } else {
//                    flashPostion = 0;
//                    setFlash(flashList.get(flashPostion));
//                }
//            }
//
//        }
//
//    };
//
//    private void setFlash(String flashModel) {
//        mCameraManager.setCameraFlashMode(flashModel);
//        if (flashModel.equals(Parameters.FLASH_MODE_ON)) {
//            camera_flash.setImageResource(R.drawable.bg_flash_on);
//        } else if (flashModel.equals(Parameters.FLASH_MODE_OFF)) {
//            camera_flash.setImageResource(R.drawable.bg_flash_off);
//        } else {
//            camera_flash.setImageResource(R.drawable.bg_flash_auto);
//        }
//    }
//
//    private List<String> getSupportFlashModel() {
//        List<String> list = new ArrayList<String>();
//        if (mCameraManager.isSupportFlash(Parameters.FLASH_MODE_OFF)) {
//            list.add(Parameters.FLASH_MODE_OFF);
//        }
//        if (mCameraManager.isSupportFlash(Parameters.FLASH_MODE_ON)) {
//            list.add(Parameters.FLASH_MODE_ON);
//        }
//        if (mCameraManager.isSupportFlash(Parameters.FLASH_MODE_AUTO)) {
//            list.add(Parameters.FLASH_MODE_AUTO);
//        }
//        return list;
//    }
//
//    public void surfaceCreated(SurfaceHolder holder) {
//        try {
//            mCameraManager.openCamera(holder);
//            flashList = getSupportFlashModel();
//            if (flashList == null || flashList.size() == 0) {
//                Toast.makeText(this, "闪光灯无法设置", Toast.LENGTH_SHORT).show();
//                camera_flash.setEnabled(false);
//            } else {
//                setFlash(flashList.get(0));
//            }
//            if (!mCameraManager.isSupportAutoFocus()) {
//                Toast.makeText(getBaseContext(), "不支持自动对焦！", Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//            Toast.makeText(ACamera.this, R.string.camera_open_error,
//                    Toast.LENGTH_SHORT).show();
//            finish();
//        }
//    }
//
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        if (width > height) {
//            mCameraManager.setPreviewSize(width, height);
//        } else {
//            mCameraManager.setPreviewSize(height, width);
//        }
//        mCameraManager.initDisplay();
//    }
//
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        mCameraManager.closeCamera();
//    }
//}