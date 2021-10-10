package com.beehivestudio.mylittleforrest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.beehivestudio.mylittleforrest.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class EmailLoginActivity : AppCompatActivity() {
    var auth : FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        var bt_login: Button = findViewById(R.id.email_login_button)
        bt_login.setOnClickListener{
            signinAndSignup()
        }
    }
    fun signinAndSignup() {
        var et_email: EditText = findViewById(R.id.email_edittext)
        var et_password: EditText = findViewById(R.id.password_edittext)
        auth?.createUserWithEmailAndPassword(et_email.text.toString(), et_password.text.toString())
            ?.addOnCompleteListener {
                    task ->
                if (task.isSuccessful) {    //계정 만들기에 성공했을 때
                    //Creating a user account
                    moveMainPage(task.result?.user)
                } else {
                    //Login if you have account
                    if (task.exception?.message.equals("The email address is already in use by another account."))
                        signinEmail()
                    //Show the error message
                    else Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }
    fun signinEmail(){
        var et_email: EditText = findViewById(R.id.email_edittext)
        var et_password: EditText = findViewById(R.id.password_edittext)
        auth?.createUserWithEmailAndPassword(et_email.text.toString(), et_password.text.toString())
            ?.addOnCompleteListener {
                    task ->
                if (task.isSuccessful) {    //계정 만들기에 성공했을 때
                    //Creating a user account
                    moveMainPage(task.result?.user)
                } else {
                    //Login if you have account
                    if (task.exception?.message.equals("The email address is already in use by another account."))
                        signinEmail()
                    //Show the error message
                    else Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        moveMainPage(auth?.currentUser)
    }

    fun moveMainPage(user:FirebaseUser?){
        if(user != null) {
            startActivity(Intent(this, LodingActivity::class.java))
            finish()
        }
    }

}