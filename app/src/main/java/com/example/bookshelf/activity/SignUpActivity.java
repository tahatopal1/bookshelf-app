package com.example.bookshelf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookshelf.R;
import com.example.bookshelf.service.AppService;
import com.example.bookshelf.model.SessionResult;
import com.example.bookshelf.model.AppUser;
import com.google.android.material.textfield.TextInputLayout;

public class SignUpActivity extends AppCompatActivity {

    private EditText et_name;
    private EditText et_surname;
    private EditText et_username;
    private EditText et_password;
    private EditText et_manager;
    private Button et_button;
    private Spinner spn_auth;
    private TextInputLayout til_manager;

    private Bundle bundle;
    private AppService appService;
    private AppUser appUser;

    private int signSession;
    private String userSession;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        appService = new AppService(getApplicationContext());
        init(); // Initiates components
        arrangeComponents(); // Some arrangements on components

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (signSession == 0) {

            spn_auth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        et_manager.setVisibility(View.VISIBLE);
                        til_manager.setVisibility(View.VISIBLE);
                    } else {
                        et_manager.setVisibility(View.GONE);
                        til_manager.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            et_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        signup(v);
                    } catch (Exception e) {
                        Log.d("TAG", "onClick: " + e.toString());
                        Toast.makeText(getApplicationContext(), "System Error!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else if (signSession == 1) {
            userSession = bundle.getString("userSession");
            appUser = appService.getCurrentUser(userSession);

            et_name.setText(appUser.getName());
            et_surname.setText(appUser.getSurname());
            et_username.setText(appUser.getUsername());
            et_password.setText(appUser.getPassword());


            switch (appUser.getAuthCode()){
                case 1:
                    spn_auth.setVisibility(View.VISIBLE);
                    spn_auth.setEnabled(false);
                    til_manager.setVisibility(View.VISIBLE);
                    et_manager.setEnabled(false);
                    et_manager.setText(appUser.getManager());

                case 2:
                    spn_auth.setVisibility(View.VISIBLE);
                    spn_auth.setEnabled(false);
                    til_manager.setVisibility(View.GONE);

            }

            et_button.setText("Update");

            et_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        update();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.system_error), Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

    }

    private void update() {

        appUser.setName(et_name.getText().toString());
        appUser.setSurname(et_surname.getText().toString());
        appUser.setUsername(et_username.getText().toString());
        appUser.setPassword(et_password.getText().toString());

        SessionResult sessionResult = appService.update(appUser);
        Toast.makeText(getApplicationContext(), sessionResult.getErrorDesc(), Toast.LENGTH_LONG).show();

        if (sessionResult.getErrorCode() == 1)
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));

    }

    private void arrangeComponents() {

        // Prevents typing number on field
        appService.addFilter(et_name, false);
        appService.addFilter(et_surname, false);

        // Prevents typing space on field
        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = et_password.getText().toString();
                if (str.length() > 0 && str.contains(" ")) {
                    et_password.setText(et_password.getText().toString().replaceAll(" ", ""));
                    et_password.setSelection(et_password.getText().length());
                }
            }
        });


    }


    private void signup(View v) {


        AppUser appUser = new AppUser();
        appUser.setName(et_name.getText().toString());
        appUser.setSurname(et_surname.getText().toString());
        appUser.setUsername(et_username.getText().toString());
        appUser.setPassword(et_password.getText().toString());
        appUser.setAuthCode(spn_auth.getSelectedItemPosition() + 1);

        if (spn_auth.getSelectedItemPosition() == 0)
            appUser.setManager(et_manager.getText().toString());

        if (validate()) {
            SessionResult sessionResult = appService.signup(appUser);
            if (sessionResult.getErrorCode() == 1) {
                onBackPressed();
                Toast.makeText(getApplicationContext(), sessionResult.getErrorDesc(), Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(getApplicationContext(), sessionResult.getErrorDesc(), Toast.LENGTH_LONG).show();
        }


    }

    private void init() {

        // Initiating components
        et_name = (EditText) findViewById(R.id.et_name);
        et_surname = (EditText) findViewById(R.id.et_surname);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        et_manager = (EditText) findViewById(R.id.et_manager);
        et_button = (Button) findViewById(R.id.button);
        spn_auth = (Spinner) findViewById(R.id.spn_auth);
        til_manager = (TextInputLayout) findViewById(R.id.et_manager_layout);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.roles, android.R.layout.simple_spinner_dropdown_item);
        spn_auth.setAdapter(adapter);

        bundle = getIntent().getExtras();
        signSession = bundle.getInt(getString(R.string.signSession));

        if (bundle.getString(getString(R.string.username)) != null) et_username.setText(bundle.getString(getString(R.string.username)));
        if (bundle.getString(getString(R.string.password)) != null) et_password.setText(bundle.getString(getString(R.string.password)));

    }

    private Boolean validate() {

        if (et_name.getText().toString().trim().equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.name_cannot_be_empty), Toast.LENGTH_LONG).show();
            return false;
        }

        if (et_surname.getText().toString().trim().equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.surname_cannot_be_empty), Toast.LENGTH_LONG).show();
            return false;
        }

        if (et_username.getText().toString().trim().equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.username_cannot_be_empty), Toast.LENGTH_LONG).show();
            return false;
        }

        if (et_password.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.password_cannot_be_empty), Toast.LENGTH_LONG).show();
            return false;
        }

        if (spn_auth.getSelectedItemPosition() == 0) {

            if (et_manager.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), getString(R.string.manager_cannot_be_empty), Toast.LENGTH_LONG).show();
                return false;
            }

            if (!appService.validateManager(et_manager.getText().toString())) {
                Toast.makeText(getApplicationContext(), getString(R.string.no_such_manager), Toast.LENGTH_LONG).show();
                return false;
            }

        }

        return true;
    }


}
