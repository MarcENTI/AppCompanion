package firebase.companionPersona.enti24

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class InternalChatUsers : AppCompatActivity() {

    private var chattingWithUserId: String? = null
    private var chattingWithUserName: String? = null
    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    private val messages = mutableListOf<Message>()
    private val processedMessageIds = mutableSetOf<String>() // Evitar duplicados
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var recyclerViewMessages: RecyclerView
    private lateinit var chatId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_internal_chat_users)

        firebaseAuth = FirebaseAuth.getInstance()
        val databaseURL = "https://p3rcompanion-default-rtdb.europe-west1.firebasedatabase.app/"
        database = FirebaseDatabase.getInstance(databaseURL).getReference("messages")

        // UI Elements
        val messageEditText: EditText = findViewById(R.id.editTextMessage)
        val sendButton: ImageButton = findViewById(R.id.buttonSend)
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages)

        // Setup RecyclerView
        messageAdapter = MessageAdapter(messages)
        recyclerViewMessages.adapter = messageAdapter
        recyclerViewMessages.layoutManager = LinearLayoutManager(this)

        // Obtener datos del usuario con el que estás chateando
        chattingWithUserId = intent.getStringExtra("userId")
        chattingWithUserName = intent.getStringExtra("userName")

        val userNameTextView: TextView = findViewById(R.id.textUserName)
        userNameTextView.text = chattingWithUserName ?: "Desconocido"

        // Configurar chatId
        val currentUserId = firebaseAuth.currentUser?.uid
        if (currentUserId != null && chattingWithUserId != null) {
            chatId = if (currentUserId.compareTo(chattingWithUserId!!) < 0) {
                "${currentUserId}_$chattingWithUserId"
            } else {
                "${chattingWithUserId}_$currentUserId"
            }
        } else {
            Log.e("ChatInit", "No se pudo obtener el ID del usuario o del destinatario.")
            return
        }

        // Escuchar mensajes
        listenForMessages()

        // Configurar botón de retroceso
        val backButton: ImageButton = findViewById(R.id.buttonBack)
        backButton.setOnClickListener { finish() }

        // Configurar botón de envío
        sendButton.setOnClickListener {
            val message = messageEditText.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
                messageEditText.text.clear()
            }
        }
    }

    private fun sendMessage(message: String) {
        val currentUserId = firebaseAuth.currentUser?.uid
        if (currentUserId == null || chattingWithUserId == null) {
            Log.e("SendMessage", "Usuario no autenticado o destinatario no definido.")
            return
        }

        val messageId = database.child(chatId).push().key
        if (messageId == null) {
            Log.e("SendMessage", "Error: El `messageId` es nulo.")
            return
        }

        val messageData = Message(
            from = currentUserId,
            message = message,
            timestamp = System.currentTimeMillis()
        )

        // Guardar el mensaje en Firebase
        database.child(chatId).child(messageId).setValue(messageData)
            .addOnSuccessListener {
                // Agregar mensaje al adaptador inmediatamente
                messages.add(messageData)
                messageAdapter.notifyItemInserted(messages.size - 1)
                recyclerViewMessages.scrollToPosition(messages.size - 1)

                // Registrar el mensaje como procesado
                processedMessageIds.add(messageId)

                Log.d("SendMessage", "Mensaje enviado exitosamente.")
            }
            .addOnFailureListener { e ->
                Log.e("SendMessage", "Error al enviar mensaje.", e)
            }
    }

    private fun listenForMessages() {
        database.child(chatId).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val messageId = snapshot.key ?: return // Verificar que el snapshot tiene un ID
                if (processedMessageIds.contains(messageId)) return // Evitar duplicados

                val message = snapshot.getValue(Message::class.java)
                if (message != null) {
                    messages.add(message)
                    processedMessageIds.add(messageId) // Registrar el ID como procesado
                    messageAdapter.notifyItemInserted(messages.size - 1)
                    recyclerViewMessages.scrollToPosition(messages.size - 1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.e("ListenForMessages", "Error al escuchar mensajes: ${error.message}")
            }
        })
    }
}
