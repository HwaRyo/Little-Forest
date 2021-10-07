package com.codingpotetoes.littleforest

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper

import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.codingpotetoes.littleforest.R.id.*
import com.codingpotetoes.littleforest.navigation.MainFragment
import com.codingpotetoes.littleforest.navigation.MissionFragment
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    val PICK_PROFILE_FROM_ALBUM = 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Bottom Navigation View
        bottom_navigation.setOnNavigationItemSelectedListener(this)
        bottom_navigation.selectedItemId = R.id.action_main

        // 앨범 접근 권한 요청
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )
    }


    fun setToolbarDefault() {
        toolbar_title_image.visibility = View.VISIBLE
        toolbar_btn_back.visibility = View.GONE
        toolbar_username.visibility = View.GONE
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        setToolbarDefault()
        when (item.itemId) {
            R.id.action_main -> {
                val mainFragment = MainFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, mainFragment)
                    .commit()
                return true
            }
            R.id.action_mission -> {
                val missionFragment = MissionFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, missionFragment)
                    .commit()
                return true
            }
            R.id.action_community -> {

                val detailViewFragment = DetailViewFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, detailViewFragment)
                    .commit()
                return true
            }
            R.id.action_search -> {
                val gridFragment = GridFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_content, gridFragment)
                    .commit()
                return true
            }

            //사진업로드 코드
//            R.id.action_add_photo -> {
//                if (ContextCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.READ_EXTERNAL_STORAGE
//                    ) == PackageManager.PERMISSION_GRANTED
//                ) {
//                    startActivity(Intent(this, AddPhotoActivity::class.java))
//                } else {
//                    Toast.makeText(this, "스토리지 읽기 권한이 없습니다.", Toast.LENGTH_LONG).show()
//                }
//                return true
//            }

            R.id.action_account -> {
                val userFragment = UserFragment()
                val uid = FirebaseAuth.getInstance().currentUser!!.uid
                val bundle = Bundle()
                bundle.putString("destinationUid", uid)
                userFragment.arguments = bundle
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, userFragment)
                    .commit()
                return true
            }
        }
        return false
    }

}