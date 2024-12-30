package firebase.companionPersona.enti24

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        //iniciarlizar firestore y authentication
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        //textViews
        val userNameTextView = findViewById<TextView>(R.id.user_name)
        val userIdTextView = findViewById<TextView>(R.id.user_id)

        val currentUser = auth.currentUser
        if(currentUser != null)
        {
            val userId = currentUser.uid

            //obtener datos de firestore
            db.collection("users").document(userId).get()
                .addOnSuccessListener {
                    document->
                    if(document !=null && document.exists())
                    {
                        val userName = document.getString("name") ?: "Nombre no encontrado"
                        val userCustomId = document.getString("id") ?: "ID no encontrado"

                        //act textViews
                        userNameTextView.text = userName
                        userIdTextView.text = userCustomId
                    }else {
                        userNameTextView.text = "Usuario no encontrado"
                        userIdTextView.text = "N/A"
                    }
                }
                .addOnFailureListener {
                    e->
                    userNameTextView.text = "Error al cargar"
                    userIdTextView.text = "N/A"
                }


            
        }
        else
        {
            userNameTextView.text = "No has iniciado sesión"
            userIdTextView.text = "N/A"
        }





    }
}