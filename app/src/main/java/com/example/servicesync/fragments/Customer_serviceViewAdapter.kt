package com.example.servicesync.fragments

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.servicesync.R
import com.google.android.material.snackbar.Snackbar

class Customer_serviceViewAdapter(
    private var customers: List<CustomerModel>,
    private val itemClickListener: (CustomerModel, Boolean) -> Unit
) : RecyclerView.Adapter<Customer_serviceViewAdapter.CustomerViewHolder>() {

    private var filteredCustomers: List<CustomerModel> = customers
        get() = field
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.customer_detail, parent, false)
        return CustomerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = filteredCustomers[position]
        holder.bind(customer)
    }

    override fun getItemCount(): Int {
        return filteredCustomers.size
    }

    inner class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textView3)
        private val phoneNumberTextView: TextView = itemView.findViewById(R.id.textView9)
        private val cityTextView: TextView = itemView.findViewById(R.id.textView10)

        fun bind(customer: CustomerModel) {
            nameTextView.text = customer.name
            phoneNumberTextView.text = customer.phoneNumber
            cityTextView.text = customer.city

            itemView.setOnClickListener {
                itemClickListener(customer, true) // Indicate visibility change to true
            }
        }
    }

    fun updateList(newCustomers: List<CustomerModel>) {
        customers = newCustomers
        filteredCustomers = newCustomers
    }

    fun filter(query: String) {
        filteredCustomers = if (query.isEmpty()) {
            customers
        } else {
            customers.filter {
                it.name.contains(query, ignoreCase = true) || it.city.contains(query, ignoreCase = true)
            }
        }
    }

    fun getFilteredSize(): Int {
        return filteredCustomers.size
    }
}
