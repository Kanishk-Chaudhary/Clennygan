package com.KanishkChaudhary.clennygan.app.Activities

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.KanishkChaudhary.clennygan.app.R
import com.KanishkChaudhary.clennygan.app.util.DATA_USERS
import com.KanishkChaudhary.clennygan.app.util.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login2.emailET
import kotlinx.android.synthetic.main.activity_login2.passwordET
import kotlinx.android.synthetic.main.activity_signupactivity.*
import android.os.Bundle as Bundle1

class Signupactivity : AppCompatActivity() {

    private val firebaseDatabase = FirebaseDatabase.getInstance().reference
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user = firebaseAuth.currentUser
        if(user!=null) {
            startActivity(MainActivity.newIntent(this))
            finish()
        }
    }


    override fun onCreate(savedInstanceState: Bundle1?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signupactivity)
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }

    fun onSignup(v:View){

        if(!emailET.text.toString().isNullOrEmpty()&&!passwordET.text.toString().isNullOrEmpty()&&!firstnameET.text.toString().isNullOrEmpty()&&!lastnameET.text.toString().isNullOrEmpty()&&!confirmpasswordET.text.toString().isNullOrEmpty()) {

            if (passwordET.text.toString().equals(confirmpasswordET.text.toString()))
            {
                firebaseAuth.createUserWithEmailAndPassword(
                    emailET.text.toString(),
                    passwordET.text.toString()
                ).addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Signup Error ${task.exception?.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // add the user to the database
                        val firstName = firstnameET.text.toString()
                        val lastname = lastnameET.text.toString()
                        val email = emailET.text.toString()
                        val userId = firebaseAuth.currentUser?.uid ?: ""
                        val user = User(userId, firstName, lastname, "", email ,"", "","","")
                        firebaseDatabase.child(DATA_USERS).child(userId).setValue(user)

                    }
                }

            }
            else
            {
                Toast.makeText(this,"Password don't match",Toast.LENGTH_SHORT).show()
            }
        }

    }
    companion object {

        fun newIntent(context: Context?) = Intent(context,Signupactivity::class.java)
    }

}