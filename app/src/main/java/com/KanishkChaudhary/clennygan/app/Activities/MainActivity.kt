package com.KanishkChaudhary.clennygan.app.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.KanishkChaudhary.clennygan.app.Fragments.GotRightSwipedByFragment
import com.KanishkChaudhary.clennygan.app.Fragments.ProfileFragment
import com.KanishkChaudhary.clennygan.app.Fragments.matchesFragment
import com.KanishkChaudhary.clennygan.app.Fragments.swipeFragment
import com.KanishkChaudhary.clennygan.app.R
import com.KanishkChaudhary.clennygan.app.util.DATA_CHATS
import com.KanishkChaudhary.clennygan.app.util.DATA_USERS
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.IOException

const val REQUEST_CODE_PHOTO = 9099

class MainActivity : AppCompatActivity(),ClennyganCallback {

    private var firebaseAuth = FirebaseAuth.getInstance()
    private var userId = firebaseAuth.currentUser?.uid
    private lateinit var userDatabase: DatabaseReference
    private lateinit var chatDatabase: DatabaseReference




    private var profileFragment: ProfileFragment? = null
    private var swipeFragment: swipeFragment?= null
    private var rightSwipedFragment : GotRightSwipedByFragment? = null
    private var matchesFragment: matchesFragment?=null

    private var profileTab: TabLayout.Tab? = null
    private var swipeTab: TabLayout.Tab? = null
    private var rightSwipesTab: TabLayout.Tab? = null
    private var matchesTab: TabLayout.Tab?= null

    private var resultImageUrl: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(userId.isNullOrEmpty())
        {
            onSignout()
        }

        userDatabase = FirebaseDatabase.getInstance().reference.child(DATA_USERS)
        chatDatabase = FirebaseDatabase.getInstance().reference.child(DATA_CHATS)

        profileTab = navigationTabs.newTab()
        swipeTab = navigationTabs.newTab()
        rightSwipesTab = navigationTabs.newTab()
        matchesTab = navigationTabs.newTab()

        profileTab?.icon = ContextCompat.getDrawable(this,R.drawable.tab_profile)
        swipeTab?.icon = ContextCompat.getDrawable(this,R.drawable.tab_swipe)
        rightSwipesTab?.icon = ContextCompat.getDrawable(this,R.drawable.star_main)
        matchesTab?.icon = ContextCompat.getDrawable(this,R.drawable.tab_matches)

        navigationTabs.addTab(profileTab!!)
        navigationTabs.addTab(swipeTab!!)
        navigationTabs.addTab(rightSwipesTab!!)
        navigationTabs.addTab(matchesTab!!)

        navigationTabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

                when(tab)
                {
                    profileTab -> {

                        if(profileFragment == null)
                        {
                            profileFragment = ProfileFragment()
                            profileFragment!!.setCallback(this@MainActivity)
                        }
                        replaceFragment(profileFragment!!) // in this case fragment container is gonna have profile fragment

                    }

                    swipeTab -> {
                        if(swipeFragment == null)
                        {
                            swipeFragment = swipeFragment()
                            swipeFragment!!.setCallback(this@MainActivity)
                        }
                        replaceFragment(swipeFragment!!)
                    }

                    rightSwipesTab -> {

                        if(rightSwipedFragment == null)
                        {
                            rightSwipedFragment = GotRightSwipedByFragment()
                           rightSwipedFragment!!.setCallback(this@MainActivity)
                        }
                        replaceFragment(rightSwipedFragment!!) // in this case fragment container is gonna have profile fragment

                    }

                    matchesTab -> {
                        if(matchesFragment == null)
                        {
                            matchesFragment = matchesFragment()
                            matchesFragment!!.setCallback(this@MainActivity)
                        }
                        replaceFragment(matchesFragment!!)
                    }
                }


            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

               onTabSelected(tab)
            }

        })

        profileTab?.select()

    }

    fun replaceFragment (fragment: Fragment)
    {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer,fragment)
        transaction.commit()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {// gets called after startactivityforresult
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO)
        {
            resultImageUrl = data?.data
            storeImage()
        }

    }

    fun storeImage() {
        if(resultImageUrl != null && userId != null) {
            val filePath = FirebaseStorage.getInstance().reference.child("profileImage").child(userId!!)
            var bitmap: Bitmap? = null
            try {
                if (android.os.Build.VERSION.SDK_INT >= 29){
                    // To handle deprecation use
                    val source = ImageDecoder.createSource(contentResolver,resultImageUrl!!)
                    bitmap = ImageDecoder.decodeBitmap(source)
                } else {
                    // Use older version
                    bitmap = MediaStore.Images.Media.getBitmap(application.contentResolver, resultImageUrl!!)

                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG,20,baos)
        val data = baos.toByteArray()

            val uploadTask = filePath.putBytes(data)
            uploadTask.addOnFailureListener{e -> e.printStackTrace()}
            uploadTask.addOnSuccessListener {taskSnapShot -> filePath.downloadUrl
                .addOnSuccessListener { uri -> profileFragment?.updateImageUri(uri.toString()) } }
                .addOnFailureListener{ e -> e.printStackTrace()}

        }


    }


    override fun onSignout() {

        firebaseAuth.signOut()
        startActivity(StartupActivity.newIntent(this))
        finish()

    }


    override fun onGetUserId(): String = userId!!


    override fun GetUserDatabase(): DatabaseReference = userDatabase

    override fun getChatDatabase(): DatabaseReference = chatDatabase

    override fun userInfoMatchedUp() {

        matchesTab?.select()
    }

    override fun profileComplete() {
        swipeTab?.select()
    }


    override fun startActivityForPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,REQUEST_CODE_PHOTO)
    }


    companion object {

        fun newIntent(context: Context?) = Intent(context,MainActivity::class.java)
    }


}