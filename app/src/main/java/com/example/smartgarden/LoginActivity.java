package com.example.smartgarden;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartgarden.client.Client;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences tokenPref = getSharedPreferences("GARDEN_PREF", MODE_PRIVATE);
        String token = tokenPref.getString("token", null);

        final Context ctx = this;

        if(token != null) {
            // go to main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            etEmail = findViewById(R.id.login_input_email);
            etPassword = findViewById(R.id.login_input_password);
            btnLogin = findViewById(R.id.login_button);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Client client = new Client();
                    String email = etEmail.getText().toString();
                    String password = etPassword.getText().toString();

                    @SuppressLint("StaticFieldLeak")
                    AsyncTask asyncTask = new AsyncTask<String, Integer, String>() {
                        @Override
                        protected String doInBackground(String... strings) {
                            String token = null;
                            try {
                                token = client.login(email, password);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return token;
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            // go to main activity
                            Toast.makeText(ctx, "login ok ", 3000).show();
                            Intent intent = new Intent(ctx, MainActivity.class);
                            intent.putExtra("token", s);
                            startActivity(intent);
                        }
                    };
                    String[] strings = {"qwe", "asdqwe"};
                    asyncTask.execute(strings);
                }
            });
        }
    }
}
