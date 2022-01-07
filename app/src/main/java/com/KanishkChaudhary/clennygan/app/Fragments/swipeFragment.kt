package com.KanishkChaudhary.clennygan.app.Fragments

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.KanishkChaudhary.clennygan.app.Activities.ClennyganCallback
import com.KanishkChaudhary.clennygan.app.Activities.MatchActivity
import com.KanishkChaudhary.clennygan.app.Adapters.CardsAdapter
import com.KanishkChaudhary.clennygan.app.R
import com.KanishkChaudhary.clennygan.app.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import kotlinx.android.synthetic.main.fragment_swipe.*


class swipeFragment : Fragment() {

    private var callback: ClennyganCallback? = null
    private lateinit var userId: String
    private lateinit var userDatabase: DatabaseReference
    private lateinit var chatDatabase: DatabaseReference
    private var cardsAdapter: ArrayAdapter<User>? = null
    private var rowItems = ArrayList<User>()
    private var preferredGender: String? = null
    private var userName :String? = null
    private var imgUrl :String? = null





    fun setCallback(callback: ClennyganCallback)
    {
        this.callback = callback
        userId = callback.onGetUserId()
        userDatabase = callback.GetUserDatabase()
        chatDatabase = callback.getChatDatabase()
    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rowItems = ArrayList<User>()
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_swipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


            userDatabase.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val user = snapshot.getValue(User :: class.java)
                preferredGender = user?.preferredGender
                userName = user?.name
                imgUrl = user?.imageUrl
                if(preferredGender == "" || user?.gender == "" || user?.age == "" || user?.imageUrl == "")
                {
                    profileNotCompletedLayout.visibility = View.VISIBLE
                    progressLayout.visibility = View.GONE
                }
                else
                {
                    populateItems()

//                   val layout = view.findViewById<LinearLayout>(R.id.noUsersLayout)
//                   if(layout!=null) {
//                       noUsersLayout.visibility = View.GONE
//                   }


                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })


        cardsAdapter = CardsAdapter(context, R.layout.item , rowItems)

        frame.adapter = cardsAdapter
        frame.setFlingListener(object : SwipeFlingAdapterView.onFlingListener{
            override fun removeFirstObjectInAdapter() {
                rowItems.removeAt(0)
                cardsAdapter?.notifyDataSetChanged()

            }

            override fun onLeftCardExit(p0: Any?) {

                var user = p0 as User
                userDatabase.child(user.uid.toString()).child(DATA_SWIPES_LEFT).child(userId).setValue(true)
                userDatabase.child(userId).child(DATA_SWIPES_LEFT_ON).child(user.uid.toString()).setValue(true)

            }

            override fun onRightCardExit(p0: Any?) {

                val selectedUser = p0 as User
                val selectedUserId = selectedUser.uid

                if(!selectedUserId.isNullOrEmpty())
                {
                    userDatabase.child(userId).child(DATA_SWIPES_RIGHT).addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) { // snapshot is us

                            if(snapshot.hasChild(selectedUserId))
                            {
                                Toast.makeText(context, "Match Found!!!",Toast.LENGTH_SHORT).show()

                                val chatKey = chatDatabase.push().key

                                if(chatKey != null)
                                {

                                    userDatabase.child(userId).child(DATA_SWIPES_RIGHT)
                                        .child(selectedUserId).removeValue()

                                    userDatabase.child(selectedUserId).child(DATA_SWIPES_RIGHT_ON)
                                        .child(userId).removeValue()

                                    userDatabase.child(userId).child(DATA_MATCHES)
                                        .child(selectedUserId).setValue(chatKey)

                                    userDatabase.child(selectedUserId).child(DATA_MATCHES)
                                        .child(userId).setValue(chatKey)

                                    chatDatabase.child(chatKey).child(userId).child(DATA_NAME)
                                        .setValue(userName)
                                    chatDatabase.child(chatKey).child(userId).child(DATA_IMAGE_URL)
                                        .setValue(imgUrl)

                                    chatDatabase.child(chatKey).child(selectedUserId).child(DATA_NAME)
                                        .setValue(selectedUser.name)
                                    chatDatabase.child(chatKey).child(selectedUserId).child(DATA_IMAGE_URL)
                                        .setValue(selectedUser.imageUrl)

                                }

                                userDatabase.child(userId).addListenerForSingleValueEvent(object : ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val currentuser = snapshot.getValue(User::class.java)

                                        startActivity(MatchActivity.newIntent(activity ,userId,selectedUserId,selectedUser.imageUrl,currentuser?.imageUrl))



                                    }

                                    override fun onCancelled(error: DatabaseError) {

                                    }

                                })



                                //Match Activity
                            }
                            else
                            {

                                userDatabase.child(selectedUserId).child(DATA_SWIPES_RIGHT).child(userId).setValue(true)
                                userDatabase.child(userId).child(DATA_SWIPES_RIGHT_ON).child(selectedUserId).setValue(true)

                            }

                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })
                }

            }


            override fun onAdapterAboutToEmpty(p0: Int) {


                if(progressLayout.visibility != View.VISIBLE && profileNotCompletedLayout.visibility != View.VISIBLE)
                {

                    noUsersLayout.visibility = View.VISIBLE
                }



            }

            override fun onScroll(p0: Float) {

            }
        })

        likeButton.setOnClickListener{
            if(!rowItems.isEmpty())
            {
                frame.topCardListener.selectRight()
            }
        }

        dislikeButton.setOnClickListener{
            if(!rowItems.isEmpty())
            {
                frame.topCardListener.selectLeft()
            }
        }



        frame.setOnItemClickListener{ position , data ->}





    }






    fun populateItems()
    {





        val cardsQuery = userDatabase.orderByChild(DATA_GENDER).equalTo(preferredGender)
        cardsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

               snapshot.children.forEach { child ->
                   val user = child.getValue(User::class.java)
                   if(user!=null)
                   {
                       var showUser = true
                       if(child.child(DATA_SWIPES_LEFT).hasChild(userId) ||
                               child.child(DATA_SWIPES_RIGHT).hasChild(userId) ||
                                child.child(DATA_MATCHES).hasChild(userId)||
                           child.child(DATA_IMAGE_URL).equals("")
                               ){

                           showUser = false
                       }

                       if(showUser)
                       {
                           rowItems.add(user)
                           cardsAdapter?.notifyDataSetChanged()
                       }


                   }

                }

                progressLayout.visibility = View.GONE

                if(rowItems.isEmpty())
                {

                    progressLayout.visibility = View.GONE
                    noUsersLayout.visibility = View.VISIBLE

                }

            }

            override fun onCancelled(error: DatabaseError) {


            }


        })

    }


   
}