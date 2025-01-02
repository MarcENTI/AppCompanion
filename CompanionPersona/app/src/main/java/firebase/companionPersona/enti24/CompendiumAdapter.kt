package firebase.companionPersona.enti24

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CompendiumAdapter(private val items: List<CompendiumItem>) :
    RecyclerView.Adapter<CompendiumAdapter.CompendiumViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompendiumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_compendium, parent, false)
        return CompendiumViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompendiumViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    class CompendiumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val arcanaText: TextView = itemView.findViewById(R.id.arcanaTextView)
        private val nameText: TextView = itemView.findViewById(R.id.personaNameTextView)
        private val levelText: TextView = itemView.findViewById(R.id.personaLevelTextView)

        fun bind(item: CompendiumItem) {
            arcanaText.text = item.arcana
            nameText.text = item.name
            levelText.text = item.level.toString()
        }
    }
}
