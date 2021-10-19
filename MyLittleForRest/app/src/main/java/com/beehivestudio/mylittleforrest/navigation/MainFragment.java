package com.beehivestudio.mylittleforrest.navigation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beehivestudio.mylittleforrest.AddPhotoActivity;
import com.beehivestudio.mylittleforrest.MainActivity;
import com.beehivestudio.mylittleforrest.R;
import com.beehivestudio.mylittleforrest.SeedActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainFragment extends Fragment {

    phpDown task;

    private final static String appKey = "9c8c143b1bb0efd33f5f41282b0b5075";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();

    String pattern = "yyyyMMdd";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String date = simpleDateFormat.format(new Date());
    int current_date = Integer.parseInt(date);

    TextView region;
    LocationManager lm;
    String weather;
    FrameLayout back;
    ImageView cloud1;
    ImageView cloud2;
    ImageView cloud3;
    ImageView iv_weather;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        back = root.findViewById(R.id.back);
        region = root.findViewById(R.id.region);
        iv_weather = root.findViewById(R.id.iv_weather);
        TextView famous_saying = root.findViewById(R.id.famous_saying);
        ImageView iv_plant = root.findViewById(R.id.iv_plant);
        TextView plant_name = root.findViewById(R.id.plant_name);
        ProgressBar exp = root.findViewById(R.id.exp);
        ImageButton upload = root.findViewById(R.id.bt_upload);

        cloud1 = root.findViewById(R.id.cloud1);
        cloud2 = root.findViewById(R.id.cloud2);
        cloud3 = root.findViewById(R.id.cloud3);

        Animation animation1 = AnimationUtils.loadAnimation(root.getContext(), R.anim.translate1);
        Animation animation2 = AnimationUtils.loadAnimation(root.getContext(), R.anim.translate2);
        Animation animation3 = AnimationUtils.loadAnimation(root.getContext(), R.anim.translate3);
        Animation animation4 = AnimationUtils.loadAnimation(root.getContext(), R.anim.translate4);
        cloud1.startAnimation(animation1);
        cloud2.startAnimation(animation2);
        cloud3.startAnimation(animation3);
        iv_weather.startAnimation(animation4);

        lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        Random rand = new Random();
        int saying_num = rand.nextInt(10) + 1;


        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(root.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) root.getContext(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, gpsLocationListener);

        }


        db.collection("saying").document(String.valueOf(saying_num)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    famous_saying.setText(document.getString("content"));
                }
            }
        });


        db.collection("Seed").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    int attendance = Integer.parseInt(document.getString("date"));
                    plant_name.setText(document.getString("name"));
                    exp.setProgress(Integer.parseInt(document.getString("exp")));
                    if (current_date - attendance <= 3) {
                        if (exp.getProgress() > 70) {
                            String species = document.getString("species");
                            if (species.equals("rose"))
                                Glide.with(root.getContext())
                                        .load(R.drawable.rose)
                                        .into(iv_plant);
                            else if (species.equals("sunflower"))
                                Glide.with(root.getContext())
                                        .load(R.drawable.sunflower)
                                        .into(iv_plant);
                            else if (species.equals("rosemoss"))
                                Glide.with(root.getContext())
                                        .load(R.drawable.rosemoss)
                                        .into(iv_plant);
                            //업데이트용
//                            else if (species.equals("forsythia"))
//                                Glide.with(root.getContext())
//                                        .load(R.drawable.forsythia)
//                                        .into(iv_plant);
//                            else if (species.equals("tulip"))
//                                Glide.with(root.getContext())
//                                        .load(R.drawable.tulip)
//                                        .into(iv_plant);
//                            else if (species.equals("cosmos"))
//                                Glide.with(root.getContext())
//                                        .load(R.drawable.cosmos)
//                                        .into(iv_plant);
//                            else if (species.equals("valley"))
//                                Glide.with(root.getContext())
//                                        .load(R.drawable.valley)
//                                        .into(iv_plant);
//                            else if (species.equals("mugunghwa"))
//                                Glide.with(root.getContext())
//                                        .load(R.drawable.mugunghwa)
//                                        .into(iv_plant);
//                            else if (species.equals("morninggory"))
//                                Glide.with(root.getContext())
//                                        .load(R.drawable.morninggory)
//                                        .into(iv_plant);
                        } else {
                            //새싹파트
                            Glide.with(root.getContext())
                                    .load(R.drawable.seed)
                                    .into(iv_plant);
                        }
                    } else {
                        Glide.with(root.getContext())
                                .load(R.drawable.death)
                                .into(iv_plant);
                    }
                } else {/*파베에서 데이터 가져오기 실패할때*/}
            }
        });

        iv_plant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exp.getProgress() == 100) {
                    Toast.makeText(root.getContext(), "꽃을 피우신 당신 축하드립니다!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(root.getContext(), SeedActivity.class);
                    startActivity(intent);
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


    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();

            task = new phpDown();

            task.execute("https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + appKey);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };


    private class phpDown extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                // 연결 url 설정
                URL url = new URL(urls[0]);
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 연결되었으면.
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            if (line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return jsonHtml.toString();
        }


        protected void onPostExecute(String str) {
            try {
                JSONObject jsonObject = new JSONObject(str);
                //현재온도
                int temperature = jsonObject.getJSONObject("main").getInt("temp") - 273;
                //현재습도
                String humidity = jsonObject.getJSONObject("main").getString("humidity");
                //현재날씨
                weather = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
                //도시
                String city = jsonObject.getString("name");
                region.setText("지역 : " + city + "\n온도 : " + String.valueOf(temperature) + "ºC\n습도 : " + humidity + "%\n기상 : " + weather);

                if(weather.equals("Clear")) {
                    back.setBackgroundResource(R.drawable.clooudbackground);
                    Glide.with(getContext())
                            .load(R.drawable.blackcloud)
                            .into(cloud1);
                    Glide.with(getContext())
                            .load(R.drawable.blackcloud)
                            .into(cloud2);
                    Glide.with(getContext())
                            .load(R.drawable.blackcloud)
                            .into(cloud3);
                    Glide.with(getContext())
                            .load(R.drawable.blackcloud)
                            .into(iv_weather);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}