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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.sql .Date;
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
       String authorId = ((MyApp) getApplication()).getUser().getUserId();

       if(auth.getCurrentUser() != null){

//           Timestamp time = new Timestamp(System.currentTimeMillis());
//           Date time1 = new Date(time);
           final DiaryItem data = new DiaryItem(authorId,editTitle,editDescription,false);
           db.collection("Diary").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
               @Override
               public void onSuccess(DocumentReference documentReference) {
                   Log.d("hi", "DocumentSnapshot written with ID: " + documentReference.getId());
                   userDiaryUpdate(data); // pass the new diary.
                   Toast.makeText(WritingActivity.this, "Saved :D",Toast.LENGTH_SHORT).show();


               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   Log.d("error", e.toString());
               }
           });
       }else{
           return;
       }


//        Map<String,Object> data = new HashMap<>();
//        data.put("title",editTitle);
//        data.put("description",editDescription);



    }

    private void userDiaryUpdate(final DiaryItem diaryItem){

       final String userId = ((MyApp) getApplication()).getUser().userId;

        //1. userid(authorId)를 가지고 데이터 doc에 접근.
        DocumentReference userRef = db.collection("User").document(userId); //might be null. so need to do something with this.

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                 DocumentSnapshot document = task.getResult();
//                 Log.d("document log",document.toString());
                 if(document.exists()){
                     UserItem user = document.toObject(UserItem.class);
                     ArrayList<DiaryItem> diaryList = user.getDiaries();
                     diaryList.add(diaryItem);
                     DocumentReference userRef = db.collection("User").document(userId); //might be null. so need to do something with this.

                     userRef.update("diaries",diaryList)
                             .addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {
                                     Log.d("", "DocumentSnapshot successfully updated!");
                                 }
                             })
                             .addOnFailureListener(new OnFailureListener() {
                                 @Override
                                 public void onFailure(@NonNull Exception e) {
                                     Log.w("w","Error updating documents.");
                                 }
                             });

                 }
                }
            }
        });



        //2. diary arrayList를 수정.
        //3. database에 업데이트.

    }

}
