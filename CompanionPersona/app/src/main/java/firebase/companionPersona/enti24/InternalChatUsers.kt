package firebase.companionPersona.enti24

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class InternalChatUsers : AppCompatActivity() {

    private var chattingWithUserId: String? = null
    private var chattingWithUserName: String? = null
    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_internal_chat_users)

        firebaseAuth = FirebaseAuth.getInstance()
        val databaseURL = "https://p3rcompanion-default-rtdb.europe-west1.firebasedatabase.app/"
        database = FirebaseDatabase.getInstance(databaseURL).getReference("messages")

        //mensaje
        val messageEditText: EditText = findViewById(R.id.editTextMessage)
        val sendButton: ImageButton = findViewById(R.id.buttonSend)


        //coger datos del usuario con el que hablo
        chattingWithUserId = intent.getStringExtra("userId")
        chattingWithUserName = intent.getStringExtra("userName")

        val userNameTextView: TextView = findViewById(R.id.textUserName)
        userNameTextView.text = chattingWithUserName ?: "Desconocido"

        val backButton: ImageButton = findViewById(R.id.buttonBack)
        backButton.setOnClickListener {
            finish() // Finaliza la actividad y vuelve atrás
        }

        sendButton.setOnClickListener {
            val message = messageEditText.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message) // Llamamos a la función que enviará el mensaje
                messageEditText.text.clear() // Limpiamos el campo de texto
            }


        }
    }



    private fun sendMessage(message: String) {
        val currentUserId = firebaseAuth.currentUser?.uid
        if (currentUserId == null || chattingWithUserId == null) return

        // Crear un ID único para el chat
        val chatId = if (currentUserId.compareTo(chattingWithUserId!!) < 0) {
            "${currentUserId}_$chattingWithUserId"
        } else {
            "${chattingWithUserId}_$currentUserId"
        }

        val messageId = database.child(chatId).push().key

        val messageData = mapOf(
            "from" to currentUserId,
            "to" to chattingWithUserId,
            "message" to message,
            "timestamp" to System.currentTimeMillis()
        )

        // Guardar en Firebase
        if (messageId != null) {
            database.child(chatId).child(messageId).setValue(messageData)
                .addOnSuccessListener {
                    Log.d("SendMessage", "Mensaje enviado exitosamente")
                }
                .addOnFailureListener { e ->
                    Log.e("SendMessage", "Error al enviar mensaje", e)
                }
        }

    }
}