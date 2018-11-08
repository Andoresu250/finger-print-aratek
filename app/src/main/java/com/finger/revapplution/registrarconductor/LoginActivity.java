package com.finger.revapplution.registrarconductor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.finger.revapplution.registrarconductor.models.Driver;
import com.finger.revapplution.registrarconductor.models.LoginUser;
import com.finger.revapplution.registrarconductor.models.Person;
import com.finger.revapplution.registrarconductor.models.PersonProfile;
import com.finger.revapplution.registrarconductor.models.Profile;
import com.finger.revapplution.registrarconductor.models.RuntimeTypeAdapterFactory;
import com.finger.revapplution.registrarconductor.models.SuperAdmin;
import com.finger.revapplution.registrarconductor.models.User;
import com.finger.revapplution.registrarconductor.models.Utils;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        Log.i(TAG, "onCreate: init");

        try {
            Log.i(TAG, "onCreate: start login");
            login();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(TAG, "onCreate: fail login");
            showErrorMessage("hay una exepcion");
        }


    }

    public static Gson getGenericGson(boolean lowerCase){
        return getGenericBuilder(lowerCase).create();
    }

    private static GsonBuilder getGenericBuilder(boolean lowerCase){
        GsonBuilder builder = Utils.gsonBuilderWithDate();
        if(lowerCase){
            builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        }
        return builder;
    }

    public static Gson getUserGson(boolean lowerCase){

        return getGenericBuilder(lowerCase)
                .registerTypeAdapterFactory(userProfileTypeFactory())
                .registerTypeAdapterFactory(personProfileTypeFactory())
                .create();
    }

    public static RuntimeTypeAdapterFactory<Profile> userProfileTypeFactory(){
        return RuntimeTypeAdapterFactory
                .of(Profile.class, "type")
                .registerSubtype(SuperAdmin.class, "SuperAdmin")
                .registerSubtype(Person.class, "Person");
    }

    public static RuntimeTypeAdapterFactory<PersonProfile> personProfileTypeFactory(){
        return RuntimeTypeAdapterFactory
                .of(PersonProfile.class, "type")
                .registerSubtype(Driver.class, "Driver");
    }



    private void login() throws UnsupportedEncodingException {

        progressBar.setVisibility(View.VISIBLE);

        LoginUser loginUser = new LoginUser("admin", "12345678");
        Log.i(TAG, "login: user created");
        RestClient.post(this, "sessions", loginUser, new MyHttpResponseHandler(this){
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressBar.setVisibility(View.GONE);
                Log.i(TAG, "onFailure: login fail");
                showErrorMessage("Fallo statusCode: " + statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                Log.i(TAG, "onSuccess: login success");
                progressBar.setVisibility(View.GONE);



                final RuntimeTypeAdapterFactory<Profile> typeFactory = RuntimeTypeAdapterFactory
                        .of(Profile.class, "type")
                        .registerSubtype(SuperAdmin.class, "SuperAdmin");

                Gson gson = getUserGson(false);

                User user = gson.fromJson(responseString, User.class);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBytes) {
                super.onSuccess(statusCode, headers, responseBytes);
                Log.i(TAG, "onSuccess: success");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBytes, Throwable throwable) {
                super.onFailure(statusCode, headers, responseBytes, throwable);
                Log.e(TAG, "onFailure: login failed");
            }
        });

    }

    private void showErrorMessage(String message){
        messageTV.setVisibility(View.VISIBLE);
        messageTV.setText(message);
    }
}
