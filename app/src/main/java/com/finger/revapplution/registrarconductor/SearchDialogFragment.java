package com.finger.revapplution.registrarconductor;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.finger.revapplution.registrarconductor.models.Driver;
import com.finger.revapplution.registrarconductor.models.Person;
import com.finger.revapplution.registrarconductor.models.PersonProfile;
import com.finger.revapplution.registrarconductor.models.Profile;
import com.finger.revapplution.registrarconductor.models.RuntimeTypeAdapterFactory;
import com.finger.revapplution.registrarconductor.models.SuperAdmin;
import com.finger.revapplution.registrarconductor.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;


/**
 * Created by Revapplution on 15/03/2018.
 */

public class SearchDialogFragment extends DialogFragment implements View.OnClickListener{

    Button closeBtn, searchBtn;
    ProgressBar progressBar;
    EditText searchET;
    SearchListener listener;
    User user;

    static SearchDialogFragment newInstance(SearchListener listener, User user){
        SearchDialogFragment searchableDialog = new SearchDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        searchableDialog.setArguments(bundle);
        searchableDialog.setListener(listener);
        searchableDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return searchableDialog;
    }

    public void setListener(SearchListener listener){
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = (User) getArguments().getSerializable("user");

    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//
//        Dialog dialog =  super.onCreateDialog(savedInstanceState);
//        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        return dialog;
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.search_dialog, container, false);

        closeBtn = view.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(this);
        searchBtn = view.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(this);
        progressBar = view.findViewById(R.id.progressBar);
        searchET = view.findViewById(R.id.searchET);

        return view;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.closeBtn:
                SearchDialogFragment.this.dismiss();
                break;
            case R.id.searchBtn:
                getPerson();
                break;
        }
    }

    public interface SearchListener{

        public void success(Person person);

    }

    private void getPerson(){

        progressBar.setVisibility(View.VISIBLE);
        String identification = searchET.getText().toString();

        RequestParams params = new RequestParams();
        params.add("identification", identification);

        RestClient.addHeader("token", user.token);
        RestClient.get("people/get_person", params, new MyHttpResponseHandler(getActivity()){
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                final RuntimeTypeAdapterFactory<PersonProfile> typeFactory = RuntimeTypeAdapterFactory
                        .of(PersonProfile.class, "type")
                        .registerSubtype(Driver.class, "Driver");

                Gson gson = new GsonBuilder().registerTypeAdapterFactory(typeFactory).create();
                Person person = gson.fromJson(responseString, Person.class);
                Log.i(TAG, "onSuccess: " + person);
                listener.success(person);
                progressBar.setVisibility(View.INVISIBLE);
                SearchDialogFragment.this.dismiss();
            }
        });

    }


}
