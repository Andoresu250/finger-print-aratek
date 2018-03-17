package com.finger.revapplution.registrarconductor;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.finger.revapplution.registrarconductor.models.Driver;
import com.finger.revapplution.registrarconductor.models.Person;
import com.finger.revapplution.registrarconductor.models.User;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import cn.com.aratek.fp.Bione;
import cn.com.aratek.fp.FingerprintImage;
import cn.com.aratek.fp.FingerprintScanner;
import cn.com.aratek.util.Result;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SearchDialogFragment.SearchListener{

    String TAG = "MainActivity";

    User user;

    ImageButton searchIBtn;
    Button sendBtn;
    Button enrollBtn;
    ImageView fingerIV;

    Person person;
    Driver driver;

    EditText identificationET;
    EditText nameET;
    EditText phoneET;
    EditText transportCompanyET;

    ProgressDialog mProgressDialog;
    private FingerprintScanner mScanner;
    private FingerprintTask mTask;
    private int mId;

    private Handler mHandler = getHandler();

    private static final int MSG_SHOW_ERROR = 0;
    private static final int MSG_SHOW_INFO = 1;
    private static final int MSG_UPDATE_IMAGE = 2;
    private static final int MSG_UPDATE_TEXT = 3;
    private static final int MSG_UPDATE_BUTTON = 4;
    private static final int MSG_UPDATE_SN = 5;
    private static final int MSG_UPDATE_FW_VERSION = 6;
    private static final int MSG_SHOW_PROGRESS_DIALOG = 7;
    private static final int MSG_DISMISS_PROGRESS_DIALOG = 8;
    private static final int MSG_FINISH_ACTIVITY = 9;

    private static final String FP_DB_PATH = "/sdcard/fp.db";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mScanner = FingerprintScanner.getInstance(this);

        searchIBtn = findViewById(R.id.searchIBtn);
        searchIBtn.setOnClickListener(this);
        sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);
        enrollBtn = findViewById(R.id.enrollBtn);
        enrollBtn.setOnClickListener(this);

        fingerIV = findViewById(R.id.fingerIV);

        identificationET = findViewById(R.id.identificationET);
        nameET = findViewById(R.id.nameET);
        phoneET = findViewById(R.id.phoneET);
        transportCompanyET = findViewById(R.id.transportCompanyET);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            user = (User) bundle.getSerializable("user");
        }

        enableControl(false);

        updateSingerTestText(-1, -1, -1, -1);

    }

    @Override
    protected void onPause() {
        closeDevice(false);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        openDevice();
    }

    private void openSearchDialog(){

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("SearchDialogFragment");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DialogFragment dialogFragment = SearchDialogFragment.newInstance(this, user);
        dialogFragment.show(ft, "SearchDialogFragment");

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.searchIBtn:
                openSearchDialog();
                break;
            case R.id.sendBtn:
                try {
                    sendFingerPrint();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.enrollBtn:
                enroll();
                break;
        }
    }

    @Override
    public void success(Person person) {
        Log.i(TAG, "success: llego");
        this.person = person;
        if (person.profile instanceof Driver){
            driver = (Driver) this.person.profile;
        }
        Log.i(TAG, "success: " + person);
        enrollBtn.setEnabled(true);
        setDriver();
    }

    private void setDriver(){
        if (person != null){
            nameET.setText(person.getFullName());
            phoneET.setText(person.phoneNumber);
            identificationET.setText(person.identification);
            if(driver != null){
                transportCompanyET.setText(driver.transportCompany.name);
            }
        }
    }

    private void sendFingerPrint() throws UnsupportedEncodingException {
        if (driver != null){
            if(driver.fingerprint != null){
                RestClient.addHeader("token", user.token);
                RestClient.put(this, "drivers/" + driver.id + "/set_fingerprint", "driver", driver, new MyHttpResponseHandler(this){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        super.onSuccess(statusCode, headers, responseString);
                        Toast.makeText(context, "Huella registrada exitosamente", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(this, "Primero hay que tomar la huella", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Primero hay que buscar al conductor", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler getHandler(){
        final String TAG = "FingerPrintHandler";
        return new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle;
                switch (msg.what){
                    case MSG_SHOW_ERROR: {
                        Log.i(TAG, "Type ERROR: ");
                        bundle = (Bundle) msg.obj;
                        Log.i(TAG, "Information: " + bundle.getString("information"));
                        Log.i(TAG, "Details: " + bundle.getString("details"));
                        break;
                    }
                    case MSG_SHOW_INFO: {
                        Log.i(TAG, "Type INFO: ");
                        bundle = (Bundle) msg.obj;
                        Log.i(TAG, "Information: " + bundle.getString("information"));
                        Log.i(TAG, "Details: " + bundle.getString("details"));
                        break;
                    }
                    case MSG_UPDATE_IMAGE: {
                        Log.i(TAG, "Type SET FINGER IMAGE: ");
                        Bitmap fingerBitmap = (Bitmap) msg.obj;
                        fingerIV.setImageBitmap(fingerBitmap);
                        break;
                    }
                    case MSG_UPDATE_TEXT: {
                        Log.i(TAG, "TYPE UPDATE TEXT: ");
                        String[] texts = (String[]) msg.obj;
                        String captureTime = texts[0];
                        String extractionTime = texts[1];
                        String generalizeTime = texts[2];
                        String verifyTime = texts[3];

                        Log.i(TAG, "captureTime : " + captureTime );
                        Log.i(TAG, "extractionTime : " + extractionTime );
                        Log.i(TAG, "generalizeTime : " + generalizeTime );
                        Log.i(TAG, "verifyTime : " + verifyTime );

                        break;
                    }
                    case MSG_UPDATE_BUTTON: {
                        Log.i(TAG, "TYPE UPDATE BUTTON: ");
                        //ENABLE DISABLE BUTTON
                        Boolean enable = (Boolean) msg.obj;

                        String enableMessage = enable ? "enable" : "disable";
                        Log.i(TAG, "Update: " + "buttons " + "enrollBtn, sendBtn, searchIBtn " + " are " + enableMessage);

                        enrollBtn.setEnabled(enable);
                        sendBtn.setEnabled(enable);
                        searchIBtn.setEnabled(enable);

                        break;
                    }
                    case MSG_UPDATE_SN: {
                        Log.i(TAG, "TYPE UPDATE SN: ");
                        String sn = (String) msg.obj;
                        Log.i(TAG, "SN: " + sn);
                        break;
                    }
                    case MSG_UPDATE_FW_VERSION: {
                        Log.i(TAG, "TYPE UPDATE FW VERSION: ");
                        String fwVersion = (String) msg.obj;
                        Log.i(TAG, "fw version: " + fwVersion);
                        break;
                    }
                    case MSG_SHOW_PROGRESS_DIALOG: {
                        Log.i(TAG, "TYPE SHOW PROGRESS DIALOG: ");
                        String[] info = (String[]) msg.obj;
                        String title = info[0];
                        String message = info[1];
                        mProgressDialog.setTitle(title);
                        mProgressDialog.setMessage(message);
                        mProgressDialog.show();
                        break;
                    }
                    case MSG_DISMISS_PROGRESS_DIALOG: {
                        Log.i(TAG, "TYPE CLOSE PROGRESS DIALOG: ");
                        mProgressDialog.dismiss();
                        break;
                    }
                    case MSG_FINISH_ACTIVITY: {
                        Log.i(TAG, "TYPE FINISH ACTIVITY: ");
                        finish();
                        break;
                    }
                }
            }
        };
    }

    private void enableControl(boolean enable) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_BUTTON, enable));
    }

    private void openDevice() {
        new Thread() {
            @Override
            public void run() {
                synchronized (this) {
                    showProgressDialog(getString(R.string.loading), getString(R.string.preparing_device));
                    int error;
                    if ((error = mScanner.powerOn()) != FingerprintScanner.RESULT_OK) {
                        showError(getString(R.string.fingerprint_device_power_on_failed), getFingerprintErrorString(error));
                    }
                    if ((error = mScanner.open()) != FingerprintScanner.RESULT_OK) {
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_SN, getString(R.string.fps_sn, "null")));
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_FW_VERSION, getString(R.string.fps_fw, "null")));
                        showError(getString(R.string.fingerprint_device_open_failed), getFingerprintErrorString(error));
                    } else {
                        Result res = mScanner.getSN();
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_SN, getString(R.string.fps_sn, (String) res.data)));
                        res = mScanner.getFirmwareVersion();
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_FW_VERSION, getString(R.string.fps_fw, (String) res.data)));
                        showInformation(getString(R.string.fingerprint_device_open_success), null);
                        enableControl(true);
                    }
                    if ((error = Bione.initialize(MainActivity.this, FP_DB_PATH)) != Bione.RESULT_OK) {
                        showError(getString(R.string.algorithm_initialization_failed), getFingerprintErrorString(error));
                    }
                    Log.i(TAG, "Fingerprint algorithm version: " + Bione.getVersion());
                    dismissProgressDialog();
                }
            }
        }.start();
    }

    private void closeDevice(final boolean finish) {
        new Thread() {
            @Override
            public void run() {
                synchronized (this) {
                    showProgressDialog(getString(R.string.loading), getString(R.string.closing_device));
                    enableControl(false);
                    int error;
                    if (mTask != null && mTask.getStatus() != AsyncTask.Status.FINISHED) {
                        mTask.cancel(false);
                        mTask.waitForDone();
                    }
                    if ((error = mScanner.close()) != FingerprintScanner.RESULT_OK) {
                        showError(getString(R.string.fingerprint_device_close_failed), getFingerprintErrorString(error));
                    } else {
                        showInformation(getString(R.string.fingerprint_device_close_success), null);
                    }
                    if ((error = mScanner.powerOff()) != FingerprintScanner.RESULT_OK) {
                        showError(getString(R.string.fingerprint_device_power_off_failed), getFingerprintErrorString(error));
                    }
                    if ((error = Bione.exit()) != Bione.RESULT_OK) {
                        showError(getString(R.string.algorithm_cleanup_failed), getFingerprintErrorString(error));
                    }
                    if (finish) {
                        finishActivity();
                    }
                    dismissProgressDialog();
                }
            }
        }.start();
    }

    private void enroll() {
        mTask = new FingerprintTask();
        mTask.execute("enroll");
    }

    private void verify() {
        mTask = new FingerprintTask();
        mTask.execute("verify");
    }

    private void identify() {
        mTask = new FingerprintTask();
        mTask.execute("identify");
    }

    private void showFingerprintImage() {
        mTask = new FingerprintTask();
        mTask.execute("show");
    }

    private void showProgressDialog(String title, String message) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_PROGRESS_DIALOG, new String[] { title, message }));
    }

    private void dismissProgressDialog() {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_DISMISS_PROGRESS_DIALOG));
    }

    private void showError(String info, String details) {
        Bundle bundle = new Bundle();
        bundle.putString("information", info);
        bundle.putString("details", details);
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_ERROR, bundle));
    }

    private void showInformation(String info, String details) {
        Bundle bundle = new Bundle();
        bundle.putString("information", info);
        bundle.putString("details", details);
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_INFO, bundle));
    }

    private void finishActivity() {
        mHandler.sendEmptyMessage(MSG_FINISH_ACTIVITY);
    }

    private void updateFingerprintImage(FingerprintImage fi) {
        byte[] fpBmp = null;
        Bitmap bitmap;
        if (fi == null || (fpBmp = fi.convert2Bmp()) == null || (bitmap = BitmapFactory.decodeByteArray(fpBmp, 0, fpBmp.length)) == null) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nofinger);
        }
        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_IMAGE, bitmap));
    }

    private void updateSingerTestText(long captureTime, long extractTime, long generalizeTime, long verifyTime) {
        String[] texts = new String[4];
        if (captureTime < 0) {
            texts[0] = getString(R.string.not_done);
        } else if (captureTime < 1) {
            texts[0] = "< 1ms";
        } else {
            texts[0] = captureTime + "ms";
        }

        if (extractTime < 0) {
            texts[1] = getString(R.string.not_done);
        } else if (extractTime < 1) {
            texts[1] = "< 1ms";
        } else {
            texts[1] = extractTime + "ms";
        }

        if (generalizeTime < 0) {
            texts[2] = getString(R.string.not_done);
        } else if (generalizeTime < 1) {
            texts[2] = "< 1ms";
        } else {
            texts[2] = generalizeTime + "ms";
        }

        if (verifyTime < 0) {
            texts[3] = getString(R.string.not_done);
        } else if (verifyTime < 1) {
            texts[3] = "< 1ms";
        } else {
            texts[3] = verifyTime + "ms";
        }

        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_TEXT, texts));
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private final static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private String getFingerprintErrorString(int error) {
        int strid;
        switch (error) {
            case FingerprintScanner.RESULT_OK:
                strid = R.string.operation_successful;
                break;
            case FingerprintScanner.RESULT_FAIL:
                strid = R.string.error_operation_failed;
                break;
            case FingerprintScanner.WRONG_CONNECTION:
                strid = R.string.error_wrong_connection;
                break;
            case FingerprintScanner.DEVICE_BUSY:
                strid = R.string.error_device_busy;
                break;
            case FingerprintScanner.DEVICE_NOT_OPEN:
                strid = R.string.error_device_not_open;
                break;
            case FingerprintScanner.TIMEOUT:
                strid = R.string.error_timeout;
                break;
            case FingerprintScanner.NO_PERMISSION:
                strid = R.string.error_no_permission;
                break;
            case FingerprintScanner.WRONG_PARAMETER:
                strid = R.string.error_wrong_parameter;
                break;
            case FingerprintScanner.DECODE_ERROR:
                strid = R.string.error_decode;
                break;
            case FingerprintScanner.INIT_FAIL:
                strid = R.string.error_initialization_failed;
                break;
            case FingerprintScanner.UNKNOWN_ERROR:
                strid = R.string.error_unknown;
                break;
            case FingerprintScanner.NOT_SUPPORT:
                strid = R.string.error_not_support;
                break;
            case FingerprintScanner.NOT_ENOUGH_MEMORY:
                strid = R.string.error_not_enough_memory;
                break;
            case FingerprintScanner.DEVICE_NOT_FOUND:
                strid = R.string.error_device_not_found;
                break;
            case FingerprintScanner.DEVICE_REOPEN:
                strid = R.string.error_device_reopen;
                break;
            case FingerprintScanner.NO_FINGER:
                strid = R.string.error_no_finger;
                break;
            case Bione.INITIALIZE_ERROR:
                strid = R.string.error_algorithm_initialization_failed;
                break;
            case Bione.INVALID_FEATURE_DATA:
                strid = R.string.error_invalid_feature_data;
                break;
            case Bione.BAD_IMAGE:
                strid = R.string.error_bad_image;
                break;
            case Bione.NOT_MATCH:
                strid = R.string.error_not_match;
                break;
            case Bione.LOW_POINT:
                strid = R.string.error_low_point;
                break;
            case Bione.NO_RESULT:
                strid = R.string.error_no_result;
                break;
            case Bione.OUT_OF_BOUND:
                strid = R.string.error_out_of_bound;
                break;
            case Bione.DATABASE_FULL:
                strid = R.string.error_database_full;
                break;
            case Bione.LIBRARY_MISSING:
                strid = R.string.error_library_missing;
                break;
            case Bione.UNINITIALIZE:
                strid = R.string.error_algorithm_uninitialize;
                break;
            case Bione.REINITIALIZE:
                strid = R.string.error_algorithm_reinitialize;
                break;
            case Bione.REPEATED_ENROLL:
                strid = R.string.error_repeated_enroll;
                break;
            case Bione.NOT_ENROLLED:
                strid = R.string.error_not_enrolled;
                break;
            default:
                strid = R.string.error_other;
                break;
        }
        return getString(strid);
    }

    @SuppressLint("StaticFieldLeak")
    private class FingerprintTask extends AsyncTask<String, Integer, Void> {
        private boolean mIsDone = false;

        @Override
        protected void onPreExecute() {
            enableControl(false);
        }

        @Override
        protected Void doInBackground(String... params) {
            long startTime, captureTime = -1, extractTime = -1, generalizeTime = -1, verifyTime = -1;
            FingerprintImage fingerprintImage = null;
            byte[] fingerPrintFeature = null, fingerPrintPerformanceTemp = null;
            Result result;

            String action = params[0];

            label:
            do {
                if (action.equals("show") || action.equals("enroll") || action.equals("verify") || action.equals("identify")) {
                    showProgressDialog(getString(R.string.loading), getString(R.string.press_finger));
                    int capRetry = 0;
                    mScanner.prepare();
                    do {
                        startTime = System.currentTimeMillis();
                        result = mScanner.capture();
                        captureTime = System.currentTimeMillis() - startTime;

                        fingerprintImage = (FingerprintImage) result.data;
                        int quality;
                        if (fingerprintImage != null) {
                            quality = Bione.getFingerprintQuality(fingerprintImage);
                            Log.i(TAG, "Fingerprint image quality is " + quality);
                            if (quality < 50 && capRetry < 3 && !isCancelled()) {
                                capRetry++;
                                continue;
                            }
                        }

                        if (result.error != FingerprintScanner.NO_FINGER || isCancelled()) {
                            break;
                        }
                    } while (true);
                    mScanner.finish();
                    if (isCancelled()) {
                        break;
                    }
                    if (result.error != FingerprintScanner.RESULT_OK) {
                        showError(getString(R.string.capture_image_failed), getFingerprintErrorString(result.error));
                        break;
                    }
                    updateFingerprintImage(fingerprintImage);
                }

                switch (action) {
                    case "show":
                        showInformation(getString(R.string.capture_image_success), null);
                        break;
                    case "enroll":
                        showProgressDialog(getString(R.string.loading), getString(R.string.enrolling));
                        break;
                    case "verify":
                        showProgressDialog(getString(R.string.loading), getString(R.string.verifying));
                        break;
                    case "identify":
                        showProgressDialog(getString(R.string.loading), getString(R.string.identifying));
                        break;
                }

                if (action.equals("enroll") || action.equals("verify") || action.equals("identify")) {
                    startTime = System.currentTimeMillis();

                    Log.i(TAG, "fingerPrintFeature: " + Arrays.toString(fingerPrintFeature));

                    result = Bione.extractFeature(fingerprintImage);
                    extractTime = System.currentTimeMillis() - startTime;
                    if (result.error != Bione.RESULT_OK) {
                        showError(getString(R.string.enroll_failed_because_of_extract_feature), getFingerprintErrorString(result.error));
                        break;
                    }
                    fingerPrintFeature = (byte[]) result.data;

                    Log.i(TAG, "doInBackground: " + "fingerPrintFeature: " + Arrays.toString(fingerPrintFeature));
                    String temp = bytesToHex(fingerPrintFeature);
                    driver.fingerprint = temp;
                    Log.i(TAG, "doInBackground: " + "hex string: " + temp);
                    byte[] bytesFromhex = hexToBytes(temp);
                    Log.i(TAG, "doInBackground: " + "bytes: " + Arrays.toString(bytesFromhex));
                    Log.i(TAG, "doInBackground: " + "equals? " + Arrays.equals(fingerPrintFeature, bytesFromhex));

                }

                switch (action) {
                    case "enroll": {
                        startTime = System.currentTimeMillis();
                        result = Bione.makeTemplate(fingerPrintFeature, fingerPrintFeature, fingerPrintFeature);
                        generalizeTime = System.currentTimeMillis() - startTime;
                        if (result.error != Bione.RESULT_OK) {
                            showError(getString(R.string.enroll_failed_because_of_make_template), getFingerprintErrorString(result.error));
                            break label;
                        }
                        fingerPrintPerformanceTemp = (byte[]) result.data;

                        int id = Bione.getFreeID();
                        if (id < 0) {
                            showError(getString(R.string.enroll_failed_because_of_get_id), getFingerprintErrorString(id));
                            break label;
                        }
                        int ret = Bione.enroll(id, fingerPrintPerformanceTemp);
                        if (ret != Bione.RESULT_OK) {
                            showError(getString(R.string.enroll_failed_because_of_error), getFingerprintErrorString(ret));
                            break label;
                        }
                        mId = id;
                        showInformation(getString(R.string.enroll_success), getString(R.string.enrolled_id, id));
                        break;
                    }
                    case "verify":
                        startTime = System.currentTimeMillis();
                        result = Bione.verify(mId, fingerPrintFeature);
//                        result = Bione.verify(myFingerPrint, fingerPrintFeature);
                        verifyTime = System.currentTimeMillis() - startTime;
                        if (result.error != Bione.RESULT_OK) {
                            showError(getString(R.string.verify_failed_because_of_error), getFingerprintErrorString(result.error));
                            break label;
                        }
                        if ((Boolean) result.data) {
                            showInformation(getString(R.string.fingerprint_match), getString(R.string.fingerprint_similarity, result.arg1));
                        } else {
                            showError(getString(R.string.fingerprint_not_match), getString(R.string.fingerprint_similarity, result.arg1));
                        }
                        break;
                    case "identify": {
                        startTime = System.currentTimeMillis();
                        int id = Bione.identify(fingerPrintFeature);
                        verifyTime = System.currentTimeMillis() - startTime;
                        if (id < 0) {
                            showError(getString(R.string.identify_failed_because_of_error), getFingerprintErrorString(id));
                            break label;
                        }
                        showInformation(getString(R.string.identify_match), getString(R.string.matched_id, id));
                        break;
                    }
                }
            } while (false);

            updateSingerTestText(captureTime, extractTime, generalizeTime, verifyTime);
            enableControl(true);
            dismissProgressDialog();
            mIsDone = true;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onCancelled() {
        }

        public void waitForDone() {
            while (!mIsDone) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
