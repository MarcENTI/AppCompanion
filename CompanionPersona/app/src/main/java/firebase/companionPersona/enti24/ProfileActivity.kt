package firebase.companionPersona.enti24

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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

    var isEditing = false

    @SuppressLint("MissingInflatedId")
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

        //edicion del nombre
        val editNameIcon = findViewById<ImageView>(R.id.edit_name_icon)

        //cambiar contraseña
        val changePasswordButton = findViewById<Button>(R.id.changePasswordButton)

        //eliminar cuenta
        val deleteAccountButton = findViewById<Button>(R.id.deleteAccountButton)

        //Crash Test Button
        val crashTestButton = findViewById<Button>(R.id.crashTestButton)


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

        //editar nombre
        editNameIcon.setOnClickListener{
            if(!isEditing)
                enableEditing()
            else
                saveNameToDatabase()
        }

        //editar contraseña
        changePasswordButton.setOnClickListener {
            sendPasswordResetEmail()
        }

        //Log Out
        btnLogOut.setOnClickListener {
            auth.signOut()
            googleSignInClient.signOut().addOnCompleteListener {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }

        //borrar cuenta
        deleteAccountButton.setOnClickListener{
            deleteAccount()
        }

        crashTestButton.setOnClickListener{
            throw RuntimeException("Test Crash!")
        }

    }

    //cambair text view por edit view, para editar el username
    private fun enableEditing()
    {
        val userNameTextView = findViewById<TextView>(R.id.user_name)

        val editText = EditText(this).apply {
            id = R.id.edit_user_name
            layoutParams = userNameTextView.layoutParams
            setText(userNameTextView.text.toString())
        }

        val parent = userNameTextView.parent as LinearLayout
        val index = parent.indexOfChild(userNameTextView)
        parent.removeViewAt(index)
        parent.addView(editText, index)

        val editNameIcon = findViewById<ImageView>(R.id.edit_name_icon)
        editNameIcon.setImageResource(R.drawable.baseline_save_alt_24)

        isEditing = true
    }

    private fun sendPasswordResetEmail()
    {
        val currentUser = auth.currentUser

        if (currentUser != null && currentUser.email != null) {
            val email = currentUser.email

            auth.sendPasswordResetEmail(email!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Correo de restablecimiento enviado a $email", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error al enviar el correo: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            Toast.makeText(this, "No se pudo obtener el correo del usuario.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveNameToDatabase() {
        val editText = findViewById<EditText>(R.id.edit_user_name)
        val newName = editText.text.toString().trim()

        if (newName.isNotEmpty()) {
            // Guardar en Database
            val currentUser = auth.currentUser
            currentUser?.let {
                database.child(it.uid).child("name").setValue(newName)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Nombre actualizado", Toast.LENGTH_SHORT).show()

                        // Volver a ser textView
                        val userNameTextView = TextView(this).apply {
                            id = R.id.user_name
                            layoutParams = editText.layoutParams
                            text = newName
                        }

                        val parent = editText.parent as LinearLayout
                        val index = parent.indexOfChild(editText)
                        parent.removeViewAt(index)
                        parent.addView(userNameTextView, index)

                        val editNameIcon = findViewById<ImageView>(R.id.edit_name_icon)
                        editNameIcon.setImageResource(R.drawable.baseline_edit_24)
                        isEditing = false
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteAccount() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            // Eliminar datos del usuario en Database
            database.child(userId).removeValue()
                .addOnSuccessListener {
                    // Eliminar la cuenta en Firebase Authentication
                    currentUser.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Cuenta eliminada correctamente", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, "Error al eliminar cuenta: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al borrar datos: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "No se encontró usuario autenticado", Toast.LENGTH_SHORT).show()
        }
    }


}
