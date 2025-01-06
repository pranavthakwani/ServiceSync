package com.example.servicesync.fragments

import android.content.Context
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
import com.example.servicesync.screens.AddService
import com.example.servicesync.screens.NotificationActivity
import com.example.servicesync.screens.ServiceDetails

class ServiceFragment : Fragment(), ServiceAdapter.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var serviceAdapter: ServiceAdapter
    private lateinit var serviceSearchView: SearchView
    private lateinit var placeholderLayout: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_service, container, false)

        placeholderLayout = view.findViewById(R.id.emptyServiceLayout)
        val addServiceBTN = view.findViewById<ImageButton>(R.id.addServiceBTN)
        addServiceBTN.setOnClickListener {
            Toast.makeText(requireContext(), "ADD Button clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, AddService::class.java)
            startActivity(intent)
        }

        recyclerView = view.findViewById(R.id.recyclerView3)
        serviceSearchView = view.findViewById(R.id.serviceSearchView)
        setupRecyclerView(view.context)
        setupSearchView()

        val serviceList = listOf(
            ServiceModel("Service 1", "2024-05-09", "2024-05-16"),
            ServiceModel("Service 2", "2024-05-10", "2024-05-17"),
            ServiceModel("Service 3", "2024-05-11", "2024-05-18"),
            ServiceModel("Service 4", "2024-05-12", "2024-05-19"),
            ServiceModel("Service 5", "2024-05-13", "2024-05-20")
            // Add more ServiceModel objects as needed
        )
        updateServiceList(serviceList)

        return view
    }

    private fun setupRecyclerView(context: Context) {
        serviceAdapter = ServiceAdapter(emptyList(), context, this)
        recyclerView.adapter = serviceAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setupSearchView() {
        serviceSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                serviceAdapter.filter(newText.orEmpty())
                return true
            }
        })
    }

    private fun updateServiceList(services: List<ServiceModel>) {
        if (services.isEmpty()) {
            // Show placeholder layout if service list is empty
            recyclerView.visibility = View.GONE
            placeholderLayout.visibility = View.VISIBLE
        } else {
            // Show recyclerView if service list is not empty
            recyclerView.visibility = View.VISIBLE
            placeholderLayout.visibility = View.GONE
            serviceAdapter.updateList(services)
        }
    }

    override fun onItemClick(service: ServiceModel) {
        val intent = Intent(context, ServiceDetails::class.java).apply {
            putExtra("service_date", service.serviceDate)
            putExtra("customer_name", service.customerName)
            putExtra("due_date", service.dueDate)
        }
        startActivity(intent)
    }
}
