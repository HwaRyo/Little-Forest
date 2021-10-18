package com.beehivestudio.mylittleforrest


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beehivestudio.mylittleforrest.MainActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.squareup.okhttp.OkHttpClient
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_detail.view.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.fragment_user.*

import java.util.*


class DetailViewFragment : Fragment() {

    var user: FirebaseUser? = null
    var firestore: FirebaseFirestore? = null
    var imagesSnapshot: ListenerRegistration? = null
    var okHttpClient: OkHttpClient? = null
    var mainView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        user = FirebaseAuth.getInstance().currentUser
        firestore = FirebaseFirestore.getInstance()
        okHttpClient = OkHttpClient()

        //리사이클러 뷰와 어뎁터랑 연결
        mainView = inflater.inflate(R.layout.fragment_detail, container, false)

        return mainView
    }

    override fun onResume() {
        super.onResume()

        mainView?.detailviewfragment_recyclerview?.layoutManager = LinearLayoutManager(activity)
        mainView?.detailviewfragment_recyclerview?.adapter = DetailRecyclerViewAdapter()
        var mainActivity = activity as MainActivity
        mainActivity.progress_bar.visibility = View.INVISIBLE

    }

    override fun onStop() {
        super.onStop()
        imagesSnapshot?.remove()
    }

    inner class DetailRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val contentDTOs: ArrayList<ContentDTO>
        val contentUidList: ArrayList<String>

        init {
            contentDTOs = ArrayList()
            contentUidList = ArrayList()
            var uid = FirebaseAuth.getInstance().currentUser?.uid
            firestore?.collection("users")?.document(uid!!)?.get()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var userDTO = task.result!!.toObject(FollowDTO::class.java)
                    if (userDTO?.followings != null) {
                        getCotents(userDTO?.followings)
                    }
                }
            }
        }

        fun getCotents(followers: MutableMap<String, Boolean>?) {
            imagesSnapshot = firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                contentUidList.clear()
                if (querySnapshot == null) return@addSnapshotListener
                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ContentDTO::class.java)!!
                    println(item.uid)
                    if (followers?.keys?.contains(item.uid)!!) {
                        contentDTOs.add(item)
                        contentUidList.add(snapshot.id)
                    }
                }
                notifyDataSetChanged()
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)
            return CustomViewHolder(view)

        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            val viewHolder = (holder as CustomViewHolder).itemView

            // Profile Image 가져오기
            firestore?.collection("profileImages")?.document(contentDTOs[position].uid!!)
                ?.get()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val url = task.result?.get("image")
                        Glide.with(holder.itemView.context)
                            .load(url)
                            .apply(RequestOptions().circleCrop()).into(viewHolder.detailviewitem_profile_image)

                    }
                }

            //UserFragment로 이동
            viewHolder.detailviewitem_profile_image.setOnClickListener {

                val fragment = UserFragment()
                val bundle = Bundle()

                bundle.putString("destinationUid", contentDTOs[position].uid)
                bundle.putString("userId", contentDTOs[position].userId)

                fragment.arguments = bundle
                activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit()
            }

            // 유저 아이디
            viewHolder.detailviewitem_profile_textview.text = contentDTOs[position].userId

            // 가운데 이미지
            Glide.with(holder.itemView.context)
                .load(contentDTOs[position].imageUrl)
                .into(viewHolder.detailviewitem_imageview_content)

            // 설명 텍스트
            viewHolder.detailviewitem_explain_textview.text = contentDTOs[position].explain

            viewHolder.detailviewitem_comment_imageview.setOnClickListener{v ->
                var intent = Intent(v.context,CommentActivity::class.java)
                intent.putExtra("contentUid",contentUidList[position])
                startActivity(intent)
            }

            viewHolder.detailviewitem_imageview_content.setOnClickListener {
                val nextIntent = Intent(holder.itemView.context, ContentDetailActivity::class.java)
                nextIntent.putExtra("imageUrl", contentDTOs[position].imageUrl)
                nextIntent.putExtra("explain", contentDTOs[position].explain)
                nextIntent.putExtra("contentUid",contentUidList[position])
                startActivity(nextIntent)
            }
        }


        override fun getItemCount(): Int {

            return contentDTOs.size

        }
    }

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}