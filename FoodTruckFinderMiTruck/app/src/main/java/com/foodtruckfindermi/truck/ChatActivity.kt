package com.foodtruckfindermi.truck

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.foodtruckfindermi.truck.Adapters.MessagesAdapter
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class ChatActivity : AppCompatActivity() {
    lateinit var chatListView : ListView
    lateinit var db: FirebaseFirestore
    lateinit var groupID: String
    lateinit var userDoc : DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val chatTitle = findViewById<TextView>(R.id.chatTitle)
        val backButton = findViewById<ImageButton>(R.id.chatBackButton)
        chatListView = findViewById<ListView>(R.id.chatListView)
        val messageEntry = findViewById<EditText>(R.id.chatMessageEntry)
        val sendButton = findViewById<Button>(R.id.sendButton)



        db = Firebase.firestore
        val email = intent.getStringExtra("email")!!
        groupID = intent.getStringExtra("groupID")!!
        userDoc = db.collection("Users").document(email)


        backButton.setOnClickListener {
            onBackPressed()
        }

        sendButton.setOnClickListener {
            runBlocking {
                if (messageEntry.text.toString() != "") {
                    val data = getDocInfo(db.collection("Groups").document(groupID))
                    val messagesCache = data["messages"] as ArrayList<String>
                    val sendersCache = data["senders"] as ArrayList<DocumentReference>

                    messagesCache.add(messageEntry.text.toString())
                    sendersCache.add(userDoc)

                    db.collection("Groups").document(groupID).set(mapOf("messages" to messagesCache, "senders" to sendersCache))
                    messageEntry.text.clear()

                    refreshList()
                }
            }

        }

        runBlocking {
            val docRef = db.collection("Groups").document(groupID)
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {

                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    refreshList()
                }
            }
        }



        refreshList()




    }


    private suspend fun getDocInfo(doc: DocumentReference): DocumentSnapshot {
        return doc.get().await()
    }

    private fun refreshList() {
        val groupDoc = db.collection("Groups").document(groupID)
        //

        runBlocking {
            val docData = getDocInfo(groupDoc)



            val MessagesAdapter = MessagesAdapter(this@ChatActivity,
                docData["messages"] as ArrayList<String>,
                docData["senders"] as ArrayList<DocumentReference>,
                userDoc
            )

            chatListView.adapter = MessagesAdapter
        }
    }
}