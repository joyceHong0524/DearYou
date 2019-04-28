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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = getClass().getSimpleName();
    private final int EMAIL_LOGIN =0;
    private final int SOCIAL_LOGIN =1;


    private static final int RC_SIGN_IN =1000;
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

    CallbackManager mCallbackManager;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean flag;

    private GoogleApiClient mGoogleApiClient;

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


        button_facebook.setOnClickListener(this);
        button_login.setOnClickListener(this);
        textView_signup.setOnClickListener(this);

//        facebook = (LoginButton) findViewById(R.id.facebook);
        mCallbackManager = CallbackManager.Factory.create();

//        facebook.setReadPermissions("email","public_profile");
//        facebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                handleFacebookAccessToken(loginResult.getAccessToken());
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//
//            }
//        });

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user!=null){
                    //User is signed in
                    Log.d(TAG,"onAuthStateChanged:signed_in"+user.getUid());
                    String email = user.getEmail();
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
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG,"Something went wrong"+e);
                        }
                    });




                } else{
                    //User is signed out
                    Log.d(TAG,"onAuthStateChanged:signed_out");
                }
            }
        };
        userItem = MyApp.getApp().getUser(); //새로운 useritem을 가져온다.


        setGoogleLogin();
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
            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                    Arrays.asList("public_profile", "user_friends"));
            LoginManager.getInstance().registerCallback(mCallbackManager, //after onActivityResult 다음에 trigerred 됨
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Log.e("onSuccess", "onSuccess");
                            handleFacebookAccessToken(loginResult.getAccessToken());
                        }

                        @Override
                        public void onCancel() {
                            Log.e("onCancel", "onCancel");
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            Log.e("onError", "onError " + exception.getLocalizedMessage());
                        }
                    });


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
                            setMyAppUser(user.getEmail(),"",EMAIL_LOGIN);

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

    private void setMyAppUser(final String email, final String nickname, final int loginType){
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
                   }else if (docs.size()==0){
                       if(loginType==EMAIL_LOGIN) {
                           Log.d(TAG, "can't find user. docs size = " + docs.size());
                           return;
                       } else if(loginType== SOCIAL_LOGIN){
                           UserItem data = new UserItem("", email, nickname, null, "Set your Diary Title", new ArrayList<DiaryItem>(), new ArrayList<String>());;
                            MyApp.getApp().setUser(data);
                           db.collection("User")
                                   .add(data)
                                   .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                       @Override
                                       public void onSuccess(DocumentReference documentReference) {
                                           Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                           //여기서 userId는 user doc의 자동생성된 값을 말한다.
                                          final String userId = documentReference.getId();
                                           db.collection("User").document(userId)
                                                   .update("userId", userId)
                                                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                       @Override
                                                       public void onSuccess(Void aVoid) {
                                                           Log.d(TAG, "Document updated!");
                                                           UserItem newUser = MyApp.getApp().getUser();
                                                           newUser.setUserId(userId);
                                                           MyApp.getApp().setUser(newUser);
                                                           Log.d(TAG,"MyApp"+MyApp.getApp().getUser().getEmail());
                                                           Log.d(TAG,"Google: "+email);
                                                           Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                                           startActivity(intent);
                                                       }
                                                   })
                                                   .addOnFailureListener(new OnFailureListener() {
                                                       @Override
                                                       public void onFailure(@NonNull Exception e) {
                                                           Log.d(TAG, "something went wrong");
                                                       }
                                                   });
                                       }
                                   })
                                   .addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception e) {
                                           Log.w(TAG, "Error adding document", e);

                                       }
                                   });


                       }
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
                Toast.makeText(LoginActivity.this, "How was your day? "+MyApp.getApp().getUser().getNickname()+" :)",Toast.LENGTH_SHORT).show();
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

    private void setGoogleLogin(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();


        button_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent,RC_SIGN_IN);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }

        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        final String email = acct.getEmail();
        final String nickname= acct.getEmail();

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            setMyAppUser(email, nickname,SOCIAL_LOGIN);

                        }else{
                            Toast.makeText(LoginActivity.this, "Google login succeed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void handleFacebookAccessToken(AccessToken token){
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
               .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                   @Override
                   public void onSuccess(AuthResult authResult) {
                       FirebaseUser user = authResult.getUser();
                       String email = user.getEmail();
                       String nickname = user.getDisplayName();

                       Log.d("facebook email ",email);
                       Log.d("nickname ",nickname);

                       setMyAppUser(email, nickname,SOCIAL_LOGIN);

                       Toast.makeText(LoginActivity.this,"facebook login succeed.",Toast.LENGTH_SHORT).show();
                   }
               })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"why 2");
                        if(e instanceof FirebaseAuthUserCollisionException){
                            Toast.makeText(LoginActivity.this,"Your account was registered in another way.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



}
