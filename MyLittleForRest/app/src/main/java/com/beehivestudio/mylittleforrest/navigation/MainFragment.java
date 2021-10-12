package com.beehivestudio.mylittleforrest.navigation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

import com.beehivestudio.mylittleforrest.AddPhotoActivity;
import com.beehivestudio.mylittleforrest.LodingActivity;
import com.beehivestudio.mylittleforrest.MainActivity;
import com.beehivestudio.mylittleforrest.R;
import com.beehivestudio.mylittleforrest.SeedActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainFragment extends Fragment {


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
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        TextView region = root.findViewById(R.id.region);
        LinearLayout ll_weather = root.findViewById(R.id.ll_weather);
        ImageView iv_weather = root.findViewById(R.id.iv_weather);
        TextView famous_saying = root.findViewById(R.id.famous_saying);
        ImageButton ib_plant = root.findViewById(R.id.ib_plant);
        TextView plant_name = root.findViewById(R.id.plant_name);
        ProgressBar exp = root.findViewById(R.id.exp);
        ImageButton upload = root.findViewById(R.id.bt_upload);

        db.collection("Seed").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    int attendance = Integer.parseInt(document.getString("date"));
                    plant_name.setText(document.getString("name"));
                    exp.setProgress(Integer.parseInt(document.getString("exp")));
                    if (attendance - current_date <= 3) {
                        //식물생존 식물이름에 맞는 사진 넣는 명령어
                    } else {
                        //식물죽음 죽음에 맞는 사진 넣는 명령어
                    }
                } else {/*파베에서 데이터 가져오기 실패할때*/}
            }
        });

        ib_plant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exp.getProgress() == 100) {
                    Toast.makeText(root.getContext(), "꽃을 피우신 당신 축하드립니다!", Toast.LENGTH_SHORT).show();
                }
                //죽은식물일때 if 만들기
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사진업로드 코드
                if (ContextCompat.checkSelfPermission(root.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                ) {
                    Intent intent = new Intent(root.getContext(), AddPhotoActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(root.getContext(), "스토리지 읽기 권한이 없습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
        return root;
    }
}