package com.beehivestudio.mylittleforrest

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beehivestudio.mylittleforrest.R.string.follow
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.Task

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.fragment_user.view.*
import java.util.ArrayList

class UserFragment : Fragment() {
    var fragmentView: View? = null
    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    var auth: FirebaseAuth? = null
    var currentUserUid: String? = null
    var followingListenerRegistration: ListenerRegistration? = null
    var followListenerRegistration: ListenerRegistration? = null
    var data_document: ArrayList<String> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView =
            LayoutInflater.from(activity).inflate(R.layout.fragment_user, container, false)
        uid = arguments?.getString("destinationUid")        // MainActivity에서 전달받은 user Id
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserUid = auth?.currentUser?.uid

        if (uid == currentUserUid) {
            //MyPage
            fragmentView?.account_btn_follow_signout?.text = getString(R.string.signout)
            fragmentView?.account_btn_follow_signout?.setOnClickListener {
                activity?.finish()
                startActivity(Intent(activity, EmailLoginActivity::class.java))
                auth?.signOut()
            }
        } else {
            //OtherUserPage
            fragmentView?.account_btn_follow_signout?.text = getString(follow)
            var mainactivity = (activity as MainActivity)
            val menu = arguments?.getString("menu")

            mainactivity?.toolbar_username?.text = arguments?.getString("userId")

            mainactivity?.toolbar_btn_back?.setOnClickListener {
                if(menu.equals("Search")){
                    mainactivity.bottom_navigation.selectedItemId = R.id.action_search
                    mainactivity?.toolbar_title_image?.visibility = View.VISIBLE
                    mainactivity?.toolbar_username?.visibility = View.GONE
                    mainactivity?.toolbar_btn_back?.visibility = View.GONE
                }else {
                    mainactivity.bottom_navigation.selectedItemId = R.id.action_community
                    mainactivity?.toolbar_title_image?.visibility = View.VISIBLE
                    mainactivity?.toolbar_username?.visibility = View.GONE
                    mainactivity?.toolbar_btn_back?.visibility = View.GONE
                }
            }
            mainactivity?.toolbar_title_image?.visibility = View.GONE
            mainactivity?.toolbar_username?.visibility = View.VISIBLE
            mainactivity?.toolbar_btn_back?.visibility = View.VISIBLE
            fragmentView?.account_btn_follow_signout?.setOnClickListener {
                requestFollow()
            }
        }

        fragmentView?.account_recyclerview?.adapter = UserFragmentRecyclerViewAdapter("images")


        fragmentView?.user_rb_private?.setOnClickListener {
            if (uid == currentUserUid) {
                fragmentView?.account_recyclerview?.adapter =
                    UserFragmentRecyclerViewAdapter("private")
            } else {
                Toast.makeText(fragmentView?.context, "Private는 자신만 볼 수 있습니다!", Toast.LENGTH_SHORT)
                    .show()
                fragmentView?.user_radio_group?.check(fragmentView?.user_rb_public?.id!!)
            }
        }

        fragmentView?.user_rb_public?.setOnClickListener {
            fragmentView?.account_recyclerview?.adapter = UserFragmentRecyclerViewAdapter("images")
        }


        // 리싸이클러뷰에 어댑터 설정
        fragmentView?.account_recyclerview?.layoutManager =
            GridLayoutManager(activity, 3)       // 열이 3개인 gridLayout


