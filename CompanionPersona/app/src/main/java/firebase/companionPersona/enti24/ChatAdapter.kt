package firebase.companionPersona.enti24

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import models.Chat

class ChatAdapter(
    private val chats: List<Chat>,
    private val onChatClick: (Chat) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        // Inflar el layout de cada item de chat
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_recycler_view_manager, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chats[position]
        holder.bind(chat, onChatClick) // Pasar el chat y la función onClick
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    // ViewHolder para manejar los elementos de la vista
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val personName: TextView = itemView.findViewById(R.id.profile_name)
        private val lastMessage: TextView = itemView.findViewById(R.id.last_message)
        private val profileImage: ImageView = itemView.findViewById(R.id.chat_image)

        // Método para enlazar los datos con la vista
        fun bind(chat: Chat, onChatClick: (Chat) -> Unit) {
            personName.text = chat.person
            lastMessage.text = chat.lastMessage

            // Cargar la imagen con Glide y recortarla a un círculo
            Glide.with(itemView.context)
                .load(R.drawable.jack_frost)
                .circleCrop()
                .into(profileImage)

            // Configurar el listener de clic en el item de la list
            itemView.setOnClickListener {
                onChatClick(chat)
            }
        }
    }
}
