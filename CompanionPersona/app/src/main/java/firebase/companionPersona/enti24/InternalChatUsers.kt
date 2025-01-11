package firebase.companionPersona.enti24

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class InternalChatUsers : AppCompatActivity() {

    private var chattingWithUserId: String? = null
    private var chattingWithUserName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_internal_chat_users)


        //coger datos del usuario con el que hablo
        chattingWithUserId = intent.getStringExtra("userId")
        chattingWithUserName = intent.getStringExtra("userName")

        val userNameTextView: TextView = findViewById(R.id.textUserName)
        userNameTextView.text = chattingWithUserName ?: "Desconocido"

        val backButton: ImageButton = findViewById(R.id.buttonBack)
        backButton.setOnClickListener {
            finish() // Finaliza la actividad y vuelve atrás
        }


    }
}