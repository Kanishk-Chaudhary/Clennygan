package com.KanishkChaudhary.clennygan.app.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.KanishkChaudhary.clennygan.app.Adapters.MessagesAdapter
import com.KanishkChaudhary.clennygan.app.R
import com.KanishkChaudhary.clennygan.app.util.DATA_CHATS
import com.KanishkChaudhary.clennygan.app.util.DATA_MESSAGES
import com.KanishkChaudhary.clennygan.app.util.Message
import com.KanishkChaudhary.clennygan.app.util.User
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {

    private var chatId: String? = null
    private var userId: String? = null
    private var imageUrl: String? = null
    private var otherUserId: String? = null

    private lateinit var chatDatabase: DatabaseReference
    private lateinit var messagesAdapter: MessagesAdapter

    private val chatMessagesListener = object : ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

            val message = snapshot.getValue(Message::class.java)
            if(message!=null)
            {
                messagesAdapter.addMessage(message)
                messagesRV.post{
                    messagesRV.smoothScrollToPosition(messagesAdapter.itemCount-1)
                }

            }

        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onChildRemoved(snapshot: DataSnapshot) {

        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onCancelled(error: DatabaseError) {

        }


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatId = intent.extras?.getString(PARAM_CHAT_ID,"")
        userId = intent.extras?.getString(PARAM_USER_ID,"")
        imageUrl = intent.extras?.getString(PARAM_IMAGE_URL,"")
        otherUserId = intent.extras?.getString(PARAM_OTHER_USER_ID,"")

        if(chatId.isNullOrEmpty()||userId.isNullOrEmpty()||otherUserId.isNullOrEmpty())
        {
            Toast.makeText(this,"Chat Error",Toast.LENGTH_SHORT).show()
        }

        chatDatabase = FirebaseDatabase.getInstance().reference.child(DATA_CHATS)
        messagesAdapter = MessagesAdapter(ArrayList(),userId!!)
        // hooking up chatadapter to our chatactivity
        messagesRV.apply{
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context)
            adapter = messagesAdapter

        }

        // addChildEventListener because everytime we add a new message we want to be here
        chatDatabase.child(chatId!!).child(DATA_MESSAGES).addChildEventListener(chatMessagesListener)

        chatDatabase.child(chatId!!).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                snapshot.children.forEach { value ->
                    val key = value.key
                    val user = value.getValue(User::class.java)
                    if(!key.equals(userId))
                    {
                        topNameTV.text = user!!.name

                        if(user?.imageUrl != "")
                        {
                            Glide.with(this@ChatActivity).load(user.imageUrl).into(topPhotoIV)
                        }
                        else
                        {
                            Glide.with(this@ChatActivity).load(R.drawable.default_pic).into(topPhotoIV)
                        }

                        topPhotoIV.setOnClickListener{
                            startActivity(UserInfoActivity.newIntent(this@ChatActivity,otherUserId))
                        }

                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }



    fun onSend(v:View)
    {
        val message = Message(userId,messageET.text.toString(),Calendar.getInstance().time.toString())
        val key = chatDatabase.child(chatId!!).child(DATA_MESSAGES).push().key
        if(!key.isNullOrEmpty())
        {
            chatDatabase.child(chatId!!).child(DATA_MESSAGES).child(key).setValue(message)
        }
        messageET.setText("",TextView.BufferType.EDITABLE)
    }

    companion object {
        private val PARAM_CHAT_ID = "Chat id"
        private val PARAM_USER_ID = "User id"
        private val PARAM_IMAGE_URL = "Image url"
        private val PARAM_OTHER_USER_ID = "Other user id"

        fun newIntent(context: Context?,chatId :String? ,userId : String?, imageUrl : String?, otherUserId :String?): Intent
        {
            val intent = Intent(context,ChatActivity::class.java)
            intent.putExtra(PARAM_CHAT_ID,chatId)
            intent.putExtra(PARAM_USER_ID,userId)
            intent.putExtra(PARAM_IMAGE_URL,imageUrl)
            intent.putExtra(PARAM_OTHER_USER_ID,otherUserId)

            return intent

        }

    }
}