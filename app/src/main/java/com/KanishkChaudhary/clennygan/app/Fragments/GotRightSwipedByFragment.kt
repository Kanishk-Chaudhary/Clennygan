package com.KanishkChaudhary.clennygan.app.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.KanishkChaudhary.clennygan.app.Activities.ClennyganCallback
import com.KanishkChaudhary.clennygan.app.Adapters.RightSwipeAdapter
import com.KanishkChaudhary.clennygan.app.R
import com.KanishkChaudhary.clennygan.app.util.DATA_MATCHES
import com.KanishkChaudhary.clennygan.app.util.DATA_SWIPES_RIGHT
import com.KanishkChaudhary.clennygan.app.util.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_got_right_swiped_by.*
import kotlinx.android.synthetic.main.fragment_matches.*


class GotRightSwipedByFragment : Fragment() {

    private lateinit var userId: String
    private lateinit var userDatabase: DatabaseReference
    private var callback: ClennyganCallback? = null

    private var rightSwipesAdapter = RightSwipeAdapter(ArrayList())

    fun setCallback(callback: ClennyganCallback)
    {
        this.callback = callback
        userId = callback.onGetUserId()
        userDatabase = callback.GetUserDatabase()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fetchData()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_got_right_swiped_by, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        rightSwipesRV.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context)
            adapter = rightSwipesAdapter
        }

        userDatabase.child(userId).child(DATA_SWIPES_RIGHT).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChildren())
                {

                }
                else
                {
                    noRightSwipesLayout.visibility = View.VISIBLE
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }



    fun fetchData()
    {
        rightSwipesAdapter = RightSwipeAdapter(ArrayList())


        userDatabase.child(userId).child(DATA_SWIPES_RIGHT).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.hasChildren())
                {
                    snapshot.children.forEach { child ->

                        val childId = child.key

                        if(!childId.isNullOrEmpty())
                        {
                            userDatabase.child(childId).addListenerForSingleValueEvent(object :ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    val user = snapshot.getValue(User::class.java)
                                    if(user!=null)
                                    {
                                        val user = User(user.uid,user.name,user.age,user.email,user.gender,user.preferredGender,user.imageUrl,user.bio,user.height)
                                        rightSwipesAdapter.addElement(user)
                                    }

                                }

                                override fun onCancelled(error: DatabaseError) {

                                }


                            })

                        }


                    }

                }
                else
                {

                    noRightSwipesLayout.visibility = View.VISIBLE

                }



            }

            override fun onCancelled(error: DatabaseError) {

            }


        })




    }




    companion object {


    }
}