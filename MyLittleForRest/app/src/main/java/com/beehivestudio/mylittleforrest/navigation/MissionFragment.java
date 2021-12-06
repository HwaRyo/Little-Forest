package com.beehivestudio.mylittleforrest.navigation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beehivestudio.mylittleforrest.MainActivity;
import com.beehivestudio.mylittleforrest.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class MissionFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();

    String pattern = "yyyyMMdd";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String date = simpleDateFormat.format(new Date());
    int current_date = Integer.parseInt(date);
    int mission[] = new int[6];


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mission, container, false);

        ImageView mission_11_image = root.findViewById(R.id.mission_11_image);
        ImageView mission_12_image = root.findViewById(R.id.mission_12_image);
        ImageView mission_21_image = root.findViewById(R.id.mission_21_image);
        ImageView mission_22_image = root.findViewById(R.id.mission_22_image);
        ImageView mission_31_image = root.findViewById(R.id.mission_31_image);
        ImageView mission_32_image = root.findViewById(R.id.mission_32_image);

        CheckBox mission_11_text = root.findViewById(R.id.mission_11_text);
        CheckBox mission_12_text = root.findViewById(R.id.mission_12_text);
        CheckBox mission_21_text = root.findViewById(R.id.mission_21_text);
        CheckBox mission_22_text = root.findViewById(R.id.mission_22_text);
        CheckBox mission_31_text = root.findViewById(R.id.mission_31_text);
        CheckBox mission_32_text = root.findViewById(R.id.mission_32_text);

        db.collection("MissionCheck").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.getData() == null || !document.getString("date").toString().equals(date)) {

                        Random rand = new Random();

                        for (int i = 0; i < 6; i++) {
                            mission[i] = rand.nextInt(50) + 1;
                            for (int j = 0; i < j; j++) {
                                if (mission[i] == mission[j]) {
                                    i--;
                                }
                            }
                        }

                        Map<String, Object> missioncheck = new HashMap<>();
                        missioncheck.put("date", date);
                        missioncheck.put("mission1", String.valueOf(mission[0]));
                        missioncheck.put("mission2", String.valueOf(mission[1]));
                        missioncheck.put("mission3", String.valueOf(mission[2]));
                        missioncheck.put("mission4", String.valueOf(mission[3]));
                        missioncheck.put("mission5", String.valueOf(mission[4]));
                        missioncheck.put("mission6", String.valueOf(mission[5]));

                        db.collection("MissionCheck").document(uid).set(missioncheck);

                    } else {
                        mission[0] = Integer.parseInt(document.getString("mission1").toString());
                        mission[1] = Integer.parseInt(document.getString("mission2").toString());
                        mission[2] = Integer.parseInt(document.getString("mission3").toString());
                        mission[3] = Integer.parseInt(document.getString("mission4").toString());
                        mission[4] = Integer.parseInt(document.getString("mission5").toString());
                        mission[5] = Integer.parseInt(document.getString("mission6").toString());
                    }
                }

                for (int i = 0; i < 6; i++) {
                    int count = i;
                    db.collection("Mission").document(String.valueOf(mission[i])).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                switch (count) {
                                    case 0:
                                        mission_11_text.setText(document.getString("content"));
                                        break;
                                    case 1:
                                        mission_12_text.setText(document.getString("content"));
                                        break;
                                    case 2:
                                        mission_21_text.setText(document.getString("content"));
                                        break;
                                    case 3:
                                        mission_22_text.setText(document.getString("content"));
                                        break;
                                    case 4:
                                        mission_31_text.setText(document.getString("content"));
                                        break;
                                    case 5:
                                        mission_32_text.setText(document.getString("content"));
                                        break;
                                }
                            }
                        }
                    });
                }
            }
        });

        db.collection("MissionClear").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.getData() == null || !document.getString("date").toString().equals(date)) {

                        Map<String, Object> missionclear = new HashMap<>();
                        missionclear.put("date", date);
                        missionclear.put("mission1", false);
                        missionclear.put("mission2", false);
                        missionclear.put("mission3", false);
                        missionclear.put("mission4", false);
                        missionclear.put("mission5", false);
                        missionclear.put("mission6", false);

                        db.collection("MissionClear").document(uid).set(missionclear);
                    } else {
                        if (document.getBoolean("mission1")) {
                            Glide.with(root.getContext())
                                    .load(R.drawable.forsythia)
                                    .into(mission_11_image);
                            mission_11_text.setChecked(true);
                        }
                        if (document.getBoolean("mission2")) {
                            Glide.with(root.getContext())
                                    .load(R.drawable.tulip)
                                    .into(mission_12_image);
                            mission_12_text.setChecked(true);
                        }
                        if (document.getBoolean("mission3")) {
                            Glide.with(root.getContext())
                                    .load(R.drawable.cosmos)
                                    .into(mission_21_image);
                            mission_21_text.setChecked(true);
                        }
                        if (document.getBoolean("mission4")) {
                            Glide.with(root.getContext())
                                    .load(R.drawable.mugunghwa)
                                    .into(mission_22_image);
                            mission_22_text.setChecked(true);
                        }
                        if (document.getBoolean("mission5")) {
                            Glide.with(root.getContext())
                                    .load(R.drawable.morninggory)
                                    .into(mission_31_image);
                            mission_31_text.setChecked(true);
                        }
                        if (document.getBoolean("mission6")) {
                            Glide.with(root.getContext())
                                    .load(R.drawable.valley)
                                    .into(mission_32_image);
                            mission_32_text.setChecked(true);
                        }
                    }
                }
            }
        });


        mission_11_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mission_11_text.isChecked()) {
                    mission_11_text.setChecked(true);
                    Map<String, Object> user = new HashMap<>();
                    Map<String, Object> check = new HashMap<>();
                    final int[] current_exp = new int[1];

                    db.collection("Seed").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Glide.with(root.getContext())
                                        .load(R.drawable.forsythia)
                                        .into(mission_11_image);
                                user.put("date", date);
                                current_exp[0] = Integer.parseInt(document.getString("exp"));
                                user.put("exp", String.valueOf(current_exp[0] + 1));
                                user.put("name", document.getString("name"));
                                user.put("species", document.getString("species"));
                                db.collection("Seed").document(uid).set(user);
                            }
                        }
                    });

                    check.put("date", date);
                    check.put("mission1", mission_11_text.isChecked());
                    check.put("mission2", mission_12_text.isChecked());
                    check.put("mission3", mission_21_text.isChecked());
                    check.put("mission4", mission_22_text.isChecked());
                    check.put("mission5", mission_31_text.isChecked());
                    check.put("mission6", mission_32_text.isChecked());

                    db.collection("MissionClear").document(uid).set(check);
                } else {
                    mission_11_text.setChecked(true);
                }
            }
        });

        mission_12_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mission_12_text.isChecked()) {
                    mission_12_text.setChecked(true);
                    Map<String, Object> user = new HashMap<>();
                    Map<String, Object> check = new HashMap<>();
                    final int[] current_exp = new int[1];

                    db.collection("Seed").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Glide.with(root.getContext())
                                        .load(R.drawable.tulip)
                                        .into(mission_12_image);
                                user.put("date", date);
                                current_exp[0] = Integer.parseInt(document.getString("exp"));
                                user.put("exp", String.valueOf(current_exp[0] + 1));
                                user.put("name", document.getString("name"));
                                user.put("species", document.getString("species"));

                                db.collection("Seed").document(uid).set(user);
                            }
                        }
                    });

                    check.put("date", date);
                    check.put("mission1", mission_11_text.isChecked());
                    check.put("mission2", mission_12_text.isChecked());
                    check.put("mission3", mission_21_text.isChecked());
                    check.put("mission4", mission_22_text.isChecked());
                    check.put("mission5", mission_31_text.isChecked());
                    check.put("mission6", mission_32_text.isChecked());

                    db.collection("MissionClear").document(uid).set(check);
                } else {
                    mission_12_text.setChecked(true);
                }
            }
        });

        mission_21_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mission_21_text.isChecked()) {
                    mission_21_text.setChecked(true);
                    Map<String, Object> user = new HashMap<>();
                    Map<String, Object> check = new HashMap<>();
                    final int[] current_exp = new int[1];

                    db.collection("Seed").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Glide.with(root.getContext())
                                        .load(R.drawable.cosmos)
                                        .into(mission_21_image);
                                user.put("date", date);
                                current_exp[0] = Integer.parseInt(document.getString("exp"));
                                user.put("exp", String.valueOf(current_exp[0] + 1));
                                user.put("name", document.getString("name"));
                                user.put("species", document.getString("species"));

                                db.collection("Seed").document(uid).set(user);
                            }
                        }
                    });

                    check.put("date", date);
                    check.put("mission1", mission_11_text.isChecked());
                    check.put("mission2", mission_12_text.isChecked());
                    check.put("mission3", mission_21_text.isChecked());
                    check.put("mission4", mission_22_text.isChecked());
                    check.put("mission5", mission_31_text.isChecked());
                    check.put("mission6", mission_32_text.isChecked());

                    db.collection("MissionClear").document(uid).set(check);
                } else {
                    mission_21_text.setChecked(true);
                }
            }
        });

        mission_22_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mission_22_text.isChecked()) {
                    mission_22_text.setChecked(true);
                    Map<String, Object> user = new HashMap<>();
                    Map<String, Object> check = new HashMap<>();
                    final int[] current_exp = new int[1];

                    db.collection("Seed").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Glide.with(root.getContext())
                                        .load(R.drawable.mugunghwa)
                                        .into(mission_22_image);
                                user.put("date", date);
                                current_exp[0] = Integer.parseInt(document.getString("exp"));
                                user.put("exp", String.valueOf(current_exp[0] + 1));
                                user.put("name", document.getString("name"));
                                user.put("species", document.getString("species"));

                                db.collection("Seed").document(uid).set(user);
                            }
                        }
                    });

                    check.put("date", date);
                    check.put("mission1", mission_11_text.isChecked());
                    check.put("mission2", mission_12_text.isChecked());
                    check.put("mission3", mission_21_text.isChecked());
                    check.put("mission4", mission_22_text.isChecked());
                    check.put("mission5", mission_31_text.isChecked());
                    check.put("mission6", mission_32_text.isChecked());

                    db.collection("MissionClear").document(uid).set(check);
                } else {
                    mission_22_text.setChecked(true);
                }
            }
        });

        mission_31_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mission_31_text.isChecked()) {
                    mission_31_text.setChecked(true);
                    Map<String, Object> user = new HashMap<>();
                    Map<String, Object> check = new HashMap<>();
                    final int[] current_exp = new int[1];

                    db.collection("Seed").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Glide.with(root.getContext())
                                        .load(R.drawable.morninggory)
                                        .into(mission_31_image);
                                user.put("date", date);
                                current_exp[0] = Integer.parseInt(document.getString("exp"));
                                user.put("exp", String.valueOf(current_exp[0] + 1));
                                user.put("name", document.getString("name"));
                                user.put("species", document.getString("species"));

                                db.collection("Seed").document(uid).set(user);
                            }
                        }
                    });

                    check.put("date", date);
                    check.put("mission1", mission_11_text.isChecked());
                    check.put("mission2", mission_12_text.isChecked());
                    check.put("mission3", mission_21_text.isChecked());
                    check.put("mission4", mission_22_text.isChecked());
                    check.put("mission5", mission_31_text.isChecked());
                    check.put("mission6", mission_32_text.isChecked());

                    db.collection("MissionClear").document(uid).set(check);
                } else {
                    mission_31_text.setChecked(true);
                }
            }
        });

        mission_32_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mission_32_text.isChecked()) {
                    mission_32_text.setChecked(true);
                    Map<String, Object> user = new HashMap<>();
                    Map<String, Object> check = new HashMap<>();
                    final int[] current_exp = new int[1];

                    db.collection("Seed").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Glide.with(root.getContext())
                                        .load(R.drawable.valley)
                                        .into(mission_32_image);
                                user.put("date", date);
                                current_exp[0] = Integer.parseInt(document.getString("exp"));
                                user.put("exp", String.valueOf(current_exp[0] + 1));
                                user.put("name", document.getString("name"));
                                user.put("species", document.getString("species"));

                                db.collection("Seed").document(uid).set(user);
                            }
                        }
                    });

                    check.put("date", date);
                    check.put("mission1", mission_11_text.isChecked());
                    check.put("mission2", mission_12_text.isChecked());
                    check.put("mission3", mission_21_text.isChecked());
                    check.put("mission4", mission_22_text.isChecked());
                    check.put("mission5", mission_31_text.isChecked());
                    check.put("mission6", mission_32_text.isChecked());

                    db.collection("MissionClear").document(uid).set(check);
                } else {
                    mission_32_text.setChecked(true);
                }
            }
        });

        for (CheckBox checkBox : Arrays.asList(mission_11_text, mission_12_text, mission_21_text, mission_22_text, mission_31_text, mission_32_text)) {
            checkBox.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }

        return root;
    }
}
