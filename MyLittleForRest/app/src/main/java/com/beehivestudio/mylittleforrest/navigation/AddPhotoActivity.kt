package com.beehivestudio.mylittleforrest

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*


class AddPhotoActivity : AppCompatActivity() {
    var storage : FirebaseStorage ?= null
    var photoUri : Uri? = null
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        //Init
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        addphoto_image.setOnClickListener{
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            launcher.launch(photoPickerIntent);
        }


        addphoto_btn_upload.setOnClickListener {
            if(rb_public.isChecked) {
                contentUpload("images")
            }else if (rb_private.isChecked){
                contentUpload("private")
           }else{
                Toast.makeText(this,"게시 방법을 선택해주세요!",Toast.LENGTH_SHORT).show()
            }
        }
    }

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            //This is path to the selected image
            photoUri = result.data?.data
            addphoto_image.setImageURI(photoUri)

        } else {
            // Exit the addPhotoActivity if you leave the album without selecting it
            finish()
        }
    }

    fun contentUpload(division: String) {
        //Make filename

        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"

        var storageRef = storage?.reference?.child(division)?.child(imageFileName)

        // 두 가지 방식 중 가독성이 좋은 것 택
        //Promise method -> 더 보편적
        storageRef?.putFile(photoUri!!)?.continueWithTask { task: Task<UploadTask.TaskSnapshot>->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri ->
            var contentDTO = ContentDTO()

            //Insert downloadUrl of image
            contentDTO.imageUrl = uri.toString()

            //Insert uid of user
            contentDTO.uid = auth?.currentUser?.uid

            //Insert userId
            contentDTO.userId = auth?.currentUser?.email

            //Insert explain of content
            contentDTO.explain = addphoto_edit_explain.text.toString()

            //Insert timestamp
            contentDTO.timestamp = System.currentTimeMillis()

            firestore?.collection(division)?.document()?.set(contentDTO)

            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}