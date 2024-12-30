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

class LoginActivity : AppCompatActivity() {

    companion object {
        const val RC_SIGN_IN_GOOGLE = 9178
    }

    private lateinit var googleSignInClient: GoogleSignInClient

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
                Log.d("Login Google", "Tengo la info de:" + account.displayName)
            }
            else
            {
                Log.w("Login Google", "Error en el login", task.exception)
            }
        }

    }
}
