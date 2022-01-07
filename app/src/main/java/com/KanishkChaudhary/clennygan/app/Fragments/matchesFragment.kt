package com.KanishkChaudhary.clennygan.app.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.KanishkChaudhary.clennygan.app.Activities.ClennyganCallback
import com.KanishkChaudhary.clennygan.app.Adapters.ChatsAdapter
import com.KanishkChaudhary.clennygan.app.R
import com.KanishkChaudhary.clennygan.app.util.Chat
import com.KanishkChaudhary.clennygan.app.util.DATA_MATCHES
import com.KanishkChaudhary.clennygan.app.util.User
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_got_right_swiped_by.*
import kotlinx.android.synthetic.main.fragment_matches.*


class matchesFragment : Fragment() {

    private lateinit var userId : String
    private lateinit var userDatabase: DatabaseReference
    private lateinit var chatDatabase: DatabaseReference
    private var chatsAdapter = ChatsAdapter(ArrayList())

    private var callback: ClennyganCallback? = null



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
        // Inflate the layout for this fragment
        fetchData()
        return inflater.inflate(R.layout.fragment_matches, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        matchesRV.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context)
            adapter = chatsAdapter
        }



        userDatabase.child(userId).child(DATA_MATCHES).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChildren())
                {

                }
                else
                {
                    noMatchesLayout.visibility = View.VISIBLE
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
        super.onViewCreated(view, savedInstanceState)
    }

    fun fetchData()
    {


       chatsAdapter = ChatsAdapter(ArrayList())

        userDatabase.child(userId).child(DATA_MATCHES).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) { // given us the list of matches

                if(snapshot.hasChildren())
                {
                    snapshot.children.forEach { child ->
                        val matchId = child.key // other user's id
                        val chatId = child.value.toString()
                        if(!matchId.isNullOrEmpty())
                        {
                            userDatabase.child(matchId).addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    val user = snapshot.getValue(User::class.java)
                                    if(user != null)
                                    {
                                        val chat = Chat(userId,chatId,user.uid,user.name,user.imageUrl)
                                        chatsAdapter.addElement(chat)
                                    }

                                }

                                override fun onCancelled(error: DatabaseError) {

                                }

                            })

                        }
                    }

                }





            }

            override fun onCancelled(error: DatabaseError) {

            }

        })



    }


}