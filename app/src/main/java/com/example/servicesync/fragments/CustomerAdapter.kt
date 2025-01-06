package com.example.servicesync.fragments

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.servicesync.R
import com.example.servicesync.screens.CustomerDetails

class CustomerAdapter(private var customers: List<CustomerModel>, private val context: Context) : RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>() {

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

    inner class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val nameTextView: TextView = itemView.findViewById(R.id.textView3)
        private val phoneNumberTextView: TextView = itemView.findViewById(R.id.textView9)
        private val cityTextView: TextView = itemView.findViewById(R.id.textView10)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(customer: CustomerModel) {
            nameTextView.text = customer.name
            phoneNumberTextView.text = customer.phoneNumber
            cityTextView.text = customer.city
        }

        override fun onClick(v: View?) {
            val intent = Intent(context, CustomerDetails::class.java).apply {
                putExtra("customer_name", filteredCustomers[adapterPosition].name)
                putExtra("customer_phone", filteredCustomers[adapterPosition].phoneNumber)
                putExtra("customer_city", filteredCustomers[adapterPosition].city)
            }
            context.startActivity(intent)
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
                it.name.contains(query, ignoreCase = true) ||
                        it.city.contains(query, ignoreCase = true) ||
                              it.phoneNumber.contains(query, ignoreCase = true)
            }
        }
    }
}
