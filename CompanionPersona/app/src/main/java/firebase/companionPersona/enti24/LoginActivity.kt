package firebase.companionPersona.enti24

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
import com.google.firebase.database.Query

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
        val databaseURL = "https://p3rcompanion-default-rtdb.europe-west1.firebasedatabase.app/"
        databaseReference = FirebaseDatabase.getInstance(databaseURL).getReference("users")

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
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } ?: run {
            Log.d("Login Google", "No hay sesión iniciada")
            findViewById<SignInButton>(R.id.btn_login_google).setOnClickListener { signInGoogle() }
        }

        btnLogin.setOnClickListener {
            val etUsername = findViewById<EditText>(R.id.etUsername)
            val etPassword = findViewById<EditText>(R.id.etPassword)

            val usernameOrEmail = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (usernameOrEmail.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (Patterns.EMAIL_ADDRESS.matcher(usernameOrEmail).matches()) {
                // Es un correo electrónico, intentar iniciar sesión directamente
                signInWithEmail(usernameOrEmail, password)
            } else {
                // No es un correo, asumir que es un nombre de usuario y buscar su correo en la base de datos
                findEmailByUsername(usernameOrEmail) { email ->
                    if (email != null) {
                        signInWithEmail(email, password)
                    } else {
                        Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnRegister.setOnClickListener {
            //startActivity(Intent(this, RegisterActivity::class.java))
            startActivity(Intent(this, MainActivity::class.java))
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
                    val intent = Intent(this, MainActivity::class.java)
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
        generateUniquePublicUserID { publicID ->
            val user = mapOf(
                "name" to (name ?: "Desconocido"),
                "email" to (email ?: "Sin email"),
                "id" to uid,
                "public_id" to publicID
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
    }


    private fun generateUniquePublicUserID(callback: (String) -> Unit) {
        val letters = ('A'..'Z').toList()

        fun generateID(): String {
            val randomLetter = letters.random()

            val numbers = (1000000 .. 99999999).toList()
            val randomNumber = numbers.random()
            return "$randomLetter$randomNumber"
        }


        //Comprobar si el ID ya existe en Database

        val newID = generateID()

        Log.d("GeneratedID", "ID generado: $newID")  // Log para ver el ID generado


        databaseReference.orderByChild("public_id").equalTo(newID).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // Si el ID ya existe, generar uno nuevo
                    Log.d("RealtimeDatabase", "ID duplicado encontrado: $newID. Generando uno nuevo.")
                    generateUniquePublicUserID(callback)
                } else {
                    // Si el ID es único, retornar el resultado
                    Log.d("RealtimeDatabase", "ID único generado: $newID")
                    callback(newID)
                }
            }
            .addOnFailureListener { e ->
                Log.e("RealtimeDatabase", "Error al verificar unicidad del ID en Firebase", e)
                callback(generateID()) // Si falla la verificación, retornar un ID nuevo
            }
    }


    //para iniciar sesion con correo
    private fun signInWithEmail(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Error de inicio de sesión: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    //para que se pueda iniciar sesion con usuario o correo indiferentemente
    private fun findEmailByUsername(username: String, callback: (String?) -> Unit) {
        Log.d("RealtimeDatabase", "Buscando email para el usuario: $username") // Log para depuración
        val query: Query = databaseReference.orderByChild("name").equalTo(username)

        query.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    Log.d("RealtimeDatabase", "Datos encontrados para usuario: ${snapshot.value}")
                    val email = snapshot.children.firstOrNull()?.child("email")?.value as? String

                    if (email.isNullOrBlank()) {
                        Log.w("RealtimeDatabase", "El email encontrado está vacío o es nulo.")
                        callback(null)
                    } else {
                        Log.d("RealtimeDatabase", "Email encontrado: $email")
                        callback(email)
                    }
                } else {
                    Log.w("RealtimeDatabase", "No se encontró un usuario con el nombre: $username")
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("RealtimeDatabase", "Error al buscar el usuario en Firebase", e)
                callback(null)
            }
    }

}
