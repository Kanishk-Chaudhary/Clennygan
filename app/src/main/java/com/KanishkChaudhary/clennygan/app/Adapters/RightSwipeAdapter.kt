package com.KanishkChaudhary.clennygan.app.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.KanishkChaudhary.clennygan.app.Activities.ChatActivity
import com.KanishkChaudhary.clennygan.app.Activities.UserInfoActivity
import com.KanishkChaudhary.clennygan.app.R
import com.KanishkChaudhary.clennygan.app.util.User
import com.bumptech.glide.Glide

class RightSwipeAdapter(private var usersList: ArrayList<User>): RecyclerView.Adapter<RightSwipeAdapter.RightSwipeViewHolder>() {

     fun addElement(user:User)
    {
         usersList.add(user)
         notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =

       RightSwipeAdapter.RightSwipeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_rightswipes, parent, false)
        )


    override fun onBindViewHolder(holder: RightSwipeViewHolder, position: Int) {
        holder.bind(usersList[position])
    }

    override fun getItemCount() = usersList.size



    class RightSwipeViewHolder(private val view:View):RecyclerView.ViewHolder(view)
    {

        private  var layout = view.findViewById<View>(R.id.rightLayout)
        private  var image = view.findViewById<ImageView>(R.id.rightPictureIV)
        private  var name = view.findViewById<TextView>(R.id.rightNameTV)


        fun bind(user:User)
        {
            name.text = user.name

            if(image != null)
            {
                if(user.imageUrl == "")
                {
                    Glide.with(view).load(R.drawable.default_pic).into(image)
                }
                else
                {
                    Glide.with(view).load(user.imageUrl).into(image)
                }
            }

            layout.setOnClickListener{
                val intent =  UserInfoActivity.newIntent(view.context,user.uid)
                view.context.startActivity(intent)
            }

        }




    }



}