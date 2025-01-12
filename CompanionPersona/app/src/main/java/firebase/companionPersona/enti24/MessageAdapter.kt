package firebase.companionPersona.enti24

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import firebase.companionPersona.enti24.R

data class Message(
    val from: String,
    val message: String,
    val timestamp: Long
)

class MessageAdapter(private val messages: List<Message>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun getItemViewType(position: Int): Int {
        // Determinar si el mensaje es enviado o recibido
        return if (messages[position].from == currentUserId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutId = if (viewType == VIEW_TYPE_SENT) {
            R.layout.item_message_sent
        } else {
            R.layout.item_message_received
        }

        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int = messages.size

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textMessage: TextView = itemView.findViewById(R.id.textMessage)
        private val textTimestamp: TextView = itemView.findViewById(R.id.textTimestamp)

        fun bind(message: Message) {
            textMessage.text = message.message
            textTimestamp.text = formatTimestamp(message.timestamp)
        }

        private fun formatTimestamp(timestamp: Long): String {
            // Pasar la hoara a formato legible
            val dateFormat = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
            return dateFormat.format(java.util.Date(timestamp))
        }
    }

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }
}
