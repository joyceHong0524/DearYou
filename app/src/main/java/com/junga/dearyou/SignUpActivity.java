package com.junga.dearyou;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.junga.dearyou.lib.CheckLib;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String name;
    private String email;
    private String password;

    EditText input_name;
    EditText input_email;
    EditText input_password;

    TextInputLayout nameWrapper;
    TextInputLayout emailWrapper;
    TextInputLayout passwordWrapper;


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button button_signup;
        TextView textView_login;

        button_signup = (Button) findViewById(R.id.button_signup);
        textView_login = (TextView) findViewById(R.id.textView_login);

        button_signup.setOnClickListener(this);
        textView_login.setOnClickListener(this);


        input_name = (EditText) findViewById(R.id.input_name);
        input_email = (EditText) findViewById(R.id.input_email);
        input_password = (EditText) findViewById(R.id.input_password);
        nameWrapper = (TextInputLayout) findViewById(R.id.name_wrapper);
        emailWrapper = (TextInputLayout) findViewById(R.id.email_wrapper);
        passwordWrapper = (TextInputLayout) findViewById(R.id.password_wrapper);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_signup) {
            signup();
        } if(v.getId() == R.id.textView_login){
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void signup() {
        name = input_name.getText().toString();
        email = input_email.getText().toString();
        password = input_password.getText().toString();

        if(!checkText(name,email,password)){
            return;
        }



        //double check

        if(nameWrapper.isErrorEnabled() || emailWrapper.isErrorEnabled() || passwordWrapper.isErrorEnabled()){
            return;
        }


        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Making your journey...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            updateDatabase(email); //Auth와 UserDatabase는 email로 연결.
                            progressDialog.dismiss();
                            toMainActivity();

                        }
                    }

                    ;

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthWeakPasswordException) {
                            Log.d(TAG,"FirebaseAuthWeakPassword Exception occured : "+e.getLocalizedMessage());
                            Toast.makeText(SignUpActivity.this, "Sign-up failed.",Toast.LENGTH_SHORT).show();
                            passwordWrapper.setError(((FirebaseAuthWeakPasswordException) e).getReason());
                            progressDialog.dismiss();
                        }

                        if(e instanceof FirebaseAuthUserCollisionException){
                            Log.d(TAG,"FirebaseAuthUserCollision Exception occured : "+e.getLocalizedMessage());
                            Toast.makeText(SignUpActivity.this, "Sign-up failed.",Toast.LENGTH_SHORT).show();
                            emailWrapper.setError(e.getLocalizedMessage());
                            progressDialog.dismiss();
                        }
                    }
                });

    }

    public void updateDatabase(String userEmail) {
        String name = input_name.getText().toString();

        UserItem data = new UserItem("", userEmail, name, null, "Set your Diary Title", new ArrayList<DiaryItem>(), new ArrayList<String>());
        ((MyApp) getApplication()).setUser(data);

        db.collection("User")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        //여기서 userId는 user doc의 자동생성된 값을 말한다.
                        String userId = documentReference.getId();
                        updateUserId(userId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);

                    }
                });
    }

    //for social login sign up
    public void updateDatabase(String userEmail,String nickname) {
        UserItem data = new UserItem("", userEmail, nickname, null, "Set your Diary Title", new ArrayList<DiaryItem>(), new ArrayList<String>());
        MyApp.getApp().setUser(data);

        db.collection("User")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        //여기서 userId는 user doc의 자동생성된 값을 말한다.
                        String userId = documentReference.getId();
                        updateUserId(userId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);

                    }
                });
    }

    private void updateUserId(String userId) {
        db.collection("User").document(userId)
                .update("userId", userId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "something went wrong");
                    }
                });

        //이렇게 하고 나서 MyApp에 올리기.

        UserItem newUser = MyApp.getApp().getUser();
        newUser.setUserId(userId);
        MyApp.getApp().setUser(newUser); //이제 전체에서 쓸 수가 있다.
    }

    public void toMainActivity() {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private boolean checkText(String name, String email,String password){
        boolean flag = true;



        if(!((Boolean) CheckLib.getInstance().isValidEmail(email))){
            emailWrapper.setError("Invalid email");
            flag = false;
        }else{
            emailWrapper.setErrorEnabled(false);
        }

        if (!((Boolean) CheckLib.getInstance().isValidPassword(password))){
            passwordWrapper.setError("Only a-z,A-Z,&*%.! is allowed. More than 6 characters.");
            flag = false;
        } else{
            passwordWrapper.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(email)){
            emailWrapper.setError("Shouldn't be empty");
            flag = false;
        } else{
            emailWrapper.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(password)){
            passwordWrapper.setError("Shouldn't be empty");
            flag = false;
        }else{
            passwordWrapper.setErrorEnabled(false);
        }

        if(TextUtils.isEmpty(name)){
            nameWrapper.setError("Shouldn't be empty");
            flag = false;
        }else{
            nameWrapper.setErrorEnabled(false);
        }

        if(!(Boolean) CheckLib.getInstance().isValidNickname(name)){
            nameWrapper.setError("Invalid name. Only letters and numbers.");
            flag = false;
        } else{
            nameWrapper.setErrorEnabled(false);
        }

        //check if nickname has been already taken or not.
        isNicknameAlreadyTaken(name);

        return flag;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void isNicknameAlreadyTaken(final String nickname){


        Query query = db.collection("User").whereEqualTo("nickname",nickname);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                QuerySnapshot querySnapshot = task.getResult();
                    if(querySnapshot !=null) {
                        if (querySnapshot.isEmpty()) {
                            nameWrapper.setErrorEnabled(false);
                        } else {
                            nameWrapper.setError("This nickname has been already taken.");
                        }

                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Failed to check a existence of the nickname.");
            }
        });

    }

}