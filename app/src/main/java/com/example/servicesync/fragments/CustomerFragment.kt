package com.example.servicesync.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.servicesync.R
import com.example.servicesync.screens.AddCustomer
import com.example.servicesync.screens.NotificationActivity

class CustomerFragment : Fragment() {

    private lateinit var noCustomerLayout: ConstraintLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomerAdapter
    private lateinit var searchView: SearchView
    private var customers: List<CustomerModel> = emptyList()
    private lateinit var notificationCounter: TextView
    private var notificationCount = 0
    private lateinit var notificationBTN: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_customer, container, false)

        noCustomerLayout = view.findViewById(R.id.epmtyCustomerLayout)
        val addButton = view.findViewById<ImageButton>(R.id.add)
        addButton.setOnClickListener {
            val intent = Intent(requireContext(), AddCustomer::class.java)
            startActivity(intent)
            Toast.makeText(requireContext(), "ADD Button clicked", Toast.LENGTH_SHORT).show()
        }

        notificationBTN = view.findViewById(R.id.notificationBTN)
        notificationCounter = view.findViewById(R.id.notificationCounter)
        notificationCounter.visibility = View.GONE

        notificationBTN.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }

        searchView = view.findViewById(R.id.searchView)
        recyclerView = view.findViewById(R.id.recyclerview1)
        setupRecyclerView()
        setupSearchView()
        receiveNotification()

        // Adding sample data to the adapter
        val customersList = listOf(
            CustomerModel("Dhaval", "+919879732575", "New York"),
            CustomerModel("Pranav", "+919313717527", "Los Angeles"),
            CustomerModel("Paa", "+919825397116", "Chicago"),
            CustomerModel("Keval", "+919429919302", "New York"),
            CustomerModel("Tiwi", "987-654-3210", "Los Angeles"),
            CustomerModel("Dhaval", "456-789-1230", "Chicago"),
            CustomerModel("Pranav", "123-456-7890", "New York"),
            CustomerModel("Kalu", "987-654-3210", "Los Angeles"),
            CustomerModel("Pendi", "456-789-1230", "Chicago"),
            CustomerModel("Tiwi", "123-456-7890", "New York"),
            CustomerModel("Dhaval", "987-654-3210", "Los Angeles"),
            CustomerModel("Pranav", "456-789-1230", "Chicago")
            // Add more sample data if needed
        )
        updateCustomers(customersList)

        return view
    }

    private fun receiveNotification() {
        notificationCount++
        updateNotificationCounter()
    }

    private fun updateNotificationCounter() {
        if (notificationCount > 0) {
            notificationCounter.text = notificationCount.toString()
            notificationCounter.visibility = View.VISIBLE
        } else {
            notificationCounter.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        adapter = CustomerAdapter(customers, requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    adapter.filter(newText)
                    return true
                }
                return false
            }
        })
    }

    private fun updateCustomers(newCustomers: List<CustomerModel>) {
        customers = newCustomers
        if (customers.isEmpty()) {
            // Show placeholder layout if customer list is empty
            recyclerView.visibility = View.GONE
            noCustomerLayout.visibility = View.VISIBLE
        } else {
            // Show recyclerView if customer list is not empty
            recyclerView.visibility = View.VISIBLE
            noCustomerLayout.visibility = View.GONE
            adapter.updateList(customers)
        }
    }
}
