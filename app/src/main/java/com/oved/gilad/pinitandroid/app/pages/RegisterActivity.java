package com.oved.gilad.pinitandroid.app.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.oved.gilad.pinitandroid.R;
import com.oved.gilad.pinitandroid.models.User;
import com.oved.gilad.pinitandroid.rest.ApiServiceBuilder;
import com.oved.gilad.pinitandroid.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText nameTxt;
    Button signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameTxt = (EditText) findViewById(R.id.nameTxt);
        signupBtn = (Button) findViewById(R.id.signupBtn);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameTxt.getText().toString().trim();
                if (name.length() < 2) {
                    //invalid input
                    Constants.Toast(RegisterActivity.this, "Please enter a valid name");
                    nameTxt.setText("");
                    nameTxt.requestFocus();
                } else {
                    //register user
                    User userToRegister = new User();
                    userToRegister.setName(name);
                    Call<User> registerCall = ApiServiceBuilder.getInstance().api().registerUser(userToRegister);
                    registerCall.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            String createdUserId = response.body().getId();
                            String createdUserName = response.body().getName();
                            String createdUserDate = response.body().getDateCreated();

                            SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString(Constants.ID_KEY, createdUserId);
                            editor.putString(Constants.NAME_KEY, createdUserName);
                            editor.putString(Constants.DATE_KEY, createdUserDate);
                            editor.commit();

                            Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(i);
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Constants.Error(t.getLocalizedMessage());

                            Toast.makeText(getApplicationContext(), "That username already exists. Please choose another one.", Toast.LENGTH_SHORT).show();

                            nameTxt.setText("");
                            nameTxt.requestFocus();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}
