package firebase.companionPersona.enti24

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import models.Chat

class ChatAdapter(private val chats: List<Chat>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_recycler_view_manager, parent, false)

        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatAdapter.ChatViewHolder, position: Int) {
        val chat = chats[position]
        holder.bind(chat)
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        private val personName: TextView = itemView.findViewById(R.id.profile_name)
        private val lastMessage: TextView = itemView.findViewById(R.id.last_message)
        private val profileImage: ImageView = itemView.findViewById(R.id.chat_image)

        fun bind(chat: Chat)
        {
            personName.text = chat.person
            lastMessage.text = chat.lastMessage
            profileImage.setImageResource(chat.profileImageId)
        }

    }


}