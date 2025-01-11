package firebase.companionPersona.enti24

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User
import models.Chat

class ChatFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatRecyclerViewManager: RecyclerView.LayoutManager
    private var chatList: MutableList<Chat> = mutableListOf()



    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val databaseURL = "https://p3rcompanion-default-rtdb.europe-west1.firebasedatabase.app/"
        database = FirebaseDatabase.getInstance(databaseURL).getReference()

        //variables buiscar usuario
        val searchEditText = view.findViewById<EditText>(R.id.UserSearchText)
        val searchButton = view.findViewById<ImageButton>(R.id.UserSearchSend)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewChats)

        val layoutManager = RecyclerView.LayoutManager::class.java.cast(
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        )
        recyclerView.layoutManager = layoutManager

        chatAdapter = ChatAdapter(chatList)
        recyclerView.adapter = chatAdapter

        searchButton.setOnClickListener{
            val publicID = searchEditText.text.toString().trim()
            if (publicID.isNotEmpty()) {
                searchUserByPublicId(publicID)
            }
        }
    }

    private fun searchUserByPublicId(publicId: String) {
        database.child("users").orderByChild("public_id").equalTo(publicId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            // Recuperar los datos directamente desde el snapshot
                            val userName = userSnapshot.child("name").value as? String ?: "Usuario desconocido"
                            val userId = userSnapshot.child("id").value as? String ?: ""

                            // Abrir un chat con el usuario encontrado
                            openChatWithUser(userId, userName)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private  fun openChatWithUser(userID: String, userName: String){

        val newChat = Chat(person = userName, lastMessage = "Sin mensajes", profileImageId = R.drawable.jack_frost)

        if (chatList.none { it.person == userName }) {
            chatList.add(newChat)
            chatAdapter.notifyItemInserted(chatList.size - 1)
        }

        val intent = Intent(requireContext(), InternalChatUsers::class.java)
        intent.putExtra("userId", userID)
        intent.putExtra("userName", userName)
        startActivity(intent)
    }

}