package com.beehivestudio.mylittleforrest.navigation;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beehivestudio.mylittleforrest.MainActivity;
import com.beehivestudio.mylittleforrest.R;
import com.bumptech.glide.Glide;
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


public class MissionFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();

    String pattern = "yyyyMMdd";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String date = simpleDateFormat.format(new Date());
    int current_date = Integer.parseInt(date);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mission, container, false);


        ImageView mission_11_image = root.findViewById(R.id.mission_11_image);
        ImageView mission_12_image = root.findViewById(R.id.mission_12_image);
        ImageView mission_21_image = root.findViewById(R.id.mission_21_image);
        ImageView mission_22_image = root.findViewById(R.id.mission_22_image);
        TextView mission_11_text = root.findViewById(R.id.mission_11_text);
        TextView mission_12_text = root.findViewById(R.id.mission_12_text);
        TextView mission_21_text = root.findViewById(R.id.mission_21_text);
        TextView mission_22_text = root.findViewById(R.id.mission_22_text);


        for(int i : new int[]{1, 2, 3, 4}) {
            db.collection("Mission").document(String.valueOf(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        switch (i){
                            case 1:
//                                Glide.with(root.getContext())
//                                        .load(url)
//                                        .into(mission_11_image);
                                mission_11_text.setText(document.getString("content"));
                                break;
                            case 2:
//                                Glide.with(root.getContext())
//                                        .load(url)
//                                        .into(mission_12_image);
                                mission_12_text.setText(document.getString("content"));
                                break;
                            case 3:
//                                Glide.with(root.getContext())
//                                        .load(url)
//                                        .into(mission_21_image);
                                mission_21_text.setText(document.getString("content"));
                                break;
                            case 4:
//                                Glide.with(root.getContext())
//                                        .load(url)
//                                        .into(mission_22_image);
                                mission_22_text.setText(document.getString("content"));
                                break;
                        }

                    }
                }
            });
        }

        ImageButton.OnClickListener onClickListener = new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> user = new HashMap<>();
                final int[] current_exp = new int[1];
                final String[] current = new String[3];
                db.collection("Seed").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            user.put("date", date);
                            current_exp[0] = Integer.parseInt(document.getString("exp"));
                            user.put("exp", String.valueOf(current_exp[0]+1));
                            user.put("name", document.getString("name"));
                            user.put("species", document.getString("species"));

                            db.collection("Seed").document(uid).set(user);
                        }
                    }
                });
            }
        };

        mission_11_image.setOnClickListener(onClickListener);
        mission_12_image.setOnClickListener(onClickListener);
        mission_21_image.setOnClickListener(onClickListener);
        mission_22_image.setOnClickListener(onClickListener);

        return root;
    }
}


