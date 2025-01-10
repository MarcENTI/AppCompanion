package firebase.companionPersona.enti24

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PersonaAdapter(private var personas: List<Persona>) :
    RecyclerView.Adapter<PersonaAdapter.PersonaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_compendium, parent, false)
        return PersonaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonaViewHolder, position: Int) {
        holder.bind(personas[position])
    }

    override fun getItemCount(): Int = personas.size

    fun updateData(newPersonas: List<Persona>) {
        personas = newPersonas
        notifyDataSetChanged()
    }

    class PersonaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val arcanaText: TextView = itemView.findViewById(R.id.arcanaTextView)
        private val levelText: TextView = itemView.findViewById(R.id.personaLevelTextView)
        private val nameText: TextView = itemView.findViewById(R.id.personaNameTextView)
        private val personaImageView: ImageView = itemView.findViewById(R.id.personaImageView) // ImageView para la imagen

        fun bind(persona: Persona) {
            arcanaText.text = persona.arcana
            levelText.text = persona.level.toString()
            nameText.text = persona.name

            // Cargar la imagen desde la URL usando Glide (glide recomendado por chat gpt)
            Glide.with(itemView.context)
                .load(persona.image)

                .into(personaImageView)
        }
    }
}

