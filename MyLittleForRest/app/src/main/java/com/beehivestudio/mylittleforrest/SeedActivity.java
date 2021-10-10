package com.beehivestudio.mylittleforrest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.beehivestudio.mylittleforrest.model.SeedDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.FirestoreClient;
import com.google.firebase.storage.FirebaseStorage;

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

        ImageButton seed1 = findViewById(R.id.seed1);
        ImageButton seed2 = findViewById(R.id.seed2);
        ImageButton seed3 = findViewById(R.id.seed3);
        ImageButton seed4 = findViewById(R.id.seed4);
        ImageButton seed5 = findViewById(R.id.seed5);
        ImageButton seed6 = findViewById(R.id.seed6);

        ImageButton.OnClickListener onClickListener = new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_seed_name.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(SeedActivity.this, "공백이 있습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    switch (view.getId()) {
                        case R.id.seed1:
                            seed = "장미";
                            seed_sand(seed, et_seed_name.getText().toString());
                            break;
                        case R.id.seed2:
                            seed = "장미";
                            seed_sand(seed, et_seed_name.getText().toString());
                            break;
                        case R.id.seed3:
                            seed = "장미";
                            seed_sand(seed, et_seed_name.getText().toString());
                            break;
                        case R.id.seed4:
                            seed = "장미";
                            seed_sand(seed, et_seed_name.getText().toString());
                            break;
                        case R.id.seed5:
                            seed = "장미";
                            seed_sand(seed, et_seed_name.getText().toString());
                            break;
                        case R.id.seed6:
                            seed = "장미";
                            seed_sand(seed, et_seed_name.getText().toString());
                            break;
                    }
                }
            }
        };
        seed1.setOnClickListener(onClickListener);
        seed2.setOnClickListener(onClickListener);
        seed3.setOnClickListener(onClickListener);
        seed4.setOnClickListener(onClickListener);
        seed5.setOnClickListener(onClickListener);
        seed6.setOnClickListener(onClickListener);


    }

    private void seed_sand(String species, String name) {
        Map<String, Object> user = new HashMap<>();
        user.put("species", species);
        user.put("name", name);
        user.put("exp", "0");

        db.collection("Seed").document(uid).set(user);

        Intent intent = new Intent(SeedActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        SeedActivity.this.startActivity(intent);
    }
}