package com.KanishkChaudhary.clennygan.app.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.KanishkChaudhary.clennygan.app.Activities.ClennyganCallback
import com.KanishkChaudhary.clennygan.app.R
import com.KanishkChaudhary.clennygan.app.util.*
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_signupactivity.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.emailET


class ProfileFragment : Fragment() {

    private lateinit var userId: String
    private lateinit var userDatabase: DatabaseReference
    private var callback: ClennyganCallback? = null



    fun setCallback(callback: ClennyganCallback)
    {
        this.callback = callback
        userId = callback.onGetUserId()
        userDatabase = callback.GetUserDatabase().child(userId)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        progressLayout.setOnTouchListener {view, event -> true} // stops the user to click on anything while the page is loading



        populateInfo()

        photoIV.setOnClickListener{ callback?.startActivityForPhoto()}

        applyButton.setOnClickListener{onApply()}
        signoutButton.setOnClickListener{callback?.onSignout()}


    }

    fun updateImageUri(uri:String)
    {
        userDatabase.child(DATA_IMAGE_URL).setValue(uri)
        populateImage(uri)

    }

    fun populateImage(uri:String)
    {
        Glide.with(this).load(uri).into(photoIV)
    }


    fun populateInfo()
    {
        userDatabase.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if(isAdded)
                {
                    val user = snapshot.getValue(User::class.java)
                    nameET.setText(user?.name,TextView.BufferType.EDITABLE)
                    emailET.setText(user?.email,TextView.BufferType.EDITABLE)
                    ageET.setText(user?.age,TextView.BufferType.EDITABLE)
                    heightET.setText(user?.height,TextView.BufferType.EDITABLE)
                    bioET.setText(user?.bio,TextView.BufferType.EDITABLE)
                    lastNameET.setText(user?.lastname,TextView.BufferType.EDITABLE)



                    if(user?.gender == GENDER_MALE)
                    {
                        radioMale1.isChecked = true
                    }
                    if(user?.gender == GENDER_FEMALE)
                    {
                        radioFemale1.isChecked = true
                    }

                    if(user?.preferredGender == GENDER_MALE)
                    {
                        radioMale2.isChecked = true
                    }
                    if(user?.preferredGender == GENDER_FEMALE)
                    {
                        radioFemale2.isChecked = true
                    }
                    if(!user?.imageUrl.isNullOrEmpty()) {
                        populateImage(user?.imageUrl!!)
                    }
                    progressLayout.visibility = View.GONE

                }

            }

            override fun onCancelled(error: DatabaseError) { //database error
                progressLayout.visibility = View.GONE

            }

        })


    }


    fun onApply()
    {
        if(nameET.text.toString().isNullOrEmpty() || emailET.text.toString().isNullOrEmpty()
            || ageET.text.toString().isNullOrEmpty() || radioGroup1.checkedRadioButtonId == -1
            || radioGroup2.checkedRadioButtonId == -1)
        {
            Toast.makeText(context,getString(R.string.error_profile_incomplete),Toast.LENGTH_SHORT).show()
        }
        else
        {
            val name = nameET.text.toString()
            val lastname = lastNameET.text.toString()
            val age = ageET.text.toString()
            val height = heightET.text.toString()
            val email = emailET.text.toString()
            val bio = bioET.text.toString()
            val gender =
                if(radioMale1.isChecked) GENDER_MALE
                else GENDER_FEMALE
            val preferredGender =
                if(radioMale2.isChecked) GENDER_MALE
                else GENDER_FEMALE

            userDatabase.child(DATA_NAME).setValue(name)
            userDatabase.child(DATA_LAST_NAME).setValue(lastname)
            userDatabase.child(DATA_AGE).setValue(age)
            userDatabase.child(DATA_EMAIL).setValue(email)
            userDatabase.child(DATA_GENDER).setValue(gender)
            userDatabase.child(DATA_GENDER_PREFERENCE).setValue(preferredGender)
            userDatabase.child(DATA_BIO).setValue(bio)
            userDatabase.child(DATA_HEIGHT).setValue(height)


            callback?.profileComplete()






        }


    }







}