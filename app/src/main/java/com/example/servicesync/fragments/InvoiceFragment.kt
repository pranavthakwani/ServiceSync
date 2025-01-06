package com.example.servicesync.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.servicesync.R
import com.example.servicesync.screens.IndividualCustomerServices

class InvoiceFragment : Fragment() {

    private lateinit var invoiceAdapter: InvoiceAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_invoice, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewInvoice)
        searchView = view.findViewById(R.id.searchView2)

        initRecyclerView()
        setupSearchView()
        return view
    }

    private fun initRecyclerView() {
        val invoiceList = getInvoiceList()
        invoiceAdapter = InvoiceAdapter(invoiceList, requireContext()) { invoice ->
            val intent = Intent(requireContext(), IndividualCustomerServices::class.java).apply {
                putExtra("CUSTOMER_NAME", invoice.customerName)
                putExtra("DUE_DATE", invoice.dueDate)
                putExtra("TOTAL_AMOUNT", invoice.totalAmount)
            }
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = invoiceAdapter
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { invoiceAdapter.filter(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { invoiceAdapter.filter(it) }
                return true
            }
        })
    }

    private fun getInvoiceList(): List<Invoice> {
        return listOf(
            Invoice("Dhaval", "₹200.00", "2024-06-15"),
            Invoice("Keval", "₹150.00", "2024-06-20"),
            Invoice("Pandit", "₹350.00", "2024-07-05"),
            Invoice("Smith", "₹250.00", "2024-07-10"),
            Invoice("Johnson", "₹180.00", "2024-07-15"),
            Invoice("Brown", "₹300.00", "2024-07-20"),
            Invoice("Lee", "₹280.00", "2024-07-25"),
            Invoice("Garcia", "₹400.00", "2024-08-05")
            // Add more invoices as needed
        )

    }
}
