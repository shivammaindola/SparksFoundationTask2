package com.Shivam.mycredibleinfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.Shivam.mycredibleinfo.LoginClasses.LoginSignupData;
import com.Shivam.mycredibleinfo.LoginClasses.ServerTest;
import com.Shivam.mycredibleinfo.LoginClasses.User;
import com.Shivam.mycredibleinfo.UserDetails.PersonalDetailsActivity;
import com.Shivam.mycredibleinfo.remote.APIUtils;
import com.Shivam.mycredibleinfo.remote.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private String email, password;
    EditText emailEditText, passwordEditText;

    public static final String MY_PREF = "MyPreference";

    private String userEmail;
    private int userId;
    UserService userService;
    private Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        emailEditText = (EditText)findViewById(R.id.email);
        passwordEditText = (EditText)findViewById(R.id.password);
        signup=(Button)findViewById(R.id.button);
        userService = APIUtils.getUserService();

        serverTest();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailEditText.getText().toString().trim();
                password = passwordEditText.getText().toString().trim();


                if(email != null && password != null)
                {
                    User user = new User(email, password);
                    signUpUser(user, email);
                }
                else
                {
                    Toast.makeText(SignUpActivity.this, "SignUp Details Empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public void signUpUser(final User user, final String email)
    {
        Call<LoginSignupData> call = userService.addUser(user);
        call.enqueue(new Callback<LoginSignupData>() {
            @Override
            public void onResponse(Call<LoginSignupData> call, Response<LoginSignupData> response) {
                userId = Integer.parseInt(response.body().getData().getId());
                userEmail = response.body().getData().getEmail();

                //Toast.makeText(SignUpActivity.this, "Thanks for joining us.\nYour unique ID is: " + userId + ".\nPlease fill the details to continue", Toast.LENGTH_LONG).show();

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREF, MODE_PRIVATE).edit();
                editor.putString("email", userEmail);
                editor.putInt("id", userId);
                editor.apply();

                Intent intent = new Intent(SignUpActivity.this, PersonalDetailsActivity.class);
                intent.putExtra("id", userId);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<LoginSignupData> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Signup Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void serverTest() {
        Call<ServerTest> call = userService.getServerStatus();
        call.enqueue(new Callback<ServerTest>() {
            @Override
            public void onResponse(Call<ServerTest> call, Response<ServerTest> response) {

                Toast.makeText(SignUpActivity.this, "ServerStatus : " + response.body().getStatus(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ServerTest> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "ServerStatus : Down : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
