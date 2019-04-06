package com.junga.dearyou;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    Button button_google;
    Button button_facebook;
    Button button_login;
    TextView textView_signup;

    EditText input_email;
    EditText input_password;

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        button_google = (Button) findViewById(R.id.button_google);
        button_facebook = (Button) findViewById(R.id.button_facebook);
        button_login = (Button) findViewById(R.id.button_login);
        textView_signup = (TextView) findViewById(R.id.textView_signup);

        input_email = (EditText) findViewById(R.id.input_email);
        input_password = (EditText) findViewById(R.id.input_password);

        button_google.setOnClickListener(this);
        button_facebook.setOnClickListener(this);
        button_login.setOnClickListener(this);
        textView_signup.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_google){

        } else if(v.getId() == R.id.button_facebook){

        } else if(v.getId() == R.id.button_login){
            login();
        } else if(v.getId() == R.id.textView_signup){
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
    }

    private void login(){
        final String email = input_email.getText().toString();
        final String password = input_password.getText().toString();

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");

        progressDialog.show();


        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d("","signInWithEmail : success!!");
                            user = mAuth.getCurrentUser();
                            progressDialog.dismiss(); // hide a progress dialog.
                            Toast.makeText(LoginActivity.this, "Hello! "+user.getEmail(),Toast.LENGTH_SHORT).show();
//                            ((MyApp) getApplication()).setBothEmailAndPassword(email,password);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            //updateUI(user);
                        } else{
                            Log.w("","signInWithEmail : failure!", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            //updateUI(user);
                        }
                    }
                });

    }







}
