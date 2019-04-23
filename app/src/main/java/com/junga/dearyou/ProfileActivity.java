package com.junga.dearyou;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.model.Document;
import com.junga.dearyou.lib.FabLib;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    EditText editText_nickname;
    EditText editText_diaryName;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

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

        FabLib fab = new FabLib(ProfileActivity.this);
        fab.setFabMenu();
    }

    private void saveProfile(){

        final String diaryName = editText_diaryName.getText().toString();
        final String nickname = editText_nickname.getText().toString();
        String pastDiaryName = MyApp.getApp().getUser().getDiaryName();
        String pastNickname = MyApp.getApp().getUser().getNickname();

        if (diaryName.equals("")||nickname.equals("")){
            Toast.makeText(this,"Please fill everything.",Toast.LENGTH_SHORT).show();
        }else if(diaryName.equals(pastDiaryName)&&nickname.equals(pastNickname)){
            Toast.makeText(this,"Nothing has been changed.",Toast.LENGTH_SHORT).show();
        }
        else{
            //userupdate, myuserupdate.

            DocumentReference document = db.collection("User").document(MyApp.getApp().getUser().getUserId());

            Map<String,Object> updates = new HashMap<>();
            updates.put("diaryName",diaryName);
            updates.put("nickname",nickname);
            document.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG,"successfuly updated");
                    myAppUserUpdate(diaryName,nickname);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG,"There was a problem processing update.");
                }
            });

        }

    }

    private void myAppUserUpdate(String diaryName, String nickname){
        MyApp.getApp().getUser().setDiaryName(diaryName);
        MyApp.getApp().getUser().setNickname(nickname);
        Toast.makeText(this,"Saved :D",Toast.LENGTH_SHORT).show();
        finish();
    }
}
