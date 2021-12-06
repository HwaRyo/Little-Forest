package com.beehivestudio.mylittleforrest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String mVerificationId;
    private static final String TAG = "PHONE_TAG";
    private FirebaseAuth firebaseAuth;

    private ProgressDialog pd;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        LinearLayout phone_get_lo = findViewById(R.id.phone_get_lo);
        EditText phone_gnumber_et = findViewById(R.id.phone_gnumber_et);
        Button phone_gnumber_btn = findViewById(R.id.phone_gnumber_btn);
        LinearLayout phone_set_lo = findViewById(R.id.phone_set_lo);
        EditText phone_snumber_et = findViewById(R.id.phone_snumber_et);
        Button phone_snumber_btn = findViewById(R.id.phone_snumber_btn);
        TextView phone_resnumber_tv = findViewById(R.id.phone_resnumber_tv);
        TextView phone_snumber_text = findViewById(R.id.phone_snumber_text);

        firebaseAuth = FirebaseAuth.getInstance();

        phone_set_lo.setVisibility(View.GONE);
        phone_get_lo.setVisibility(View.VISIBLE);

        pd = new ProgressDialog(this);
        pd.setTitle("잠시만 기다려주세요...");
        pd.setCanceledOnTouchOutside(false);

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signinPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pd.dismiss();
                Toast.makeText(PhoneLoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token){
                super.onCodeSent(verificationId, forceResendingToken);
                Log.d(TAG, "onCodeSent: "+ verificationId);

                mVerificationId = verificationId;
                forceResendingToken = token;
                pd.dismiss();

                phone_get_lo.setVisibility(View.GONE);
                phone_set_lo.setVisibility(View.VISIBLE);

                Toast.makeText(PhoneLoginActivity.this, "인증코드 전송중", Toast.LENGTH_SHORT).show();

                phone_snumber_text.setText("인증번호를 입력해주세요\n"+phone_gnumber_et.getText().toString().trim());
            }
        };

        phone_gnumber_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = phone_gnumber_et.getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(PhoneLoginActivity.this, "핸드폰 번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    startPhoneNumberVerification(phone);
                }
            }
        });

        phone_resnumber_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = phone_gnumber_et.getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(PhoneLoginActivity.this, "핸드폰 번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    resendPhoneNumberVerification(phone, forceResendingToken);
                }
            }
        });

        phone_snumber_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = phone_snumber_btn.getText().toString().trim();
                if(TextUtils.isEmpty(code)){
                    Toast.makeText(PhoneLoginActivity.this, "인증번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    verifyPhoneNumberCode(mVerificationId, code);
                }
            }
        });
    }

    private void startPhoneNumberVerification(String phone) {
        pd.setMessage("전화번호 확인중...");
        pd.show();


        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallback)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void resendPhoneNumberVerification(String phone, PhoneAuthProvider.ForceResendingToken token) {
        pd.setMessage("인증코드 재전송");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber("+821039406993")
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallback)
                        .setForceResendingToken(token)
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyPhoneNumberCode(String mVerificationId, String code) {
        pd.setMessage("인증코드 확인중...");
        pd.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signinPhoneAuthCredential(credential);

    }

    private void signinPhoneAuthCredential(PhoneAuthCredential credential) {
        pd.setMessage("접속중...");

        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        pd.dismiss();
                        String phone = firebaseAuth.getCurrentUser().getPhoneNumber();
                        Toast.makeText(PhoneLoginActivity.this, "접속중...", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(PhoneLoginActivity.this, MainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(PhoneLoginActivity.this, ""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
