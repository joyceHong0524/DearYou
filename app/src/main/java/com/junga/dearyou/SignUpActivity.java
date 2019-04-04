package com.junga.dearyou;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.UserProfileChangeRequest;

import org.w3c.dom.Text;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private String name;
    private String email;
    private String password;

    EditText input_name;
    EditText input_email;
    EditText input_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button button_google;
        Button button_facebook;
        Button button_signup;
        TextView textView_login;




        button_google = (Button) findViewById(R.id.button_google);
        button_facebook = (Button) findViewById(R.id.button_facebook);
        button_signup = (Button) findViewById(R.id.button_signup);
        textView_login = (TextView) findViewById(R.id.textView_login);

        button_google.setOnClickListener(this);
        button_facebook.setOnClickListener(this);
        button_signup.setOnClickListener(this);
        textView_login.setOnClickListener(this);


        input_name = (EditText) findViewById(R.id.input_name);
        input_email = (EditText) findViewById(R.id.input_email);
        input_password = (EditText) findViewById(R.id.input_password);






        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();




    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_signup){
            signup();
        }

    }

    private void signup(){

        name = input_name.getText().toString();
        email = input_email.getText().toString();
        password = input_password.getText().toString();


        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("d", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("d", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                    }
                };

     });

    }
}