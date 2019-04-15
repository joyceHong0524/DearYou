package com.junga.dearyou;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String name;
    private String email;
    private String password;

    EditText input_name;
    EditText input_email;
    EditText input_password;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                            updateDatabase(email); //Auth와 UserDatabase는 email로 연결.
                            toMainActivity();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("d", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();


                    }
                };

     });

    }

    public void updateDatabase(String userEmail){
        String name = input_name.getText().toString();
        UserItem data = new UserItem("",userEmail,name,null,"Set your Diary Title",new ArrayList<DiaryItem>(),new ArrayList<FriendItem>());
        ((MyApp) getApplication()).setUser(data);

        db.collection("User")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("hi", "DocumentSnapshot written with ID: " + documentReference.getId());
                        //여기서 userId는 user doc의 자동생성된 값을 말한다.
                        String userId = documentReference.getId();
                        updateUserId(userId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("hi", "Error adding document", e);

                    }
                });
    }

    private void updateUserId(String userId) {
        db.collection("User").document(userId)
                .update("userId",userId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("he","Document updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("her","something went wrong");
                    }
                });

        //이렇게 하고 나서 MyApp에 올리기.

       UserItem newUser = MyApp.getApp().getUser();
       newUser.setUserId(userId);
       MyApp.getApp().setUser(newUser); //이제 전체에서 쓸 수가 있다.
    }

    private void toMainActivity() {
        Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
        startActivity(intent);
    }
}