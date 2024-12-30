package firebase.companionPersona.enti24

import android.app.ComponentCaller
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
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    companion object {
        const val RC_SIGN_IN_GOOGLE = 9178
    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("431744884113-hgmcd9qdihi8uam2kc28osm3g0nboect.apps.googleusercontent.com")
            .requestEmail()
            .build()

        //boton google

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val account = GoogleSignIn.getLastSignedInAccount(this)

        account?.let {
            Log.d("Login Google", "Ya has robado la info de la cuenta: " + account.displayName + "anteriormente")
        }?:run {
            Log.d("Login Google", "No hay sesion iniciada")
            findViewById<SignInButton>(R.id.btn_login_google).setOnClickListener{ signInGoogle()}
        }



        btnLogin.setOnClickListener {
            // TODO: Agregar validación de usuario
            startActivity(Intent(this, MainActivity::class.java))
        }


        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun signInGoogle()
    {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN_GOOGLE)
        {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if(task.isSuccessful)
            {
                val account = task.getResult(ApiException::class.java)

                if(account != null)
                {
                    val usersCollection = db.collection("users")

                    //numero de usuarios existentes
                    usersCollection.get().addOnSuccessListener {
                        result ->
                        val userCount = result.size()
                        val generatedId = GenerateUserID(userCount + 1)

                        val userData = hashMapOf(
                            "name" to account.displayName,
                            "email" to account.email,
                            "id" to generatedId
                        )

                        //guardar datos en firebase firestore

                        usersCollection.document(account.id!!).set(userData)
                            .addOnSuccessListener {
                                Log.d("Firestore", "Usuario guardado exitosamente con ID: $generatedId")

                                //redirigir a profileActivity
                                val intent = Intent(this, ProfileActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "Error al guardar usuario", e)
                            }
                    }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error al obtener la colección de usuarios", e)
                        }
                }
                else
                {
                    Log.w("Login Google", "ID de cuenta es nulo.")
                }
            }
            else
            {
                Log.w("Login Google", "Error en el login", task.exception)
            }
        }

    }

    private fun GenerateUserID(userCount: Int): String {
        val letter = ('A'..'Z').random()
        val number = String.format("%04d", userCount) // Sin sumar +1 aquí
        return "$letter$number"
    }

}
