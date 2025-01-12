package models

import firebase.companionPersona.enti24.R

data class Chat(val person: String, val lastMessage: String, val profileImageId:  Int = R.drawable.jack_frost, val userID: String) {
}
