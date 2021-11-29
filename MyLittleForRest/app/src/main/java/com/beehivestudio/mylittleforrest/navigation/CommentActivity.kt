package com.beehivestudio.mylittleforrest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.beehivestudio.mylittleforrest.R
import com.beehivestudio.mylittleforrest.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_user.view.*
import kotlinx.android.synthetic.main.item_comment.view.*

class CommentActivity : AppCompatActivity() {

    var contentUid: String? = null
    var user: FirebaseUser? = null
    var destinationUid: String? = null
    var commentSnapshot: ListenerRegistration? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        user = FirebaseAuth.getInstance().currentUser
        destinationUid = intent.getStringExtra("destinationUid")
        contentUid = intent.getStringExtra("contentUid")

        comment_recyclerview.adapter = CommentRecyclerViewAdapter()
        comment_recyclerview.layoutManager = LinearLayoutManager(this)

        comment_btn_send.setOnClickListener {
            val comment = ContentDTO.Comment()

            comment.userId = FirebaseAuth.getInstance().currentUser!!.email
            comment.comment = comment_edit_message.text.toString()
            comment.uid = FirebaseAuth.getInstance().currentUser!!.uid
            comment.timestamp = System.currentTimeMillis()

            FirebaseFirestore.getInstance()
                .collection("images")
                .document(contentUid!!)
                .collection("comments")
                .document()
                .set(comment)
        }

        comment_toolbar_btn_back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onStop() {
        super.onStop()
        commentSnapshot?.remove()
    }


    inner class CommentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        val comments: ArrayList<ContentDTO.Comment>

        init {
            comments = ArrayList()
            commentSnapshot = FirebaseFirestore
                .getInstance()
                .collection("images")
                .document(contentUid!!)
                .collection("comments")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    comments.clear()
                    if (querySnapshot == null) return@addSnapshotListener
                    for (snapshot in querySnapshot?.documents!!) {
                        comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
                    }
                    notifyDataSetChanged()

                }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_comment, parent, false)
            return CustomViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            var view = holder.itemView

            // Profile Image
            FirebaseFirestore.getInstance()
                .collection("profileImages")
                .document(comments[position].uid!!)
                .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (documentSnapshot?.data != null) {

                        val url = documentSnapshot?.data!!["image"]
                        Glide.with(holder.itemView.context)
                            .load(url)
                            .apply(RequestOptions().circleCrop()).into(view.commentviewitem_imageview_profile)
                    }
                }

            view.commentviewitem_textview_profile.text = comments[position].userId
            view.commentviewitem_textview_comment.text = comments[position].comment
        }

        override fun getItemCount(): Int {

            return comments.size
        }

        private inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

}
