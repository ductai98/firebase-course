package com.ductai.course.firebasestorage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText edtEmail, edtPassword;
    Button btnSignIn, btnSignUp;
    TextView txtState;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnSignUp = findViewById(R.id.btn_sign_up);
        btnSignIn = findViewById(R.id.btn_sign_in);
        txtState = findViewById(R.id.txt_state);
        auth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //login success
                    goToMainActivity();
                }else{
                    btnSignIn.setText("Sign in");
                    edtEmail.setVisibility(View.VISIBLE);
                    edtPassword.setVisibility(View.VISIBLE);
                    txtState.setVisibility(View.INVISIBLE);
                }
            }
        };

        View.OnClickListener btnClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.btn_sign_in){
                    String email = edtEmail.getText().toString();
                    String password = edtPassword.getText().toString();
                    if(user != null){
                        signOut();
                    }else{
                        if(isFormValid()){
                            signIn(email, password);
                        }else {
                            toast("Invalid email or password");
                        }
                    }
                }

                if(view.getId() == R.id.btn_sign_up){
                    String email = edtEmail.getText().toString();
                    String password = edtPassword.getText().toString();
                    if(isFormValid()){
                        signUp(email, password);
                    }else {
                        toast("Invalid email or password");
                    }
                }
            }
        };

        btnSignUp.setOnClickListener(btnClick);
        btnSignIn.setOnClickListener(btnClick);
    }

    private void signUp(String email, String password){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    toast("Sign up successful");
                }else{
                    toast(task.getException() != null ? task.getException().getMessage():"Sign up fail");
                }
            }
        });
    }

    private void signOut(){
        auth.signOut();
    }

    private void signIn(String email, String password){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    toast("Sign in successful");
                }else{
                    toast(task.getException() != null ? task.getException().getMessage():"Sign in fail");
                }
            }
        });
    }

    private void toast(String message){
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authStateListener);
    }

    private boolean isFormValid(){
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        if(email.isEmpty() || password.isEmpty()){
            return false;
        }
        return true;
    }

    private void goToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}