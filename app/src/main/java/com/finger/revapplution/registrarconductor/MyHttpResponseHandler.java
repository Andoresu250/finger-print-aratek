package com.finger.revapplution.registrarconductor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Revapplution on 14/03/2018.
 */

public class MyHttpResponseHandler extends TextHttpResponseHandler {

    String TAG = "MyHttpResponseHandler";
    Context context;

    public MyHttpResponseHandler(Context context) {
        this.context = context;
    }

    public MyHttpResponseHandler(String encoding, Context context) {
        super(encoding);
        this.context = context;
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        Log.e(TAG, "onFailure Message: " + responseString);
        Log.e(TAG, "onFailure: ", throwable);
        showDialog(responseString);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        Log.d(TAG, "onSuccess() returned: " + responseString);
    }

    private void showDialog(String message){
        if (context != null){

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Error")
                    .setMessage(message)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

            builder.show();

        }
    }
}
