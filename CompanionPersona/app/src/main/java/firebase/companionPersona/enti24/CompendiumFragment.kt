package firebase.companionPersona.enti24

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

class CompendiumFragment : Fragment() {

    private lateinit var fusionCalculatorEditText: EditText
    private lateinit var compendiumSearchEditText: EditText
    private lateinit var filtersCheckBox: CheckBox
    private lateinit var btnByArcana: Button
    private lateinit var btnByLevel: Button
    private lateinit var btnByName: Button
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView


    private val sampleData = listOf(
        CompendiumItem("Fool", "Orpheus Telos", 97),
        CompendiumItem("Fool", "Susano-o", 82),
        CompendiumItem("Fool", "Loki", 73),
        CompendiumItem("Fool", "Decarabia", 58),
        CompendiumItem("Fool", "Ose", 45),
        CompendiumItem("Fool", "Black Frost", 41),
        CompendiumItem("Fool", "Legion", 28),
        CompendiumItem("Fool", "Slime", 14)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_compendium, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        fusionCalculatorEditText = view.findViewById(R.id.fusionCalculatorEditText)
        compendiumSearchEditText = view.findViewById(R.id.compendiumSearchEditText)
        filtersCheckBox = view.findViewById(R.id.filtersCheckBox)
        btnByArcana = view.findViewById(R.id.btnByArcana)
        btnByLevel = view.findViewById(R.id.btnByLevel)
        btnByName = view.findViewById(R.id.btnByName)
        recyclerView = view.findViewById(R.id.compendiumRecyclerView)


        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = CompendiumAdapter(sampleData)


        compendiumSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        filtersCheckBox.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(requireContext(), "Filters checked? $isChecked", Toast.LENGTH_SHORT).show()
        }


        btnByArcana.setOnClickListener {
            Toast.makeText(requireContext(), "By Arcana clicked", Toast.LENGTH_SHORT).show()
        }
        btnByLevel.setOnClickListener {
            Toast.makeText(requireContext(), "By Level clicked", Toast.LENGTH_SHORT).show()
        }
        btnByName.setOnClickListener {
            Toast.makeText(requireContext(), "By Name clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
