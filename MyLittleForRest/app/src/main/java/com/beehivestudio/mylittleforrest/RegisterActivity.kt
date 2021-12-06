package com.beehivestudio.mylittleforrest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        register_toolbar_btn_back.setOnClickListener { onBackPressed() }
        auth = FirebaseAuth.getInstance()


        btn_reg.setOnClickListener {
            if (et_email.text.toString().isNullOrEmpty() || et_pass.text.toString()
                    .isNullOrEmpty() || et_pass1.text.toString()
                    .isNullOrEmpty()
            ) {
                Toast.makeText(this, "공백없이 작성해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                if (et_pass.text.length < 6 || et_pass1.text.length < 6) {
                    Toast.makeText(this, "비밀번호를 최소 6자리 이상 해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    if (et_pass.text.toString().equals(et_pass1.text.toString())) {
                        createAndLoginEmail()
                    } else {
                        Toast.makeText(this, "비밀번호가 서로 다릅니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }


    //이메일 회원가입 및 로그인 메소드
    fun createAndLoginEmail() {
        auth?.createUserWithEmailAndPassword(
            et_email.text.toString(),
            et_pass.text.toString()
        )
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //아이디 생성이 성공했을 경우
                    auth?.currentUser?.sendEmailVerification()
                    Toast.makeText(this, "회원가입 성공했습니다.\n메일 확인 후 인증후 사용해주세요!.", Toast.LENGTH_SHORT)
                        .show()
                    onBackPressed()
                } else if (task.exception?.message.isNullOrEmpty()) {
                    //회원가입 에러가 발생했을 경우
                    Toast.makeText(this, "회원가입 도중 에러가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    try {
                        task.getResult();
                    } catch (e: Exception) {
                        e.printStackTrace();
                        Toast.makeText(this, "이미있는 이메일 형식입니다 다시 입력해주세요", Toast.LENGTH_LONG).show();
                    }
                }
            }
    }
}