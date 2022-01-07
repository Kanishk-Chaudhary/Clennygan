package com.KanishkChaudhary.clennygan.app.Adapters

import android.content.Context
import android.content.LocusId
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.KanishkChaudhary.clennygan.app.Activities.UserInfoActivity
import com.KanishkChaudhary.clennygan.app.R
import com.KanishkChaudhary.clennygan.app.util.User
import com.bumptech.glide.Glide

class CardsAdapter(context: Context? ,resourceId: Int,users:List<User>): ArrayAdapter<User>(context!!,resourceId,users) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var user = getItem(position)
        var finalView = convertView ?: LayoutInflater.from(context).inflate(R.layout.item,parent,false)

        var name = finalView.findViewById<TextView>(R.id.nameTV)
        var image = finalView.findViewById<ImageView>(R.id.photoIV)

        var userInfo = finalView.findViewById<LinearLayout>(R.id.userInfoLayout)



        name.text = "${user?.name},${user?.age}"

        if(user?.imageUrl == "")
        {
            Glide.with(context).load(R.drawable.default_pic).into(image)
        }
        else
        {
            Glide.with(context).load(user?.imageUrl).into(image)
        }
        userInfo.setOnClickListener{
            finalView.context.startActivity(UserInfoActivity.newIntent(finalView.context,user?.uid))
        }

        return finalView

    }

}