package com.finger.revapplution.registrarconductor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.finger.revapplution.registrarconductor.models.LoginUser;
import com.finger.revapplution.registrarconductor.models.Profile;
import com.finger.revapplution.registrarconductor.models.RuntimeTypeAdapterFactory;
import com.finger.revapplution.registrarconductor.models.SuperAdmin;
import com.finger.revapplution.registrarconductor.models.User;
import com.finger.revapplution.registrarconductor.models.Utils;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    String TAG = "LoginActivity";

    ProgressBar progressBar;
    TextView messageTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progressBar);
        messageTV = findViewById(R.id.messageTV);

        try {
            login();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            showErrorMessage("hay una exepcion");
        }


    }



    private void login() throws UnsupportedEncodingException {

        progressBar.setVisibility(View.VISIBLE);

        LoginUser loginUser = new LoginUser("admin", "12345678");

        RestClient.post(this, "sessions", loginUser, new MyHttpResponseHandler(this){
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressBar.setVisibility(View.GONE);
                showErrorMessage("Fallo statusCode: " + statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                progressBar.setVisibility(View.GONE);



                final RuntimeTypeAdapterFactory<Profile> typeFactory = RuntimeTypeAdapterFactory
                        .of(Profile.class, "type")
                        .registerSubtype(SuperAdmin.class, "SuperAdmin");

                Gson gson = Utils.gsonBuilderWithDate().registerTypeAdapterFactory(typeFactory).create();

                User user = gson.fromJson(responseString, User.class);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    private void showErrorMessage(String message){
        messageTV.setVisibility(View.VISIBLE);
        messageTV.setText(message);
    }
}
