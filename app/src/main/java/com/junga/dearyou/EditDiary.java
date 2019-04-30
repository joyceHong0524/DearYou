package com.junga.dearyou;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.junga.dearyou.lib.FabLib;
import com.junga.dearyou.lib.FontLib;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class EditDiary extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();


    private final int NO_POSITION = -1;

    private String title;
    private String content;
    private boolean isLocked;
    private int position; //diary의 위치를 알려줌.

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String diaryId; //For method delete diary , 2.Diarydata delete

    private FabLib fab;
    private final FontLib fontLib = new FontLib();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_diary);

        TextView textView_title = findViewById(R.id.textView_title);
        TextView textView_content = findViewById(R.id.textView_content);
        TextView textView_edit = findViewById(R.id.textView_edit);
        TextView textView_delete = findViewById(R.id.textView_delete);
        ImageView imageView_locker = findViewById(R.id.icon_locker);


        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        isLocked = getIntent().getBooleanExtra("isLocked",false);
        position = getIntent().getIntExtra("position", NO_POSITION);
        String setTitle= "< "+title+" >";
        textView_title.setText(setTitle);
        textView_content.setText(content);

        textView_edit.setOnClickListener(this);
        textView_delete.setOnClickListener(this);



        fontLib.setFont(this,"oleo_script_bold", textView_title);
        fontLib.setFont(this,"inconsolata", textView_content);
        if(isLocked){
            Glide.with(this).load(R.drawable.icon_lock).into(imageView_locker);
        } else{
            Glide.with(this).load(R.drawable.icon_open).into(imageView_locker);
        }

        fab = new FabLib(EditDiary.this);
        fab.setFabMenu();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.textView_edit) {
            if (position != NO_POSITION) {
                Intent intent = new Intent(EditDiary.this, WritingActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("content", content);
                intent.putExtra("position", position);
                intent.putExtra("isLocked",isLocked);
                int UPDATE = 1;
                intent.putExtra("mode", UPDATE); //update mode라는 것!
                startActivity(intent);
                finish();
            }
        }else if (v.getId() == R.id.textView_delete) {
            if (position != NO_POSITION) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("This diary will be deleted for good.");
                builder.setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                              deleteDiary();
                            }
                        });
                builder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.closeFABMenu();
    }

    private void deleteDiary() {


        //1.Userdata delete
        //유저를 찾는다. diaries 필드를 받아와서 업데이트한다.
        //Diarydata를 위해서 diaries 필드중 id를 받아와서 저장한다.

        db.collection("User").document(MyApp.getApp().getUser().getUserId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                UserItem user = document.toObject(UserItem.class);
                ArrayList<DiaryItem> diaries = user.getDiaries();
                diaryId = diaries.get(position).getDiaryId();
                diaries.remove(position);
                //update editted diary arraylist.
                db.collection("User").document(MyApp.getApp().getUser().getUserId())
                        .update("diaries", diaries)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                hanlder.sendEmptyMessage(1);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating documents.");

                    }
                });
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private final
    Handler hanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {

                //2.Diarydata  delete
                //diaryId가 필요하다.
                //찾아서 document를 삭제한다.
                db.collection("Diary").document(diaryId)
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Diary successfully deleted!");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to delete");
                    }
                });

                //3.MyApp data delete
                //diaries필드를 찾아서 업데이트 한다.
                //어떻게 찾아줄것인가?

                ArrayList<DiaryItem> diaries = MyApp.getApp().getUser().getDiaries();
                diaries.remove(position);
                MyApp.getApp().getUser().setDiaries(diaries);

                finish();
            }
        }
    };

    @Override
    public void onBackPressed() {
        finish();
    }
}


