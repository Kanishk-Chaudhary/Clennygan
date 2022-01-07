package com.KanishkChaudhary.clennygan.app.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.KanishkChaudhary.clennygan.app.R
import com.KanishkChaudhary.clennygan.app.util.Message


class MessagesAdapter(private var messages:ArrayList<Message>, var userId:String)
    :RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {



    companion object{
        val MESSAGE_CURRENT_USER = 1
        val MESSAGE_OTHER_USER = 2
    }


    fun addMessage(message: Message)
    {
        messages.add(message)
        notifyDataSetChanged()
    }


        class MessageViewHolder(private val view: View):RecyclerView.ViewHolder(view)
        {
            fun bind(message : Message)
            {
                view.findViewById<TextView>(R.id.messageTV).text = message.message
            }

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {

        //we have 2 types of messages( differentiate between them)

        if(viewType.equals(MESSAGE_CURRENT_USER))
        {
            return MessageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_current_user_message,parent,false))
        }
        else
        {
            return MessageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_other_user_message,parent,false))

        }


    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])

    }

    override fun getItemViewType(position: Int): Int {

        if(messages[position].sentBy.equals(userId))
        {
            return MESSAGE_CURRENT_USER
        }
            return MESSAGE_OTHER_USER   // returned to viewType in onCreateViewHolder

        return super.getItemViewType(position)
    }

    override fun getItemCount() = messages.size

}