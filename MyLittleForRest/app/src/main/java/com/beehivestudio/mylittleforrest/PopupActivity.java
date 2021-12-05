package com.beehivestudio.mylittleforrest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class PopupActivity extends Activity {

    private String menu;
    private String contentUid;
    private String selectOption;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        TextView head = findViewById(R.id.head);
        TextView txtText = findViewById(R.id.txtText);
        menu = getIntent().getStringExtra("menu");
        selectOption = getIntent().getStringExtra("selectOption");

        contentUid = getIntent().getStringExtra("contentUid");
        if(menu.equals("delete")){
            head.setText("삭제");
            txtText.setText("정말 지우시겠습니까?");
        }

    }

    public void yes(View v){
        if (menu.equals("delete")){
            Intent intent =new Intent(this, ContentDetailActivity.class);
            db.collection(selectOption).document(contentUid).delete();
            setResult(RESULT_OK, intent);
            finish();
        }else {
            Intent intent = new Intent(this, SeedActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void no(View v){
        if (menu.equals("delete")){
            finish();
        }else {
            moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        return;
    }
}