package com.KanishkChaudhary.clennygan.app.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.KanishkChaudhary.clennygan.app.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_match.*

class MatchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)

        val currentUserImageUrl = intent.extras?.getString(PARAM_IMAGE_URL)
        val otherUserImageUrl = intent.extras?.getString(PARAM_OTHER_IMAGE_URL)

        if(currentUserImageUrl == "")
        {
            Glide.with(this).load(R.drawable.default_pic).into(currentUserImage)
        }
        else
        {
            Glide.with(this).load(currentUserImageUrl).into(currentUserImage)
        }



        if(otherUserImageUrl == "")
        {
            Glide.with(this@MatchActivity).load(R.drawable.default_pic).into(otherUserImage)
        }
        else
        {
            Glide.with(this@MatchActivity).load(otherUserImageUrl).into(otherUserImage)
        }




    }



    fun onYayyy(v:View)
    {
        startActivity(MainActivity.newIntent(this))
    }



    companion object {
        private val PARAM_OTHER_IMAGE_URL = "Other image url"
        private val PARAM_USER_ID = "User id"
        private val PARAM_IMAGE_URL = "Image url"
        private val PARAM_OTHER_USER_ID = "Other user id"

        fun newIntent(context: Context?, userId :String?, otherUserId : String?, imageUrl : String?, otherImageUrl :String?): Intent
        {
            val intent = Intent(context,MatchActivity::class.java)

            intent.putExtra(PARAM_USER_ID,userId)
            intent.putExtra(PARAM_OTHER_USER_ID,otherUserId)
            intent.putExtra(PARAM_IMAGE_URL,imageUrl)
            intent.putExtra(PARAM_OTHER_IMAGE_URL,otherImageUrl)


            return intent

        }

    }


}