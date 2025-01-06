package com.example.servicesync.screens

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.servicesync.R

data class NotificationModel(val companyName: String, val message: String, val date: String)

class NotificationAdapter(
    private var notifications: MutableList<NotificationModel>,
    private val context: Context,
    private val onItemLongPressed: (Int) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    private val selectedPositions = mutableSetOf<Int>()

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val companyNameTextView: TextView = itemView.findViewById(R.id.textView88)
        val messageTextView: TextView = itemView.findViewById(R.id.textView89)
        val dateTextView: TextView = itemView.findViewById(R.id.textView92)
        val layout: View = itemView.findViewById(R.id.notificationItemLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_box_design, parent, false)
        return NotificationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val currentNotification = notifications[position]
        holder.companyNameTextView.text = currentNotification.companyName
        holder.messageTextView.text = currentNotification.message
        holder.dateTextView.text = currentNotification.date

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ServiceDetails::class.java).apply {
                putExtra("customer_name", currentNotification.companyName)
            }
            context.startActivity(intent)
        }

        // Highlight the item if it's selected
        if (selectedPositions.contains(position)) {
            holder.layout.setBackgroundResource(R.color.highlight) // Highlight color
        } else {
            holder.layout.setBackgroundResource(R.drawable.bg_box) // Default color
        }

        // Toggle selection on long press
        holder.itemView.setOnLongClickListener {
            onItemLongPressed(position)
            toggleSelection(position)
            true
        }
    }

    override fun getItemCount() = notifications.size

    fun updateNotifications(newNotifications: List<NotificationModel>) {
        notifications = newNotifications.toMutableList()
        notifyDataSetChanged()
    }

    private fun toggleSelection(position: Int) {
        if (selectedPositions.contains(position)) {
            selectedPositions.remove(position)
        } else {
            selectedPositions.add(position)
        }
        notifyItemChanged(position)
    }

    fun removeNotification(position: Int) {
        if (position >= 0 && position < notifications.size) {
            notifications.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, notifications.size)
            selectedPositions.remove(position)
        }
    }

    fun getSelectedPositions(): Set<Int> {
        return selectedPositions
    }
}
