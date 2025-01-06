package com.example.servicesync.fragments

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.servicesync.R
import com.example.servicesync.screens.ServiceDetails

class ServiceAdapter(
    private var serviceList: List<ServiceModel>,
    private val context: Context,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    private var filteredServiceList: List<ServiceModel> = serviceList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.service_detail, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = filteredServiceList[position]
        holder.customerName.text = service.customerName
        holder.serviceDate.text = service.serviceDate
        holder.dueDate.text = service.dueDate
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(service)
        }
    }

    override fun getItemCount(): Int {
        return filteredServiceList.size
    }

    fun updateList(newServiceList: List<ServiceModel>) {
        serviceList = newServiceList
        filteredServiceList = newServiceList
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredServiceList = if (query.isEmpty()) {
            serviceList
        } else {
            serviceList.filter {
                it.customerName.contains(query, ignoreCase = true) ||
                        it.serviceDate.contains(query, ignoreCase = true) ||
                        it.dueDate.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val customerName: TextView = itemView.findViewById(R.id.customerNameTXT)
        val serviceDate: TextView = itemView.findViewById(R.id.serviceDateTXT)
        val dueDate: TextView = itemView.findViewById(R.id.dueDateTXT)
    }

    interface OnItemClickListener {
        fun onItemClick(service: ServiceModel)
    }
}
