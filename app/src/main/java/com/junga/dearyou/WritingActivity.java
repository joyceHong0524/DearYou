package com.junga.dearyou;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class WritingActivity extends AppCompatActivity implements View.OnClickListener{

    EditText title;
    EditText description;
    TextView save;
    TextView cancel;
    TextView titleAbove;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        title = (EditText) findViewById(R.id.editText_title);
        description = (EditText) findViewById(R.id.editText_description);
        save = (TextView) findViewById(R.id.TextView_save);
        cancel = (TextView)findViewById(R.id.cancel);
        titleAbove = (TextView) findViewById(R.id.titleAbove);

        save.setOnClickListener(this);
        cancel.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.TextView_save){
            saveDiary();
        }else if(v.getId()==R.id.cancel){

        }

    }

    private void saveDiary(){

       String editTitle = title.getText().toString();
       String editDescription = description.getText().toString();



//        Map<String,Object> data = new HashMap<>();
//        data.put("title",editTitle);
//        data.put("description",editDescription);
        String email = auth.getCurrentUser().getEmail();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        Long a = time.getTime();

        DiaryItem data = new DiaryItem(email,time,editDescription,false);
        db.collection("Diary").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("hi", "DocumentSnapshot written with ID: " + documentReference.getId());
                Toast.makeText(WritingActivity.this, "Saved :D",Toast.LENGTH_SHORT).show();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("error", e.toString());
            }
        });

    }
}