        fragmentView?.account_iv_profile?.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            launcher.launch(photoPickerIntent);
        }
        getProfileImage()
        getFollowing()
        getFollower()


        fragmentView?.book?.setOnClickListener {
            if (uid == currentUserUid) {
                startActivity(Intent(this.context, BookActivity::class.java))
            } else {
                Toast.makeText(this.context, "자신의 도감만 확인할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        return fragmentView
    }

    val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                var imageUri = result.data?.data
                var uid = FirebaseAuth.getInstance().currentUser?.uid
                var storageRef = FirebaseStorage.getInstance().reference.child("userProfileImages")
                    .child(uid!!)    // uid와 맞는 유저의 프로필 이미지 참조 정보
                storageRef.putFile(imageUri!!)
                    .continueWithTask { task: Task<UploadTask.TaskSnapshot> ->                                               // 프로필 이미지 업로드
                        return@continueWithTask storageRef.downloadUrl
                    }.addOnSuccessListener { uri ->
                        var map = HashMap<String, Any>()
                        map["image"] = uri.toString()
                        FirebaseFirestore.getInstance().collection("profileImages").document(uid)
                            .set(map)           // 바꾼 이미지의 주소 저장
                    }
            }
        }

    fun requestFollow() {


        var tsDocFollowing = firestore!!.collection("users").document(currentUserUid!!)
        firestore?.runTransaction { transaction ->

            var followDTO = transaction.get(tsDocFollowing).toObject(FollowDTO::class.java)
            if (followDTO == null) {

                followDTO = FollowDTO()
                followDTO.followingCount = 1
                followDTO.followings[uid!!] = true

                transaction.set(tsDocFollowing, followDTO)
                return@runTransaction

            }
            // Unstar the post and remove self from stars
            if (followDTO?.followings?.containsKey(uid)!!) {

                followDTO?.followingCount = followDTO?.followingCount - 1
                followDTO?.followings.remove(uid)
            } else {

                followDTO?.followingCount = followDTO?.followingCount + 1
                followDTO?.followings[uid!!] = true
            }
            transaction.set(tsDocFollowing, followDTO)
            return@runTransaction
        }
        //Save data to third person
        var tsDocFollower = firestore?.collection("users")?.document(uid!!)
        firestore?.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollower!!).toObject(FollowDTO::class.java)
            if (followDTO == null) {
                followDTO = FollowDTO()
                followDTO!!.followerCount = 1
                followDTO!!.followers[currentUserUid!!] = true

                transaction.set(tsDocFollower, followDTO!!)
                return@runTransaction
            }

            if (followDTO!!.followers.containsKey(currentUserUid)) {
                //It cancel my follower when I follow a third person
                followDTO!!.followerCount -= 1
                followDTO!!.followers.remove(currentUserUid)
            } else {
                //It add my follower when I don't follow a third person
                followDTO!!.followerCount += 1
                followDTO!!.followers[currentUserUid!!] = true
            }
            transaction.set(tsDocFollower, followDTO!!)
            return@runTransaction
        }
    }

    fun getFollowing() {
        followingListenerRegistration = firestore?.collection("users")?.document(uid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                val followDTO = documentSnapshot?.toObject(FollowDTO::class.java)
                if (followDTO == null) return@addSnapshotListener
                fragmentView!!.account_tv_following_count.text =
                    followDTO?.followingCount.toString()
            }
    }


    fun getFollower() {

        followListenerRegistration = firestore?.collection("users")?.document(uid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                val followDTO = documentSnapshot?.toObject(FollowDTO::class.java)
                if (followDTO == null) return@addSnapshotListener
                fragmentView?.account_tv_follower_count?.text = followDTO?.followerCount.toString()
                if (followDTO?.followers?.containsKey(currentUserUid)!!) {

                    fragmentView?.account_btn_follow_signout?.text =
                        getString(R.string.follow_cancel)
                    fragmentView?.account_btn_follow_signout
                        ?.background
                        ?.setColorFilter(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.colorLightGray
                            ), PorterDuff.Mode.MULTIPLY
                        )
                } else {

                    if (uid != currentUserUid) {

                        fragmentView?.account_btn_follow_signout?.text = getString(R.string.follow)
                        fragmentView?.account_btn_follow_signout?.background?.colorFilter = null
                    }
                }

            }

    }

    fun getProfileImage() {
        firestore?.collection("profileImages")?.document(uid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener
                if (documentSnapshot.data != null) {
                    var url = documentSnapshot?.data!!["image"]
                    Glide.with(requireActivity()).load(url).apply(RequestOptions().circleCrop())
                        .into(fragmentView?.account_iv_profile!!)
                }
            }
    }

    inner class UserFragmentRecyclerViewAdapter(user_collection: String) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

        init {
            firestore?.collection(user_collection)?.whereEqualTo("uid", uid)
                ?.addSnapshotListener { value, error ->
                    // Sometimes, This code return null of value when it signout
                    if (value == null) return@addSnapshotListener

                    // Get data
                    for (snapshot in value.documents) {
                        data_document.add(snapshot.id)
                        contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                    }
                    // textView에 post 개수 설정
                    fragmentView?.account_tv_post_count?.text = contentDTOs.size.toString()
                    notifyDataSetChanged()

                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels / 3

            // 화면의 1/3 크기를 갖는 정사각형 imageView 생성
            var imageview = ImageView(parent.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            return CustomViewHolder(imageview)
        }

        inner class CustomViewHolder(var imageview: ImageView) :
            RecyclerView.ViewHolder(imageview) {}

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageview = (holder as CustomViewHolder).imageview

            // content 이미지 load
            Glide.with(holder.itemView.context).load(contentDTOs[position].imageUrl)
                .apply(RequestOptions().centerCrop()).into(imageview)

            holder.itemView.setOnClickListener {

                val nextIntent = Intent(fragmentView?.context, ContentDetailActivity::class.java)
                nextIntent.putExtra("imageUrl", contentDTOs[position].imageUrl)
                nextIntent.putExtra("explain", contentDTOs[position].explain)
                nextIntent.putExtra("favoriteCount", contentDTOs[position].favoriteCount)
                nextIntent.putExtra("contentUid", data_document.get(position))
                nextIntent.putExtra("destinationUid", contentDTOs[position].uid)
                if(fragmentView?.user_rb_public?.isChecked==true){
                    nextIntent.putExtra("selectOption", "images")
                }else{
                    nextIntent.putExtra("selectOption", "private")
                }
                startActivity(nextIntent)
            }
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

    }

    override fun onStop() {
        super.onStop()
        followListenerRegistration?.remove()
        followingListenerRegistration?.remove()
    }
}