package firebase.companionPersona.enti24

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompendiumFragment : Fragment() {

    private lateinit var fusionCalculatorEditText: EditText
    private lateinit var compendiumSearchEditText: EditText
    private lateinit var filtersCheckBox: CheckBox
    private lateinit var btnByArcana: Button
    private lateinit var btnByLevel: Button
    private lateinit var btnByName: Button
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var progressBar: ProgressBar


    private val personaAdapter = PersonaAdapter(emptyList())


    private var allPersonas: List<Persona> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_compendium, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        progressBar = view.findViewById(R.id.progressBarCompendium)
        fusionCalculatorEditText = view.findViewById(R.id.fusionCalculatorEditText)
        compendiumSearchEditText = view.findViewById(R.id.compendiumSearchEditText)
        filtersCheckBox = view.findViewById(R.id.filtersCheckBox)
        btnByArcana = view.findViewById(R.id.btnByArcana)
        btnByLevel = view.findViewById(R.id.btnByLevel)
        btnByName = view.findViewById(R.id.btnByName)
        recyclerView = view.findViewById(R.id.compendiumRecyclerView)


        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = personaAdapter

        // Llamada real a la API: /personas/
        fetchAllPersonas()


        compendiumSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                filterPersonas(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        filtersCheckBox.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(requireContext(), "Filters: $isChecked", Toast.LENGTH_SHORT).show()
        }


        btnByArcana.setOnClickListener {
            Toast.makeText(requireContext(), "By Arcana clicked", Toast.LENGTH_SHORT).show()

            val sorted = allPersonas.sortedBy { it.arcana }
            personaAdapter.updateData(sorted)
        }

        btnByLevel.setOnClickListener {
            Toast.makeText(requireContext(), "By Level clicked", Toast.LENGTH_SHORT).show()

            val sorted = allPersonas.sortedBy { it.level }
            personaAdapter.updateData(sorted)
        }


        btnByName.setOnClickListener {
            val personaName = compendiumSearchEditText.text.toString().trim()
            if (personaName.isNotEmpty()) {
                fetchPersonaByName(personaName)
            } else {
                Toast.makeText(requireContext(), "Enter a name to search", Toast.LENGTH_SHORT).show()
            }
        }
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


    private fun fetchPersonaByName(personaName: String) {
        progressBar.visibility = View.VISIBLE
        RetrofitInstance.api.getPersonaByName(personaName).enqueue(object : Callback<Persona> {
            override fun onResponse(call: Call<Persona>, response: Response<Persona>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val onePersona = response.body()
                    if (onePersona != null) {

                        personaAdapter.updateData(listOf(onePersona))
                    } else {
                        Toast.makeText(requireContext(), "Persona not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Persona>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error searching: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    
    private fun filterPersonas(query: String) {
        val filtered = allPersonas.filter { it.name.contains(query, ignoreCase = true) }
        personaAdapter.updateData(filtered)
    }
}
