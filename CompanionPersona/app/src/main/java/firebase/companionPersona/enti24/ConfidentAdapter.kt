package firebase.companionPersona.enti24

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ConfidentAdapter(
    private val confidants: List<Confidant>,
    var showPersonaImages: Boolean,
    private val onItemClick: (Confidant) -> Unit
) : RecyclerView.Adapter<ConfidentAdapter.ConfidentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfidentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_confidant_card, parent, false)
        return ConfidentViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ConfidentViewHolder, position: Int) {
        holder.bind(confidants[position], showPersonaImages)
    }

    override fun getItemCount() = confidants.size

    class ConfidentViewHolder(itemView: View, val onItemClick: (Confidant) -> Unit): RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.confidantNameTextView)
        private val imageView: ImageView = itemView.findViewById(R.id.confidantCardImageView)

        fun bind(confidant: Confidant, showPersonaImages: Boolean) {
            nameText.text = confidant.name
            val imageRes = if (showPersonaImages) confidant.personaImageResId else confidant.cardImageResId
            imageView.setImageResource(imageRes)
            itemView.setOnClickListener {
                onItemClick(confidant)
            }
        }
    }
}
