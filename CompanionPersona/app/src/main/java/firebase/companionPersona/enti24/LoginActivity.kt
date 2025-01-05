package firebase.companionPersona.enti24

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    companion object {
        const val RC_SIGN_IN_GOOGLE = 9178
    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth // Corregido: Declarar firebaseAuth correctamente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar Firebase Auth y Realtime Database
        firebaseAuth = FirebaseAuth.getInstance() // Corregido: Inicialización de firebaseAuth
        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("431744884113-hgmcd9qdihi8uam2kc28osm3g0nboect.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val account = GoogleSignIn.getLastSignedInAccount(this)
        account?.let {
            Log.d("Login Google", "Ya has robado la info de la cuenta: ${account.displayName} anteriormente")
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        } ?: run {
            Log.d("Login Google", "No hay sesión iniciada")
            findViewById<SignInButton>(R.id.btn_login_google).setOnClickListener { signInGoogle() }
        }

        btnLogin.setOnClickListener {
            // TODO: Agregar validación de usuario
            startActivity(Intent(this, MainActivity::class.java))
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        Log.d("Login Google", "Iniciando el intent de inicio de sesión de Google")
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN_GOOGLE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                val account = task.getResult(ApiException::class.java)

                if (account != null) {
                    val idToken = account.idToken
                    val credential = GoogleAuthProvider.getCredential(idToken, null)

                    firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener { authTask ->
                            if (authTask.isSuccessful) {
                                // Usuario autenticado con Google
                                val currentUser = firebaseAuth.currentUser
                                currentUser?.let {
                                    checkIfUserExistsAndProceed(
                                        it.uid,
                                        account.displayName,
                                        account.email
                                    ) // Cambio: Ahora verifica si existe antes de proceder.
                                }
                            } else {
                                Log.e(
                                    "FirebaseAuth",
                                    "Error al autenticar en Firebase",
                                    authTask.exception
                                )
                            }
                        }
                } else {
                    Log.w("Login Google", "ID de cuenta es nulo.")
                }
            } else {
                Log.w("Login Google", "Error al iniciar sesión con Google", task.exception)
            }
        }
    }

    // Cambio: Nueva función para verificar la existencia del usuario
    private fun checkIfUserExistsAndProceed(uid: String, name: String?, email: String?) {
        databaseReference.child(uid).get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    // Usuario ya existe, redirigir directamente
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Usuario no existe, guardarlo
                    saveUserToRealtimeDatabase(uid, name, email) {
                        val intent = Intent(this, ProfileActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("RealtimeDatabase", "Error al verificar la existencia del usuario", e)
            }
    }


    private fun saveUserToRealtimeDatabase(uid: String, name: String?, email: String?, onSuccess: () -> Unit) {
        val user = mapOf(
            "name" to (name ?: "Desconocido"),
            "email" to (email ?: "Sin email"),
            "id" to uid,
            "public_id" to generatePublicUserID()
        )

        databaseReference.child(uid).setValue(user)
            .addOnSuccessListener {
                Log.d("RealtimeDatabase", "Usuario guardado exitosamente en la base de datos")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("RealtimeDatabase", "Error al guardar el usuario en la base de datos", e)
            }
    }

    private fun generatePublicUserID(): String {
        val letters = ('A'..'Z').toList()
        val randomLetter = letters.random()
        val randomNumber = (1000..9999).random()
        return "$randomLetter$randomNumber" // Ejemplo: "A1234"
    }
}
