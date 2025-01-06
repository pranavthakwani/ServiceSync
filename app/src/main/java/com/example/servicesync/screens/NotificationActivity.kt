package com.example.servicesync.screens

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.servicesync.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class NotificationActivity : AppCompatActivity() {

    private lateinit var backBTN: ImageButton
    private lateinit var notificationRecyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var deleteNotificationButton: ImageButton

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        backBTN = findViewById(R.id.backNotificationBTN)
        notificationRecyclerView = findViewById(R.id.notificationRecylerView)
        deleteNotificationButton = findViewById(R.id.deleteNotificationBTN)

        backBTN.setOnClickListener {
            finish()
        }

        notificationAdapter = NotificationAdapter(mutableListOf(), this) { position ->
            // Long press listener
            toggleDeleteButtonVisibility()
        }
        notificationRecyclerView.layoutManager = LinearLayoutManager(this)
        notificationRecyclerView.adapter = notificationAdapter
        deleteNotificationButton.visibility = View.INVISIBLE

        deleteNotificationButton.setOnClickListener {
            val selectedPositions = notificationAdapter.getSelectedPositions().toList()
            selectedPositions.sortedDescending().forEach { position ->
                notificationAdapter.removeNotification(position)
            }
            deleteNotificationButton.visibility = View.INVISIBLE
        }

        // Create the notification channel
        createNotificationChannel()

        // Check notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            } else {
                loadAndShowNotifications()
            }
        } else {
            loadAndShowNotifications()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Service Notifications"
            val descriptionText = "Channel for service notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("service_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun loadAndShowNotifications() {
        // Load notifications (this could be from a local database, server, etc.)
        val notifications = listOf(
            NotificationModel("Company A", "Service 7 is due", "01-06-2024"),
            NotificationModel("Company B", "Service 6 has been added", "02-06-2024"),
            NotificationModel("Company C", "Service 8 has been added", "03-06-2024"),
            NotificationModel("Company D", "Service 5 has been added", "04-06-2024"),
            NotificationModel("Company E", "Service 4 has been added", "05-06-2024"),
            NotificationModel("Company F", "Service 3 has been added", "06-06-2024"),
            NotificationModel("Company G", "Service 2 has been added", "07-06-2024"),
            NotificationModel("Company H", "Service 1 has been added", "08-06-2024"),
            NotificationModel("Company I", "Service 9 has been added", "09-06-2024"),
            NotificationModel("Company J", "Service 10 has been added", "10-06-2024"),
            NotificationModel("Company K", "Service 11 has been added", "11-06-2024"),
            NotificationModel("Company L", "Service 12 has been added", "12-06-2024"),
            NotificationModel("Company M", "Service 13 has been added", "13-06-2024")
        )

        val enhancedNotifications = enhanceNotificationsWithDueDates(notifications)
        notificationAdapter.updateNotifications(enhancedNotifications)
        showGroupedNotification(enhancedNotifications)
    }

    private fun enhanceNotificationsWithDueDates(notifications: List<NotificationModel>): List<NotificationModel> {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = Calendar.getInstance()
        val today = currentDate.time

        val enhancedNotifications = mutableListOf<NotificationModel>()

        for (notification in notifications) {
            val dueDate = dateFormat.parse(notification.date)
            if (dueDate != null) {
                val dueCalendar = Calendar.getInstance().apply { time = dueDate }
                val diffInDays = getDifferenceInDays(currentDate, dueCalendar)

                val message = when {
                    diffInDays == 0L -> "Service Due is today"
                    diffInDays == 1L -> "Due date is tomorrow"
                    diffInDays == 2L -> "Due date in 2 days"
                    diffInDays == 3L -> "Due date in 3 days"
                    else -> continue // Skip notifications more than 3 days before the due date
                }

                // Override old message
                enhancedNotifications.add(NotificationModel(notification.companyName, message, notification.date))
            }
        }

        // Sort the enhanced notifications in ascending order by date
        enhancedNotifications.sortBy { dateFormat.parse(it.date) }

        return enhancedNotifications
    }

    private fun getDifferenceInDays(currentDate: Calendar, dueDate: Calendar): Long {
        // Set both calendars to midnight to compare dates only, without considering the time
        currentDate.set(Calendar.HOUR_OF_DAY, 0)
        currentDate.set(Calendar.MINUTE, 0)
        currentDate.set(Calendar.SECOND, 0)
        currentDate.set(Calendar.MILLISECOND, 0)

        dueDate.set(Calendar.HOUR_OF_DAY, 0)
        dueDate.set(Calendar.MINUTE, 0)
        dueDate.set(Calendar.SECOND, 0)
        dueDate.set(Calendar.MILLISECOND, 0)

        val diffInMillis = dueDate.timeInMillis - currentDate.timeInMillis
        return TimeUnit.MILLISECONDS.toDays(diffInMillis)
    }

    private fun showGroupedNotification(notifications: List<NotificationModel>) {
        val notificationManager = NotificationManagerCompat.from(this)

        notifications.forEachIndexed { index, notification ->
            val intent = Intent(this, ServiceDetails::class.java).apply {
                putExtra("companyName", notification.companyName)
            }
            val pendingIntent = PendingIntent.getActivity(
                this, index, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(this, "service_channel")
                .setSmallIcon(R.drawable.service)
                .setContentTitle(notification.companyName)
                .setContentText(notification.message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notificationManager.notify(index, builder.build())
        }
    }

    private fun toggleDeleteButtonVisibility() {
        deleteNotificationButton.visibility = if (notificationAdapter.getSelectedPositions().isNotEmpty()) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_NOTIFICATION_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted, load and show notifications
                    loadAndShowNotifications()
                }
            }
        }
    }
}
