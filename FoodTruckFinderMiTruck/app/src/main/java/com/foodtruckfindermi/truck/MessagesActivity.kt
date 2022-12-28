package com.foodtruckfindermi.truck

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class MessagesActivity : AppCompatActivity() {

    lateinit var userDoc: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        val db = Firebase.firestore
        val email = intent.getStringExtra("email")

        val backButton = findViewById<ImageButton>(R.id.messagesBackButton)
        val groupsListView = findViewById<ListView>(R.id.messagesList)

        userDoc = db.collection("Users").document(email!!)
        val groupsArray = mutableListOf<String>()
        val groupIDArray = mutableListOf<String>()

        backButton.setOnClickListener {
            onBackPressed()
        }

        runBlocking {


            val document = getListOfGroups()

            for (i in document.data!!["groups"] as ArrayList<DocumentReference>) {
                val groupDoc = getDocData(i)


                var participants = groupDoc["participants"] as ArrayList<DocumentReference>
                participants.remove(userDoc)
                Log.i("Participants List", participants.toString())
                val groupName = getDocData(participants[0]).id








                groupsArray.add(groupName)
                groupIDArray.add(groupDoc.id)
                Log.i("GroupDocs", groupDoc.id)



            }
            Log.i("Groups", groupsArray.toString())

            val groupsAdapter =
                ArrayAdapter(this@MessagesActivity, android.R.layout.simple_list_item_1, groupsArray)
            groupsListView.adapter = groupsAdapter

            groupsListView.setOnItemClickListener { _, _, position, _ ->
                val intent = Intent(this@MessagesActivity, ChatActivity::class.java)
                intent.putExtra("groupID", groupIDArray[position])
                intent.putExtra("email", email)
                startActivity(intent)
            }

        }



    }



    private suspend fun getListOfGroups(): DocumentSnapshot {
        val snapshot = userDoc.get().await()
        return snapshot
    }

    private suspend fun getDocData(doc: DocumentReference): DocumentSnapshot {
        val snapshot = doc.get().await()
        return snapshot
    }
}