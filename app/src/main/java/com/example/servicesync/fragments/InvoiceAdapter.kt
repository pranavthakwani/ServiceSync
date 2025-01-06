package com.example.servicesync.fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.servicesync.R

class InvoiceAdapter(
    private var invoiceList: List<Invoice>,
    private val context: Context,
    private val itemClickListener: (Invoice) -> Unit
) : RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder>() {

    private var filteredInvoiceList: List<Invoice> = invoiceList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.invoice_list, parent, false)
        return InvoiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        val invoice = filteredInvoiceList[position]
        holder.customerName.text = invoice.customerName
        holder.totalAmount.text = invoice.totalAmount
        holder.dueDate.text = invoice.dueDate
        holder.itemView.setOnClickListener {
            itemClickListener(invoice)
        }
    }

    override fun getItemCount(): Int {
        return filteredInvoiceList.size
    }

    fun updateList(newInvoiceList: List<Invoice>) {
        invoiceList = newInvoiceList
        filteredInvoiceList = newInvoiceList
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredInvoiceList = if (query.isEmpty()) {
            invoiceList
        } else {
            invoiceList.filter {
                it.customerName.contains(query, ignoreCase = true) ||
                        it.totalAmount.contains(query, ignoreCase = true) ||
                        it.dueDate.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    class InvoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val customerName: TextView = itemView.findViewById(R.id.customerInvoiceField)
        val totalAmount: TextView = itemView.findViewById(R.id.ammountField)
        val dueDate: TextView = itemView.findViewById(R.id.duedateInvoice)
    }
}
