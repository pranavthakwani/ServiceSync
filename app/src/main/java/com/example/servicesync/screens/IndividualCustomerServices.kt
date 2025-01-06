package com.example.servicesync.screens

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.servicesync.R
import com.example.servicesync.fragments.ServiceAdapter
import com.example.servicesync.fragments.ServiceModel

class IndividualCustomerServices : AppCompatActivity(), ServiceAdapter.OnItemClickListener {

    private lateinit var backBTN: ImageButton
    private lateinit var customerNameField: TextView
    private lateinit var dueDateField: TextView
    private lateinit var totalAmountField: TextView
    private lateinit var searchView3: SearchView
    private lateinit var recyclerViewICS: RecyclerView
    private lateinit var serviceAdapter: ServiceAdapter
    private lateinit var serviceList: MutableList<ServiceModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_individual_customer_services)

        backBTN = findViewById(R.id.backindividualCustomerServicesBTN)
        customerNameField = findViewById(R.id.customerNameFieldICS)
        dueDateField = findViewById(R.id.dueDateField)
        totalAmountField = findViewById(R.id.totalAmmountFieldICS)
        searchView3 = findViewById(R.id.searchView3)
        recyclerViewICS = findViewById(R.id.recylerViewICS)

        backBTN.setOnClickListener {
            finish()
        }

        // Retrieve the data passed via the Intent
        val customerName = intent.getStringExtra("CUSTOMER_NAME")
        val dueDate = intent.getStringExtra("DUE_DATE")
        val totalAmount = intent.getStringExtra("TOTAL_AMOUNT")

        // Set the data to the TextViews
        customerNameField.text = customerName
        dueDateField.text = dueDate
        totalAmountField.text = totalAmount

        // Initialize the service list (this would typically come from a database or API)
        serviceList = mutableListOf(
            ServiceModel("Service InfoTech", "2024-01-01", "2024-02-01"),
            ServiceModel("Ds infoways", "2024-03-01", "2024-04-01"),
            ServiceModel("ABC Solutions", "2024-05-01", "2024-06-01"),
            ServiceModel("XYZ Technologies", "2024-07-01", "2024-08-01"),
            ServiceModel("Tech Solutions Inc.", "2024-09-01", "2024-10-01"),
            ServiceModel("Global IT Services", "2024-11-01", "2024-12-01"),
            ServiceModel("DataTech Enterprises", "2025-01-01", "2025-02-01"),
            ServiceModel("Software Solutions Ltd.", "2025-03-01", "2025-04-01"),
            ServiceModel("Alpha Systems", "2025-05-01", "2025-06-01"),
            ServiceModel("Beta Technologies", "2025-07-01", "2025-08-01"),
            ServiceModel("Gamma Innovations", "2025-09-01", "2025-10-01"),
            ServiceModel("Delta Solutions", "2025-11-01", "2025-12-01")
            // Add more service models as needed
        )


        // Set up the RecyclerView
        serviceAdapter = ServiceAdapter(serviceList, this, this)
        recyclerViewICS.layoutManager = LinearLayoutManager(this)
        recyclerViewICS.adapter = serviceAdapter

        // Set up the SearchView to filter the list
        searchView3.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                serviceAdapter.filter(newText ?: "")
                return true
            }
        })
    }

    // In IndividualCustomerServices
    override fun onItemClick(service: ServiceModel) {
        // Handle the item click, e.g., navigate to ServiceDetails with data from individualCustomerServiceDetails.kt
        val intent = Intent(this, ServiceDetails::class.java).apply {
            putExtra("individual_customer_name", service.customerName)
            putExtra("individual_service_date", service.serviceDate)
            putExtra("individual_due_date", service.dueDate)
            putExtra("FROM_INDIVIDUAL_CUSTOMER_SERVICES", true)
        }
        startActivity(intent)
    }

}
