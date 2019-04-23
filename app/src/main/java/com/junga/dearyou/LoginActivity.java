package com.junga.dearyou;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.firestore.model.Document;
import com.junga.dearyou.lib.CheckLib;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    Button button_google;
    Button button_facebook;
    Button button_login;
    TextView textView_signup;

    EditText input_email;
    EditText input_password;

    TextInputLayout emailWrapper;
    TextInputLayout passwordWrapper;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean flag;

    UserItem userItem;
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

        emailWrapper = (TextInputLayout) findViewById(R.id.email_wrapper);
        passwordWrapper = (TextInputLayout) findViewById(R.id.password_wrapper);

        button_google.setOnClickListener(this);
        button_facebook.setOnClickListener(this);
        button_login.setOnClickListener(this);
        textView_signup.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user!=null){
                    //User is signed in
                    Log.d(TAG,"onAuthStateChanged:signed_in"+user.getUid());
                    setMyAppUser(user.getEmail());
                } else{
                    //User is signed out
                    Log.d(TAG,"onAuthStateChanged:signed_out");
                }
            }
        };
        userItem = MyApp.getApp().getUser(); //새로운 useritem을 가져온다.
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
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

        if(!checkText(email,password)){
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG,"signInWithEmail : success!!");
                            user = mAuth.getCurrentUser();
                            progressDialog.dismiss(); // hide a progress dialog.
                            input_email.setText("");
                            input_password.setText("");
                            setMyAppUser(user.getEmail());

                        }
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException){
                    progressDialog.dismiss();
                    Log.d(TAG,"FirebaseCollision Exception occured : "+e.getLocalizedMessage());
                    Toast.makeText(LoginActivity.this, "Log-in failed.",Toast.LENGTH_SHORT).show();
                    passwordWrapper.setError("Password doesn't match with the account.");
                }

                if (e instanceof FirebaseAuthInvalidUserException){
                    progressDialog.dismiss();
                    Log.d(TAG,"FirebaseAuthInvalidUser Exception occured: "+e.getLocalizedMessage());
                    Toast.makeText(LoginActivity.this,"Log-in failed",Toast.LENGTH_SHORT).show();
                    emailWrapper.setError("This account doesn't exist.");
                }
            }
        });

    }

    private void setMyAppUser(String email){
        CollectionReference userCollection = db.collection("User");
        Query getUserQuery = userCollection.whereEqualTo("email",email);

        getUserQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                   QuerySnapshot querySnapshot = task.getResult();
                   List<DocumentSnapshot> docs = querySnapshot.getDocuments();
                   if(docs.size()==1) {
                       DocumentSnapshot document = docs.get(0); //anyways there should be only one document snapshot.
                       userItem = document.toObject(UserItem.class);
                      MyApp.getApp().setUser(userItem);
                      handler.sendEmptyMessage(0);
                   }else {
                       Log.d(TAG,"can't find user. docs size = "+docs.size());
                       return;
                   }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Something went wrong"+e);
            }
        });

    }

    //Handler to handle update UserInformation
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                Log.d(TAG,"Start Main Activity");
                Toast.makeText(LoginActivity.this, "Hello! "+MyApp.getApp().getUser().getNickname(),Toast.LENGTH_SHORT).show();
                startActivity(intent);

            }
        }
    };

    private boolean checkText(String email,String password){
        boolean flag = true;

        if(!((Boolean) CheckLib.getInstance().isValidEmail(email))){
            emailWrapper.setError("Invalid email");
            flag = false;
        }

        if (!((Boolean) CheckLib.getInstance().isValidPassword(password))){
            passwordWrapper.setError("Invalid password");
            flag = false;
        }

        if (TextUtils.isEmpty(email)){
            emailWrapper.setError("Shouldn't be empty");
            flag = false;
        }

        if (TextUtils.isEmpty(password)){
            passwordWrapper.setError("Shouldn't be empty");
            flag = false;
        }

        return flag;
    }
}
