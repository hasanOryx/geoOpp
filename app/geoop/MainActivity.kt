package com.oryx.geoop

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.material.tabs.TabLayoutMediator
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import org.java_websocket.client.WebSocketClient
import org.json.JSONObject
import java.util.*

lateinit var appViewModel: AppViewModel
val assignmentsOpenedList = mutableListOf<Assignment>() //= mutableListOf()
val assignmentsCompletedList = mutableListOf<Assignment>() //= mutableListOf()
lateinit var recyclerView: RecyclerView
lateinit var layoutManager: RecyclerView.LayoutManager
lateinit var uniqueID: String
lateinit var reader: JSONObject
lateinit var task_reader: JSONObject
lateinit var calendar: Calendar
var client = SocketClient()
lateinit var location: Location
lateinit var locationRequest: LocationRequest
lateinit var fusedLocationProviderClient: FusedLocationProviderClient
lateinit var mWebSocketClient: WebSocketClient
var alarmMgr: AlarmManager? = null
lateinit var alarmIntent: PendingIntent

class MainActivity : AppCompatActivity(), AssignmentsFragment.OnFragmentInteractionListener,
    CompletedFragment.OnFragmentInteractionListener, InfoFragment.OnFragmentInteractionListener {

    lateinit var adapter: AssignmentsAdapter

    override fun onFragmentInteraction(uri: Uri) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
      //  setSupportActionBar(toolbar)

        adapter = AssignmentsAdapter(this)
        viewpager.adapter = AppViewPagerAdapter(supportFragmentManager, lifecycle)
        appViewModel = ViewModelProviders.of(this).get(AppViewModel::class.java)

        val taskDetailsHTML = "Fix the following:<br>" +
                "<br>" +
                "1- AC unit of <b>Kitchen</b><br>" +
                "2- AC unit of the main <u>bedroom</u><br><br>" +
                "Following is <span style='color:red'>FREE</span> if required:<br>" +
                "- Freon up to 1 KG <i>only</i><br><br>" +
                "If not found leave a note on the door<br>" +
                "<span style='color:red'><i>This is a sample run, If interested abou the app contact te developer</i></span>"

        val taskDetailsTEXT = "Fix the following:\n" +
                "1- AC unit of <b>Kitchen</b>\n" +
                "2- AC unit of the main bedroom\n\n" +
                "Following is FREE if required:\n" +
                "- Freon up to 1 KG only\n\n" +
                "If not found leave a note on the door"

        var assignment =Assignment(0, "sample task", taskDetailsHTML
                , 46.675, 24.721,
            "Amman", "new", "new","2020-0-01", "16:00",
            Calendar.getInstance().time.toString(), "Hasan", 966598840555
        )
        appViewModel.insert(assignment)
        adapter.notifyDataSetChanged()

        TabLayoutMediator(tabs, viewpager,
            TabLayoutMediator.OnConfigureTabCallback { tab, position ->
                // Styling each tab here
                tab.text = when (position){
                    0 -> "Assignments"
                    1 -> "Completed"
                    else -> "Info"
                }

            }).attach()

        uniqueID = UUID.randomUUID().toString()

        val requiredPermissions = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> listOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            else -> listOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        Dexter.withActivity(this)
            .withPermissions(
                requiredPermissions
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    /* ... */
                }


                override fun onPermissionsChecked(report: MultiplePermissionsReport) =/* ... */
                    if (report.isAnyPermissionPermanentlyDenied) {
                        toast("You should grant all permissions")
                    } else {
                        toast("All permissions granted")


                        val intent = Intent(this@MainActivity, LocationUpdateService::class.java)
                        //  intent.setAction(LocationUpdateService.ACTION_START_FOREGROUND_SERVICE)
                        intent.action = "ACTION_START_FOREGROUND_SERVICE"

                        //     connectWebSocket()appViewModel

                        client.connectWebSocket(this@MainActivity)
                        client.message.observe(this@MainActivity, androidx.lifecycle.Observer {
                            //  toast("received value: $it")
                            println("received value: $it")

                            if (it!!.isNotEmpty()) {
                                reader = JSONObject(it)
                                if (reader.has("command")) {
                                    println("command: ${reader["command"]}")
                                    //   val action =
                                    when (reader["command"]) {
                                        "Task" -> {
                                            println(reader);
                                            // task_reader = JSONObject(reader["Task"].toString())
                                            //   var id = reader["id"].toString().toInt()
                                            var task = reader["task"].toString()
                                            var details = reader["details"].toString()
                                            var lon = reader["lon"].toString().toDouble()
                                            var lat = reader["lat"].toString().toDouble()
                                            var address = reader["address"].toString()
                                            //    var datePicker = LocalDate.parse(reader["datePicker"].toString(), DateTimeFormatter.ISO_DATE)
                                            //    var timePicker = LocalTime.parse(reader["timePicker"].toString(), DateTimeFormatter.ofLocalizedTime(
                                            //        FormatStyle.SHORT))

                                            var datePicker = reader["datePicker"].toString()
                                            var timePicker = reader["timePicker"].toString()
                                            var status = reader["status"].toString()
                                            var customer = reader["customer"].toString()
                                            var mobile = reader["mobile"].toString().toLong()

                                            assignment = Assignment(
                                                0, task, details, lon, lat,
                                                address, status, status, datePicker, timePicker,
                                                Calendar.getInstance().time.toString(), customer, mobile
                                            )
                                            appViewModel.insert(assignment)
                                            adapter.notifyDataSetChanged()
                                        }
                                        "Confirmation" -> {
                                            // reader["confirm"].toString()
                                            println("Transaction: ${reader["trx_id"]}")
                                            for (trx in trxList) {
                                                when (trx.trx_id) {
                                                    reader["trx_id"].toString() -> {
                                                        trx.updated = reader["updated"] as Boolean
                                                    }
                                                    else -> {
                                                        println("transaction not found!")
                                                    }
                                                }
                                            }

                                        }
                                        "Notification" -> {
                                            println("Notification: ${reader["Notification"]}")
                                        }
                                        else -> println("nothing")
                                    }
                                    //      chaptersList.add(insertIndex, action)
                                    //      adapter.notifyItemInserted(insertIndex)
                                    println("Rep all users: ${appViewModel.allAssignments}")
                                    //  var assignment =Assignment(3,"Ali", "Mr")
                                    //  var assignment =Assignment(0, "Hasan", "Mr")
                                    // var assignment =Assignment(action)
                                    //  appViewModel.insert(assignment)
                                    // adapter.notifyDataSetChanged()
                                    println("Rep updated all users: ${appViewModel.allAssignments}")
                                }
                            }


                        })
                        startService(intent)
                        updateLocation()
                    }

            }).check()

    }

    fun updateLocation() {
        buildLocationRequest()
        if(checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent())

        // this.finish()
    }

    private fun getPendingIntent(): PendingIntent? {
        var intent = Intent(this, AppLocationService::class.java)
        intent.action = "oryx.track.UPDATE_LOCATION"
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000
            fastestInterval = 3000
            smallestDisplacement = 10f
        }
    }

    fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
