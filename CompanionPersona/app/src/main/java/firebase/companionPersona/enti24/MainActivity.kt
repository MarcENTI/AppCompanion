package firebase.companionPersona.enti24

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //Iniciar firebase y configurar crashalytics
        FirebaseApp.initializeApp(this)

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        //Test of error
        //throw RuntimeException("Test Crash!")

        if (savedInstanceState == null) {
            val bottomNavFragment = BottomNavFragment()

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, bottomNavFragment)
                .commit()
        }

    }
}