package firebase.companionPersona.enti24

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.bumptech.glide.Glide
import models.Chat

class ChatFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var chatAdapter: ChatAdapter
    private var chatList: MutableList<Chat> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val databaseURL = "https://p3rcompanion-default-rtdb.europe-west1.firebasedatabase.app/"
        database = FirebaseDatabase.getInstance(databaseURL).getReference()

        // Cargar los chats desde SharedPreferences o Firebase
        loadChatsFromSharedPreferences()

        // Variables para buscar usuario
        val searchEditText = view.findViewById<EditText>(R.id.UserSearchText)
        val searchButton = view.findViewById<ImageButton>(R.id.UserSearchSend)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewChats)

        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        // Inicialización del adaptador con la función onChatClick
        chatAdapter = ChatAdapter(chatList) { chat ->
            openChatWithUser(chat.userID, chat.person)
        }
        recyclerView.adapter = chatAdapter

        searchButton.setOnClickListener {
            val publicID = searchEditText.text.toString().trim()
            if (publicID.isNotEmpty()) {
                searchUserByPublicId(publicID)
            }
        }
    }

    // Buscar usuario por ID público
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

    // Abrir un chat con un usuario
    private fun openChatWithUser(userID: String, userName: String) {
        // Verificar si el chat ya existe en la lista
        if (chatList.none { it.person == userName }) {
            val newChat = Chat(
                person = userName,
                lastMessage = "Sin mensajes",
                profileImageId = R.drawable.jack_frost, // Usar la imagen predeterminada
                userID = userID // Asegúrate de pasar el userID correctamente
            )
            chatList.add(newChat)
            chatAdapter.notifyItemInserted(chatList.size - 1)

            // Guardar los chats actualizados en SharedPreferences o Firebase
            saveChatsToSharedPreferences()
            saveChatToFirebase(newChat)
        }

        // Abrir la actividad de chat
        val intent = Intent(requireContext(), InternalChatUsers::class.java)
        intent.putExtra("userId", userID)
        intent.putExtra("userName", userName)
        startActivity(intent)
    }

    // Guardar los chats en Firebase con un identificador único
    private fun saveChatToFirebase(chat: Chat) {
        val userId = chat.userID
        // Usa push() para agregar un nuevo chat bajo el usuario
        val userChatsRef = database.child("chats").child(userId).push() // Usa push para crear un ID único para cada chat
        userChatsRef.setValue(chat)
    }

    // Guardar los chats en SharedPreferences
    private fun saveChatsToSharedPreferences() {
        val gson = Gson()
        val jsonChats = gson.toJson(chatList)
        val sharedPreferences = requireContext().getSharedPreferences("chatPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("chats", jsonChats)
        editor.apply()
    }

    private fun loadChatsFromSharedPreferences() {
        val sharedPreferences = requireContext().getSharedPreferences("chatPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val jsonChats = sharedPreferences.getString("chats", null)

        if (jsonChats != null) {
            try {
                val type = object : TypeToken<List<Chat>>() {}.type
                val savedChats: List<Chat> = gson.fromJson(jsonChats, type)
                chatList.clear()
                chatList.addAll(savedChats)
                chatAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error al cargar chats desde SharedPreferences", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Si no hay chats en SharedPreferences, puedes cargar desde Firebase
            loadChatsFromFirebase()
        }
    }

    private fun loadChatsFromFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            database.child("chats").child(userId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        chatList.clear()
                        for (chatSnapshot in snapshot.children) {
                            val chat = chatSnapshot.getValue(Chat::class.java)
                            chat?.let {
                                // Aquí no necesitas hacer nada, la imagen predeterminada se asigna en la clase Chat
                                chatList.add(it)
                            }
                        }
                        chatAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireContext(), "Error al cargar los chats", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }
}
