package com.beehivestudio.mylittleforrest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SeedActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    String uid = user.getUid();
    String seed = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seed);

        EditText et_seed_name = findViewById(R.id.et_seed_name);

        ImageButton rose = findViewById(R.id.rose);
        ImageButton sunflower = findViewById(R.id.sunflower);
        ImageButton rosemoss = findViewById(R.id.rosemoss);
        ImageButton forsythia = findViewById(R.id.forsythia);
        ImageButton tulip = findViewById(R.id.tulip);
        ImageButton cosmos = findViewById(R.id.cosmos);
        ImageButton valley = findViewById(R.id.valley);
        ImageButton mugunghwa = findViewById(R.id.mugunghwa);
        ImageButton morninggory = findViewById(R.id.morninggory);

        ImageButton.OnClickListener onClickListener = new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_seed_name.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(SeedActivity.this, "공백이 있습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    switch (view.getId()) {
                        case R.id.rose:
                            seed = "rose";
                            seed_sand(seed, et_seed_name.getText().toString(), 1);
                            break;
                        case R.id.sunflower:
                            seed = "sunflower";
                            seed_sand(seed, et_seed_name.getText().toString(), 2);
                            break;
                        case R.id.rosemoss:
                            seed = "rosemoss";
                            seed_sand(seed, et_seed_name.getText().toString(), 3);
                            break;
                        default:
                            Toast.makeText(SeedActivity.this, "업데이트를 기다려 주세요!", Toast.LENGTH_SHORT).show();
                            break;

//                        case R.id.forsythia:
//                            seed = "forsythia";
//                            seed_sand(seed, et_seed_name.getText().toString(), 4);
//                            break;
//                        case R.id.tulip:
//                            seed = "tulip";
//                            seed_sand(seed, et_seed_name.getText().toString(), 5);
//                            break;
//                        case R.id.cosmos:
//                            seed = "cosmos";
//                            seed_sand(seed, et_seed_name.getText().toString(), 6);
//                            break;
//                        case R.id.valley:
//                            seed = "valley";
//                            seed_sand(seed, et_seed_name.getText().toString(), 7);
//                            break;
//                        case R.id.mugunghwa:
//                            seed = "mugunghwa";
//                            seed_sand(seed, et_seed_name.getText().toString(), 8);
//                            break;
//                        case R.id.morninggory:
//                            seed = "morninggory";
//                            seed_sand(seed, et_seed_name.getText().toString(), 9);
//                            break;
                    }
                }
            }
        };
        rose.setOnClickListener(onClickListener);
        rosemoss.setOnClickListener(onClickListener);
        sunflower.setOnClickListener(onClickListener);
        morninggory.setOnClickListener(onClickListener);
        mugunghwa.setOnClickListener(onClickListener);
        forsythia.setOnClickListener(onClickListener);
        valley.setOnClickListener(onClickListener);
        tulip.setOnClickListener(onClickListener);
        cosmos.setOnClickListener(onClickListener);

    }

    private void seed_sand(String species, String name, int num) {
        String pattern = "yyyyMMdd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());

        Map<String, Object> user = new HashMap<>();
        user.put("date", date);
        user.put("species", species);
        user.put("name", name);
        user.put("exp", "0");

        db.collection("Seed").document(uid).set(user);


        Map<String, Object> seed_book = new HashMap<>();
        db.collection("Book").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String temp;
                    //i로 갯수 수정
                    for (int i = 1; i < 4; i++) {
                        temp = "seed" + i;
                        if (i == num) {
                            seed_book.put(temp, true);
                        } else {
                            if (document.getBoolean(temp) == null) {
                                seed_book.put(temp, false);
                            } else {
                                seed_book.put(temp, document.getBoolean(temp));
                            }
                        }
                    }
                }
                db.collection("Book").document(uid).set(seed_book);
            }
        });


        Intent intent = new Intent(SeedActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        SeedActivity.this.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}