package com.beehivestudio.mylittleforrest

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_content_detail.*
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.item_detail.view.*


class ContentDetailActivity : AppCompatActivity() {


    var user: FirebaseUser? = null
    var content_commentSnapshot: ListenerRegistration? = null
    var firestore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_detail)

        user = FirebaseAuth.getInstance().currentUser

        content_detailviewitem_explain_textview.setText(intent.getStringExtra("explain"))
        Glide.with(this)
            .load(Uri.parse(intent.getStringExtra("imageUrl")))
            .into(content_detailviewitem_imageview_content)

        content_comment_recyclerview.adapter = content_CommentRecyclerViewAdapter()
        content_comment_recyclerview.layoutManager = LinearLayoutManager(this)

        content_comment_btn_send.setOnClickListener {
            val comment = ContentDTO.Comment()

            comment.userId = FirebaseAuth.getInstance().currentUser!!.email
            comment.comment = content_comment_edit_message.text.toString()
            comment.uid = FirebaseAuth.getInstance().currentUser!!.uid
            comment.timestamp = System.currentTimeMillis()

            FirebaseFirestore.getInstance()
                .collection("images")
                .document(intent.getStringExtra("contentUid")!!)
                .collection("comments")
                .document()
                .set(comment)

        }

    }

    override fun onStop() {
        super.onStop()
        content_commentSnapshot?.remove()
    }


    inner class content_CommentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        val content_comments: ArrayList<ContentDTO.Comment>

        init {
            content_comments = ArrayList()
            content_commentSnapshot = FirebaseFirestore
                .getInstance()
                .collection("images")
                .document(intent.getStringExtra("contentUid")!!)
                .collection("comments")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    content_comments.clear()
                    if (querySnapshot == null) return@addSnapshotListener
                    for (snapshot in querySnapshot?.documents!!) {
                        content_comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
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
                .document(content_comments[position].uid!!)
                .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (documentSnapshot?.data != null) {

                        val url = documentSnapshot?.data!!["image"]
                        Glide.with(holder.itemView.context)
                            .load(url)
                            .apply(RequestOptions().circleCrop()).into(view.commentviewitem_imageview_profile)
                    }
                }

            view.commentviewitem_textview_profile.text = content_comments[position].userId
            view.commentviewitem_textview_comment.text = content_comments[position].comment
        }

        override fun getItemCount(): Int {

            return content_comments.size
        }

        private inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

}
