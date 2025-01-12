package firebase.companionPersona.enti24

import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompendiumFragment : Fragment() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var fusionCalculatorEditText: EditText
    private lateinit var compendiumSearchEditText: EditText
    private lateinit var btnByArcana: Button
    private lateinit var btnByLevel: Button
    private lateinit var btnByName: Button
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var progressBar: ProgressBar

    private val personaAdapter = PersonaAdapter(emptyList())
    private var allPersonas: List<Persona> = emptyList()

    private var startTime: Long = 0 // Para rastrear tiempo de entrada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializar Firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_compendium, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Registrar el tiempo de entrada al fragmento
        startTime = SystemClock.elapsedRealtime()
        logFragmentStart()

        progressBar = view.findViewById(R.id.progressBarCompendium)
        fusionCalculatorEditText = view.findViewById(R.id.fusionCalculatorEditText)
        compendiumSearchEditText = view.findViewById(R.id.compendiumSearchEditText)
        btnByArcana = view.findViewById(R.id.btnByArcana)
        btnByLevel = view.findViewById(R.id.btnByLevel)
        btnByName = view.findViewById(R.id.btnByName)
        recyclerView = view.findViewById(R.id.compendiumRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = personaAdapter

        fetchAllPersonas()

        compendiumSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterLocalList(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnByArcana.setOnClickListener {
            Toast.makeText(requireContext(), "By Arcana clicked", Toast.LENGTH_SHORT).show()
            val sortedList = allPersonas.sortedBy { it.arcana }
            personaAdapter.updateData(sortedList)
        }

        btnByLevel.setOnClickListener {
            Toast.makeText(requireContext(), "By Level clicked", Toast.LENGTH_SHORT).show()
            val sortedList = allPersonas.sortedBy { it.level }
            personaAdapter.updateData(sortedList)
        }

        btnByName.setOnClickListener {
            Toast.makeText(requireContext(), "By Name clicked", Toast.LENGTH_SHORT).show()
            val sortedList = allPersonas.sortedBy { it.name }
            personaAdapter.updateData(sortedList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logFragmentEnd()
    }

    private fun fetchAllPersonas() {
        progressBar.visibility = View.VISIBLE
        RetrofitInstance.api.getAllPersonas().enqueue(object : Callback<List<Persona>> {
            override fun onResponse(call: Call<List<Persona>>, response: Response<List<Persona>>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val personas = response.body() ?: emptyList()
                    allPersonas = personas
                    personaAdapter.updateData(allPersonas)
                } else {
                    Toast.makeText(requireContext(), "HTTP Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("CompendiumFragment", "HTTP Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Persona>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("CompendiumFragment", "onFailure: ${t.localizedMessage}")
            }
        })
    }

    private fun filterLocalList(query: String) {
        val filtered = allPersonas.filter { it.name.contains(query, ignoreCase = true) }
        personaAdapter.updateData(filtered)
    }

    private fun logFragmentStart() {
        val bundle = Bundle().apply {
            putString("fragment_name", "CompendiumFragment")
        }
        firebaseAnalytics.logEvent("fragment_enter", bundle)
    }

    private fun logFragmentEnd() {
        val endTime = SystemClock.elapsedRealtime()
        val duration = endTime - startTime // Calcular tiempo pasado

        val bundle = Bundle().apply {
            putString("fragment_name", "CompendiumFragment")
            putLong("time_spent", duration)
        }
        firebaseAnalytics.logEvent("fragment_exit", bundle)
    }
}
