package com.KanishkChaudhary.clennygan.app.util

data class User(

    val uid: String? ="",
    val name: String? ="",
    val lastname :String?="",
    val age: String? ="",
    val email: String? ="",
    val gender: String? ="",
    val preferredGender: String? ="",
    val imageUrl: String? ="",
    val bio: String? ="",
    val height:String?=""


)

data class Chat(
    val userId: String? = "",
    val chatId : String? = "",
    val otherUserId : String? = "",
    val name : String? = "",
    val imageUrl: String? = ""
)


data class Message(
    val sentBy: String? = null,
    val message :String? = null,
    val time :String? = null

)