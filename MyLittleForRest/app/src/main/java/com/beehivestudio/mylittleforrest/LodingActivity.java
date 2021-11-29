package com.beehivestudio.mylittleforrest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.beehivestudio.mylittleforrest.navigation.MainFragment;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LodingActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    private final static String appKey = "9c8c143b1bb0efd33f5f41282b0b5075";
    LocationManager lm;
    apiDown task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loding);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(LodingActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LodingActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            lm.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    task = new apiDown();
                    task.execute("https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + appKey);
                    Log.d("longitude", String.valueOf(longitude));
                    Log.d("latitude", String.valueOf(latitude));
                }
            }, null);
        }
    }

    private class apiDown extends AsyncTask<String, Integer, String> {
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
                String weather = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
                //도시
                String city = jsonObject.getString("name");

                Double lon = jsonObject.getJSONObject("coord").getDouble("lon");
                Double lat = jsonObject.getJSONObject("coord").getDouble("lat");


                Map<String, Object> gps = new HashMap<>();
                gps.put("temperature", String.valueOf(temperature));
                gps.put("humidity", humidity);
                gps.put("weather", weather);

                List<Address> addresses = null;
                try {
                    Geocoder geocoder = new Geocoder(LodingActivity.this, Locale.getDefault());
                    addresses = geocoder.getFromLocation(lat, lon, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (addresses == null || addresses.size() == 0) {
                    Log.d("location", "미발견");
                } else {
                    Address address = addresses.get(0);
                    String arr[] = address.getAddressLine(0).split(" ");
                    String tmp1 = String.valueOf(arr[1].charAt(arr[1].length()-1));
                    String tmp2 = String.valueOf(arr[2].charAt(arr[2].length()-1));
                    if(tmp1.equals("시")) {
                        gps.put("city", arr[1]);
                    }else if (tmp2.equals("시")) {
                        gps.put("city", arr[2]);
                    }else{
                        gps.put("city", city);
                    }
                }

                db.collection("gps").document(uid).set(gps);

                db.collection("Seed").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.getData() != null) {
                                Intent intent = new Intent(LodingActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                LodingActivity.this.startActivity(intent);
                            } else {
                                Intent intent = new Intent(LodingActivity.this, SeedActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                LodingActivity.this.startActivity(intent);
                            }
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}