package com.example.bookshelf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookshelf.R;
import com.example.bookshelf.service.AppService;
import com.example.bookshelf.model.SessionResult;

import org.springframework.web.client.RestTemplate;

public class MainActivity extends AppCompatActivity {

    private RestTemplate restTemplate;
    private Bundle bundle;

    private EditText et_username;
    private EditText et_password;
    private Button btn_signup;
    private Button btn_login;

    private AppService appService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        appService = new AppService(getApplicationContext());

        init();
        setFilters();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    private void setFilters() {
        appService.addFilter(et_username, true);
        appService.addFilter(et_password, true);

    }

    private Boolean validate() {
        if (et_username.getText().toString().trim().equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.username_cannot_be_empty), Toast.LENGTH_LONG).show();
            return false;
        }

        if (et_password.getText().toString().trim().equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.password_cannot_be_empty), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void login() {

        if (validate()) {

            SessionResult sessionResult = appService.login(et_username.getText().toString(), et_password.getText().toString());
            if (sessionResult.getErrorCode() == 1) {
                if (sessionResult.getUsage().equals("1")) {

                    Intent intent = new Intent(this, UserSessionActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(getString(R.string.userSession), et_username.getText().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);

                } else if (sessionResult.getUsage().equals("2")) {

                    Intent intent = new Intent(this, AdminSessionActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(getString(R.string.userSession), et_username.getText().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);

                } else if (sessionResult.getUsage().equals("3")) {

                    Intent intent = new Intent(this, AppMasterSessionActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(getString(R.string.userSession), et_username.getText().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            } else
                Toast.makeText(getApplicationContext(), sessionResult.getErrorDesc(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        et_username.setText("");
        et_password.setText("");
    }

    private void signup() {

        Intent intent = new Intent(this, SignUpActivity.class);

        bundle = new Bundle();
        bundle.putInt(getString(R.string.signSession), 0);
        bundle.putString(getString(R.string.username), et_username.getText().toString());
        bundle.putString(getString(R.string.password), et_password.getText().toString());

        intent.putExtras(bundle);
        startActivity(intent);

    }

    private void init() {
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_signup = (Button) findViewById(R.id.btn_signup);
    }
}
