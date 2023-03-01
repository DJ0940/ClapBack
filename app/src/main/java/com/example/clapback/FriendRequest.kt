package com.example.clapback

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FriendRequest : AppCompatActivity() {

    private lateinit var sendRequestBtn: Button
    private lateinit var usernameField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_request)

        usernameField = findViewById(R.id.search_user)
        sendRequestBtn = findViewById(R.id.send_request)

        // TODO placeholder user info
        val currentUser = User()

        lateinit var mDbRef: DatabaseReference

        // Allows user to send friend request
        sendRequestBtn.setOnClickListener {

            // Error checking for no user entered
            if (usernameField.text.toString().equals("")) {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mDbRef = FirebaseDatabase.getInstance().getReference()
            val searchedEmail = usernameField.text.toString()
            mDbRef.child("user").child(searchedEmail).get().addOnSuccessListener {
                if (it.exists()) {
                    val foundFriend = it.getValue(User::class.java)

                    // Error handler if getValue doesn't "populate" the foundFriend user object
                    // **For Debugging Purposes**
                    if (foundFriend == null) {

                        Toast.makeText(this, "**BIG ERROR HERE FIX**", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    // If friend request was already sent before
                    if (foundFriend?.friendRequests?.contains(FriendR(currentUser.uid, foundFriend.uid, true)) == true) {

                        Toast.makeText(this, "Friend request already sent", Toast.LENGTH_SHORT).show()

                    } else {

                        // Adding the request to both user's request list
                        foundFriend?.friendRequests?.add(FriendR(currentUser.uid, foundFriend.uid, true))
                        currentUser.friendRequests.add(FriendR(currentUser.uid, foundFriend?.uid, false))

                        // Uploading friend requests to database of both parties
                        foundFriend?.uid?.let { ffuid -> mDbRef.child(ffuid).child("friendRequests")
                            .setValue(foundFriend.friendRequests)}
                        currentUser.uid?.let { cuuid -> mDbRef.child(cuuid).child("friendRequests")
                            .setValue(currentUser.friendRequests)}

                    }
                }

            } .addOnFailureListener {
                // If user not found
                Toast.makeText(this, "User Not Found", Toast.LENGTH_SHORT).show()
            }





        }
    }
}