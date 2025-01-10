package firebase.companionPersona.enti24

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //iniciar firebase
        auth = FirebaseAuth.getInstance()
        val databaseURL = "https://p3rcompanion-default-rtdb.europe-west1.firebasedatabase.app/"
        database = FirebaseDatabase.getInstance(databaseURL).getReference("users")


        //user data
        val etUsername = findViewById<EditText>(R.id.etNewUsername)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etNewPassword)

        val btnRegister = findViewById<Button>(R.id.btnRegisterConfirm)

        btnRegister.setOnClickListener {
            //validate data
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            //comprobar datos completos
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //comprobar email valido
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Correo electrónico inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //comprobar longitud de la contraseña (posible mejora con el clasico X letras, mayusculas...)
            if (password.length < 8) {
                Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            saveUserToRealtimeDatabase(currentUser.uid, username, email)

                            //redirigir a la siguiente activity
                            val intent = Intent(this, ProfileActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(this, "Error al registrar usuario: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun saveUserToRealtimeDatabase(uid: String, username: String, email: String) {
        val publicUserID = generatePublicUserID()

        val user = mapOf(
            "name" to username,
            "email" to email,
            "id" to uid,
            "public_id" to publicUserID
        )

        Log.d("Database", "Guardando datos del usuario: $user en la referencia: ${database.child(uid)}")

        database.child(uid).setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al guardar usuario: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("DatabaseError", "Error al guardar usuario", exception)
            }
    }


    private fun generatePublicUserID(): String {
        val letters = ('A'..'Z').toList()
        val randomLetter = letters.random()
        val randomNumber = (1000..9999).random() // Número aleatorio de 4 dígitos
        return "$randomLetter$randomNumber" // Ejemplo: "A1234"
    }
}
