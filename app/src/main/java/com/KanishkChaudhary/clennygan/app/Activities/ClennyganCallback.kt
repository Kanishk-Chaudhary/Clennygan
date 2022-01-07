package com.KanishkChaudhary.clennygan.app.Activities

import com.google.firebase.database.DatabaseReference

interface ClennyganCallback {

    fun onSignout()

    fun onGetUserId(): String

    fun GetUserDatabase(): DatabaseReference

    fun profileComplete()

    fun startActivityForPhoto()

    fun userInfoMatchedUp()

    fun getChatDatabase(): DatabaseReference


}