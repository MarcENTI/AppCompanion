package firebase.companionPersona.enti24

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ConfidentsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ConfidentAdapter
    private var showPersonaImages = false // estado del switch

    // Lista ejemplo de confidants con dos imágenes: carta y persona
    // Asegúrate de tener estos drawables en res/drawable
    private val confidantsList = listOf(
        Confidant("Mutatsu (Tower)", R.drawable.mutatsu_card, R.drawable.mutatsu_persona),
        Confidant("Bunkichi & Mitsuko (Hierophant)", R.drawable.bunkichi_mitsuko_card, R.drawable.bunkichi_mitsuko_persona),
        Confidant("Aigis (Eon)", R.drawable.aigis_card, R.drawable.aigis_persona),
        Confidant("Yukari (Lovers)", R.drawable.yukari_card, R.drawable.yukari_persona),
        Confidant("Tanaka (Devil)", R.drawable.tanaka_card, R.drawable.tanaka_persona),
        Confidant("Miyamoto (Chariot)", R.drawable.miyamoto_card, R.drawable.miyamoto_persona)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.confidents_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val descriptionText = view.findViewById<TextView>(R.id.descriptionTextView)
        val imageSwitch = view.findViewById<Switch>(R.id.imageSwitch)

        // Texto descriptivo
        descriptionText.text = getString(R.string.confidant_information)

        recyclerView = view.findViewById(R.id.confidentsRecyclerView)
        // 3 columnas para las cartas
        val spanCount = 3
        recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)

        // Añadimos espacios uniformes entre las cartas
        val spacing = resources.getDimensionPixelSize(R.dimen.recycler_item_spacing)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing))

        adapter = ConfidentAdapter(confidantsList, showPersonaImages) { selectedConfidant ->
            val detailFragment = ConfidantDetailFragment.newInstance(
                selectedConfidant.name,
                selectedConfidant.personaImageResId
            )
            parentFragmentManager.beginTransaction()
                .replace(R.id.inner_fragment_container, detailFragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = adapter

        imageSwitch.setOnCheckedChangeListener { _, isChecked ->
            showPersonaImages = isChecked
            adapter.showPersonaImages = showPersonaImages
            adapter.notifyDataSetChanged()
        }
    }

    // Clase para espaciar uniformemente los items en el grid
    class GridSpacingItemDecoration(private val spanCount: Int, private val spacing: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view) // posición del item
            val column = position % spanCount
            // Calcular márgenes para que queden uniformes
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            if (position >= spanCount) {
                outRect.top = spacing
            }
        }
    }
}
