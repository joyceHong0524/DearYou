package com.junga.dearyou;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.firestore.model.Document;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
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

        button_google.setOnClickListener(this);
        button_facebook.setOnClickListener(this);
        button_login.setOnClickListener(this);
        textView_signup.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        userItem = ((MyApp) getApplication()).getUser(); //새로운 useritem을 가져온다.
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
////        updateUI(currentUser);
//    }


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
                            setMyAppUser(user.getEmail());

                            Log.d("flag",String.valueOf(flag));
                        } else{
                            Log.w("","signInWithEmail : failure!", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            //updateUI(user);
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
                Log.d("MyApp","in1");
                if (task.isSuccessful()){
                    Log.d("MyApp","in2");
                   QuerySnapshot querySnapshot = task.getResult();
                   List<DocumentSnapshot> docs = querySnapshot.getDocuments();
                   if(docs.size()==1) {
                       Log.d("MyApp","in3");
                       DocumentSnapshot document = docs.get(0); //anyways there should be only one document snapshot.
                       userItem = document.toObject(UserItem.class);
                      MyApp.getApp().setUser(userItem);
                      handler.sendEmptyMessage(0);
                   }else {
                       Log.d("MyApp","in4");
                       Log.d("hi","can't find user. docs size = "+docs.size());
                       return;
                   }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Tag","Something went wrong"+e);
            }
        });

    }

    //Handler to handle update UserInformation

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                Log.d("","Start Main Activity");
                startActivity(intent);

            }
        }
    };



}
