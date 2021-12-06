package com.beehivestudio.mylittleforrest;

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


class EmailLoginActivity : AppCompatActivity() {

    // Firebase Authentication 관리 클래스
    var auth: FirebaseAuth? = null
    val MY_PERMISSION_ACCESS_ALL = 1000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_NETWORK_STATE
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            var permissions = arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSION_ACCESS_ALL)
        }
        // Firebase 로그인 통합 관리하는 Object 만들기
        auth = FirebaseAuth.getInstance()
        email_login_button.setOnClickListener{ emailLogin() }
        phone_sign_in_button.setOnClickListener { startActivity(Intent(this, PhoneLoginActivity::class.java)) }
        register_button.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
    }


    fun moveMainPage(user: FirebaseUser?) {

        // User is signed in
        if (user != null) {
            Toast.makeText(this, getString(R.string.signin_complete), Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LodingActivity::class.java))
            finish()
        }
    }


    fun emailLogin() {

        if (email_edittext.text.toString().isNullOrEmpty() || password_edittext.text.toString()
                .isNullOrEmpty()
        ) {
            Toast.makeText(this, getString(R.string.signout_fail_null), Toast.LENGTH_SHORT).show()

        } else {
            progress_bar.visibility = View.VISIBLE
            signinEmail()
        }
    }

    //로그인 메소드
    fun signinEmail() {
        auth?.signInWithEmailAndPassword(
            email_edittext.text.toString(),
            password_edittext.text.toString()
        )
            ?.addOnCompleteListener { task ->
                progress_bar.visibility = View.GONE
                if (task.isSuccessful) {
                    Log.d("test", auth?.currentUser!!.isEmailVerified.toString())
                    if (!auth?.currentUser!!.isEmailVerified){
                        Toast.makeText(this, "메일 확인 후 사용해주세요.", Toast.LENGTH_SHORT).show()
                    }else {
                        moveMainPage(auth?.currentUser)
                    }
                } else {
                    //로그인 실패
                    Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }

    }

//    override fun onStart() {
//        super.onStart()
//
//        //자동 로그인 설정
//        moveMainPage(auth?.currentUser)
//
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
        if (requestCode === MY_PERMISSION_ACCESS_ALL) {
            if (grantResults.size > 0) {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) System.exit(0)
                }
            }
        }
    }

}