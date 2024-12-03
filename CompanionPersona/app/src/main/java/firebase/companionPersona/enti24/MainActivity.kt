package firebase.companionPersona.enti24

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        private lateinit var googleSignInClient: GoogleSignInClient

        super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_login_google)

        val CHORISOMYBELOVED = "431744884113-hgmcd9qdihi8uam2kc28osm3g0nboect.apps.googleusercontent.com"

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
        requestIdToken(CHORISOMYBELOVED)
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()

        val account = GoogleSignIn.getLastSignedInAccount(this)

        account?.let{
            Log.d("Login Google", "Ya he robado la informacion de este usuario antes: " + account.displayName)
        }?:run {
            findViewById<SignInButton>(R.id.btn_login_google).setOnClickListener{signIn()}
        }



        if (savedInstanceState == null) {
            val bottomNavFragment = BottomNavFragment()

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, bottomNavFragment)
                .commit()
        }

    }

    private fun signIn()
    {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 9001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, caller: ComponentCaller)
    {
        super.onActivityResult(requestCode, resultCode, data, caller)

        if(requestCode == 9001)
        {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if(task.isSuccessful)
            {
                val account = task.getResult(ApiException::class.java)
                Log.d("Login Google", "Tengo la info de: " + account.displayName)
            }
            else
            {
                Log.d("Login Google", "Error en el login: " + task.exception)
            }
        }
    }


}