package firebase.companionPersona.enti24

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics

class ForumFragment : Fragment() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var startTime: Long = 0 // Almacena el tiempo de entrada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializar Firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Registrar tiempo de entrada
        startTime = SystemClock.elapsedRealtime()
        logFragmentStart()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forum, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logFragmentEnd()
    }

    private fun logFragmentStart() {
        val bundle = Bundle().apply {
            putString("fragment_name", "ForumFragment")
        }
        firebaseAnalytics.logEvent("fragment_enter", bundle)
    }

    private fun logFragmentEnd() {
        // Calcular tiempo pasado en el fragmento
        val endTime = SystemClock.elapsedRealtime()
        val duration = endTime - startTime

        // Registrar el evento de salida con la duración
        val bundle = Bundle().apply {
            putString("fragment_name", "ForumFragment")
            putLong("time_spent", duration)
        }
        firebaseAnalytics.logEvent("fragment_exit", bundle)
    }
}
