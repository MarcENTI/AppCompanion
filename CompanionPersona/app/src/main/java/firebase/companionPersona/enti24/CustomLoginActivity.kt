package firebase.companionPersona.enti24

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_login)

        emailField = findViewById(R.id.etEmail)
        passwordField = findViewById(R.id.etPassword)
        auth = FirebaseAuth.getInstance()

        findViewById<Button>(R.id.btnRegister).setOnClickListener { register() }
        findViewById<Button>(R.id.btnLogin).setOnClickListener { login() }
    }

    private fun register() {
        val email = emailField.text.toString()
        val password = passwordField.text.toString()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Registro fallido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login() {
        val email = emailField.text.toString()
        val password = passwordField.text.toString()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Funciona", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No funciona", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
