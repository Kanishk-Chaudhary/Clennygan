package com.KanishkChaudhary.clennygan.app.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.KanishkChaudhary.clennygan.app.Adapters.ChatsAdapter
import com.KanishkChaudhary.clennygan.app.R
import com.KanishkChaudhary.clennygan.app.util.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_user_info.*

class UserInfoActivity : AppCompatActivity() {

    private var currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    private var userDatabase = FirebaseDatabase.getInstance().reference.child(DATA_USERS)
    private var chatDatabase = FirebaseDatabase.getInstance().reference.child(DATA_CHATS)
    private var callback: ClennyganCallback? = null
    private var matchExist = false



    private var chatsAdapter = ChatsAdapter(ArrayList())






    fun setCallback(callback: ClennyganCallback)
    {
        this.callback = callback


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        var currentUser : User? = null

        val userId = intent.extras?.getString(PARAM_USER_ID,"")
        if(userId.isNullOrEmpty())
        {
            finish()
        }



        userDatabase.child(userId!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)




                userDatabase.child(userId!!).child(DATA_SWIPES_RIGHT_ON)
                    .addListenerForSingleValueEvent(object : ValueEventListener {

                        override fun onDataChange(snapshot: DataSnapshot) {

                            userDatabase.child(currentUserId!!).child(DATA_MATCHES).addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.hasChildren()) {

                                        snapshot.children.forEach { child ->

                                            var selectedUserId = child.key

                                            if (selectedUserId.equals(userId)) {

                                                matchExist = true
                                            }
                                        }




                                    }

                                }

                                override fun onCancelled(error: DatabaseError) {

                                }


                            })


                            if (snapshot.hasChildren()) {
                                var rightSwipeExist = false

                                snapshot.children.forEach { child ->

                                    var selectedUserId = child.key

                                    if (selectedUserId.equals(currentUserId)) {

                                        rightSwipeExist = true
                                    }
                                }

                                if (!rightSwipeExist || matchExist) {

                                    matchUpButton.setEnabled(false)
                                    matchUpButton.setClickable(false)

                                } else {
                                    matchUpButton.setEnabled(true)
                                    matchUpButton.setClickable(true)
                                }


                            } else {
                                matchUpButton.setEnabled(false)
                                matchUpButton.setClickable(false)
                            }





                        }



                        override fun onCancelled(error: DatabaseError) {

                        }


                    })



                userInfoName.text = "${user?.name}  ${user?.lastname}"
                userInfoAge.text = user?.age
                userInfoHeight.text = user?.height
                userInfoBio.text = user?.bio

                if (user?.imageUrl != null) {
                    if (user?.imageUrl == "") {
                        Glide.with(this@UserInfoActivity).load(R.drawable.default_pic)
                            .into(userInfoIV)
                    } else {
                        Glide.with(this@UserInfoActivity).load(user.imageUrl).into(userInfoIV)
                    }
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    fun onMatchUp(v: View)
    {

       Toast.makeText(this@UserInfoActivity, "Match Found!!!", Toast.LENGTH_SHORT).show()





        val userId = intent.extras?.getString(PARAM_USER_ID,"")
        if(userId.isNullOrEmpty())
        {
            finish()
        }



        userDatabase.child(userId!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val user = snapshot.getValue(User::class.java)


                userDatabase.child(currentUserId!!).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val currentUser = snapshot.getValue(User::class.java) // this is we


                        userDatabase.child(userId!!).child(DATA_SWIPES_RIGHT_ON).addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.hasChildren())
                                {

                                    snapshot.children.forEach { child ->

                                        val selectedUserId = child.key

                                        if(selectedUserId.equals(currentUser?.uid))
                                        {

                                            val chatKey = chatDatabase.push().key

                                            userDatabase.child(userId).child(DATA_SWIPES_RIGHT_ON).child(currentUserId!!).removeValue()
                                            userDatabase.child(currentUserId!!).child(DATA_SWIPES_RIGHT).child(userId).removeValue()

                                            userDatabase.child(currentUserId!!).child(DATA_MATCHES)
                                                .child(userId).setValue(chatKey)

                                            userDatabase.child(userId).child(DATA_MATCHES)
                                                .child(currentUserId!!).setValue(chatKey)

                                            chatDatabase.child(chatKey!!).child(currentUserId!!).child(DATA_NAME)
                                                .setValue(currentUser?.name)
                                            chatDatabase.child(chatKey!!).child(currentUserId!!).child(DATA_IMAGE_URL)
                                                .setValue(currentUser?.imageUrl)

                                            chatDatabase.child(chatKey).child(userId).child(DATA_NAME)
                                                .setValue(user?.name)
                                            chatDatabase.child(chatKey).child(userId).child(DATA_IMAGE_URL)
                                                .setValue(user?.imageUrl)



                                        }
                                    }


                                }



                            }

                            override fun onCancelled(error: DatabaseError) {

                            }


                        })





                    }

                    override fun onCancelled(error: DatabaseError) {

                    }


                })

//                userInfoName.text = "${user?.name} + ${user?.lastname}"
//                userInfoAge.text = user?.age
//                userInfoHeight.text = user?.height
//                userInfoBio.text = user?.bio
//
//                if(user?.imageUrl != null)
//                {
//                    if(user?.imageUrl == "")
//                    {
//                        Glide.with(this@UserInfoActivity).load(R.drawable.default_pic).into(userInfoIV)
//                    }
//                    else
//                    {
//                        Glide.with(this@UserInfoActivity).load(user.imageUrl).into(userInfoIV)
//                    }
//                }



                userDatabase.child(currentUserId!!).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                       val currentUser = snapshot.getValue(User::class.java)
                        startActivity(MatchActivity.newIntent(this@UserInfoActivity,currentUserId!!,userId,user?.imageUrl,currentUser?.imageUrl))

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })



    //matchActivity




    }






   companion object {
      private val PARAM_USER_ID = "User  id"

       fun newIntent(context: Context,userId: String?): Intent {
           val intent = Intent(context,UserInfoActivity::class.java)
           intent.putExtra(PARAM_USER_ID,userId)
           return intent
       }
   }

}