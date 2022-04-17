package com.hfad.daily;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hfad.daily.Model.Users;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {
    private Button loginBtn;
    private EditText phoneInput, passwordInput;
    private ProgressDialog loadingBar;
    private String parentDBName= "Users";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBtn=(Button) findViewById(R.id.login_btn);
        phoneInput=(EditText) findViewById(R.id.login_phone_input);
       passwordInput=(EditText) findViewById(R.id.login_password_input);
        loadingBar=new ProgressDialog(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String phone= phoneInput.getText().toString();
        String password= passwordInput.getText().toString();

        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Введите номер",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Введите пароль",Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Вход в приложение");
            loadingBar.setMessage("Пожалуйста,подождите..");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateUser(phone,password);

        }
    }

    private void ValidateUser(String phone, String password) {

        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance("https://data-98267-default-rtdb.europe-west1.firebasedatabase.app"
        ).getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.child(parentDBName).child(phone).exists()){
                Users usersData=snapshot.child(parentDBName).child(phone).getValue(Users.class);
                if(usersData.getPhone().equals(phone)){
                    if(usersData.getPassword().equals(password)){
                        loadingBar.dismiss();
Toast.makeText(LoginActivity.this, "Успешный вход",Toast.LENGTH_SHORT).show();
                        Intent homeIntent= new Intent(LoginActivity.this,HomeActivity.class);
                        startActivity(homeIntent);
                    }
                    else{
                        loadingBar.dismiss();
                        Toast.makeText(LoginActivity.this, "Неверный пароль",Toast.LENGTH_SHORT).show();
                    }
                }
                }
                else{
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "Аккаунта с номером "+phone+" не существует",Toast.LENGTH_SHORT).show();
                    Intent registerIntent= new Intent(LoginActivity.this,RegisterActivity.class);
                    startActivity(registerIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}