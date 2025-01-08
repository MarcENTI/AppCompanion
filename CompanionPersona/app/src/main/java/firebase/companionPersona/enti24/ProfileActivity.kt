package firebase.companionPersona.enti24

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Inicializar Realtime Database y FirebaseAuth
        val databaseURL = "https://p3rcompanion-default-rtdb.europe-west1.firebasedatabase.app/"
        database = FirebaseDatabase.getInstance(databaseURL).getReference("users")
        auth = FirebaseAuth.getInstance()

        googleSignInClient = GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        )

        // Referencias a los elementos del layout
        val userNameTextView = findViewById<TextView>(R.id.user_name)
        val userIdTextView = findViewById<TextView>(R.id.user_id)
        val btnLogOut = findViewById<Button>(R.id.LogOutButton)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // Obtener datos desde Realtime Database
            database.child(userId).get()
                .addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.exists()) {
                        val userName = dataSnapshot.child("name").value?.toString() ?: "Nombre no encontrado"
                        val userCustomId = dataSnapshot.child("public_id").value?.toString() ?: "ID no encontrado"

                        // Actualizar TextViews
                        userNameTextView.text = userName
                        userIdTextView.text = userCustomId
                    } else {
                        userNameTextView.text = "Usuario no encontrado"
                        userIdTextView.text = "N/A"
                    }
                }
                .addOnFailureListener { e ->
                    userNameTextView.text = "Error al cargar"
                    userIdTextView.text = "N/A"
                }
        } else {
            userNameTextView.text = "No has iniciado sesión"
            userIdTextView.text = "N/A"
        }

        btnLogOut.setOnClickListener {
            auth.signOut()
            googleSignInClient.signOut().addOnCompleteListener {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }

    }
}
