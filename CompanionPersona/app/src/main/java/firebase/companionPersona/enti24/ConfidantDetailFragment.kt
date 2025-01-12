package firebase.companionPersona.enti24

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class ConfidantDetailFragment : Fragment() {

    private var confidantName: String? = null
    private var personaImageResId: Int = 0

    companion object {
        private const val ARG_NAME = "arg_name"
        private const val ARG_PERSONA_IMAGE = "arg_persona_image"

        fun newInstance(name: String, personaImageResId: Int): ConfidantDetailFragment {
            val fragment = ConfidantDetailFragment()
            val args = Bundle().apply {
                putString(ARG_NAME, name)
                putInt(ARG_PERSONA_IMAGE, personaImageResId)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        confidantName = arguments?.getString(ARG_NAME)
        personaImageResId = arguments?.getInt(ARG_PERSONA_IMAGE) ?: 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.confidant_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val nameTextView = view.findViewById<TextView>(R.id.detailConfidantName)
        val imageView = view.findViewById<ImageView>(R.id.detailConfidantImage)
        val closeButton = view.findViewById<ImageView>(R.id.closeButton)
        val infoTextView = view.findViewById<TextView>(R.id.detailConfidantInfo)

        nameTextView.text = confidantName
        imageView.setImageResource(personaImageResId)

        infoTextView.text = """
            ${getString(R.string.detail_availability)}
            Nights of Thursday, Friday, Saturday and Sunday

            ${getString(R.string.detail_requisites)}
            Courage Level 2 (enter Escapade)
            Yuko (Strength) Confident Rank 4 + Talking to her

            ${getString(R.string.detail_note)}
            Easy to achieve maximum rank, but the first tower persona is level 31.
            It's not necessary a tower Persona except that you will fall short at rank 9 to 10.
            
            Rank 1 (Unlock)
            No points availables
            Mutatsu asks for a drink so
            Speak to the bartender and help him with the drink.

            Minigame Solution ...
            (Agrega más texto si deseas)
        """.trimIndent()

        closeButton.contentDescription = getString(R.string.close_detail)
        closeButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
