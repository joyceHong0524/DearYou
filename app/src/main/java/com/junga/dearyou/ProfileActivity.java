package com.junga.dearyou;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;
import com.junga.dearyou.lib.CheckLib;
import com.junga.dearyou.lib.FabLib;
import com.junga.dearyou.lib.FontLib;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    EditText editText_nickname;
    EditText editText_diaryName;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    FabLib fab;
    FontLib fontLib = new FontLib();
    @Override
    protected void onResume() {
        super.onResume();
        fab.closeFABMenu();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editText_nickname = (EditText) findViewById(R.id.editText_nickname);
        editText_diaryName = (EditText) findViewById(R.id.editText_diaryName);
        TextView textView_save = (TextView) findViewById(R.id.textView_save);

        editText_diaryName.setText(MyApp.getApp().getUser().getDiaryName());
        editText_nickname.setText(MyApp.getApp().getUser().getNickname());

        TextView signOut = (TextView) findViewById(R.id.textView_signout);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    auth.signOut();
                LoginManager.getInstance().logOut();
                    Toast.makeText(ProfileActivity.this,"Bye..!",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileActivity.this,LoginActivity.class);
                    startActivity(intent);
            }
        });

        textView_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });

        fab = new FabLib(ProfileActivity.this);
        fab.setFabMenu();

        fontLib.setFont(this,"oleo_script",editText_nickname);
        fontLib.setFont(this,"oleo_script",editText_diaryName);
        fontLib.setFont(this,"oleo_script_bold",signOut);
    }

    private void saveProfile(){

        final String diaryName = editText_diaryName.getText().toString();
        final String nickname = editText_nickname.getText().toString();


        Query query = db.collection("User").whereEqualTo("nickname",nickname);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    if(querySnapshot !=null) {
                        if (!querySnapshot.isEmpty()) {
                            if(MyApp.getApp().getUser().getNickname().equals(nickname)){
                                handler.sendEmptyMessage(0);
                            }else{
                            Toast.makeText(ProfileActivity.this,"Username has been already taken.",Toast.LENGTH_SHORT).show();}

                        } else{
                            if(textCheck(diaryName,nickname)) handler.sendEmptyMessage(0);
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



    private void myAppUserUpdate(String diaryName, String nickname){
        MyApp.getApp().getUser().setDiaryName(diaryName);
        MyApp.getApp().getUser().setNickname(nickname);
        Toast.makeText(this,"Saved :D",Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void updateUser(final String diaryName,final String nickName){
        DocumentReference document = db.collection("User").document(MyApp.getApp().getUser().getUserId());

        Map<String,Object> updates = new HashMap<>();
        updates.put("diaryName",diaryName);
        updates.put("nickname",nickName);
        document.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG,"successfuly updated");
                myAppUserUpdate(diaryName,nickName);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"There was a problem processing update.");
            }
        });
    }

    private boolean textCheck(String diaryName,String nickname) {
        String pastDiaryName = MyApp.getApp().getUser().getDiaryName();
        String pastNickname = MyApp.getApp().getUser().getNickname();
        if (diaryName.equals("") || nickname.equals("")) {
            Toast.makeText(this, "Please fill everything.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (diaryName.equals(pastDiaryName) && nickname.equals(pastNickname)) {
            Toast.makeText(this, "Nothing has been changed.", Toast.LENGTH_SHORT).show();
            return false;
        } else if(!CheckLib.getInstance().isValidNickname(nickname)){
            Toast.makeText(this, "Only english, korean, number can be used.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0){
                updateUser(editText_diaryName.getText().toString(),editText_nickname.getText().toString());
            }
        }
    };

}
