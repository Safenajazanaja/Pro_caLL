package com.example.loginmvvm.presentation.repair


import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.loginmvvm.R
import com.example.loginmvvm.base.BaseFragment
import com.example.loginmvvm.base.onItemSelected
import com.example.loginmvvm.data.models.SeletTypejobModel
import com.example.loginmvvm.data.request.RepairRequest
import com.example.loginmvvm.presentation.repair.engineer.SpinnertypeAdapter
import com.example.loginmvvm.utils.awaitLastLocation
import com.example.loginmvvm.utils.getGoogleMap
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.frament_call.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import java.util.*


@ExperimentalCoroutinesApi
class RepairFragment : BaseFragment(R.layout.frament_call) {

    private lateinit var viewModel: RepairViewModel
    private var mMarker: Marker? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private lateinit var type: SeletTypejobModel
    private var name: String? = null
    private var id: Int? = null


    //Calendar
    private var mCalendar: Calendar? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val userId = context?.getSharedPreferences(
            "file",
            AppCompatActivity.MODE_PRIVATE
        )?.getInt("id", 0)
        viewModel = ViewModelProvider(this).get(RepairViewModel::class.java)
//        viewModel.repair()
        viewModel.toast.observe(this, { str ->
            Toast.makeText(context, "$str", Toast.LENGTH_SHORT).show()
        })

        btCalendar.setOnClickListener {
            val calendar = mCalendar ?: Calendar.getInstance()

            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]

            calendar.add(Calendar.DATE, 0)
            val dateDialog = DatePickerDialog(
                requireContext(),
                { view, year, month, dayOfMonth ->
                    Toast.makeText(
                        context,
                        "$year/${month.plus(1)}/$dayOfMonth",
                        Toast.LENGTH_SHORT
                    ).show()
                    re_date.setText("$dayOfMonth/${month.plus(1)}/$year")



                    val calendar = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)


                    }
                    mCalendar = calendar


                },
                year,
                month,
                day,
            )

            calendar.add(Calendar.DATE, 0)
            dateDialog.datePicker.minDate = DateTime.now().millis
//                .show()
            dateDialog.show()


        }

        Bt_ok.setOnClickListener {
            val Abode = re_abode.text.toString()
            val RepairList = re_joblist.text.toString()
            val Repair = RepairRequest(
                userid =userId,
                abode = Abode,
                repair_list=RepairList,
                date = mCalendar?.timeInMillis,
                latitudeval = latitude,
                longitude = longitude,
                idtypejob = id
            )
            viewModel.repair(Repair)
            val intent = Intent(context, ConfirmActivity::class.java).apply {
                putExtra("user_id", userId)
                putExtra("abode", Abode)
                putExtra("repair_list", RepairList)
                putExtra("date", Repair?.date)
                putExtra("latitude", latitude)
                putExtra("longitude", longitude)
                putExtra("type_job",id)
            }
            startActivity(intent)

//            Toast.makeText(context, "${mCalendar?.timeInMillis}", Toast.LENGTH_SHORT).show()
        }

        val mapFragment = this.childFragmentManager
            .findFragmentById(R.id.maps) as SupportMapFragment?

        re_joblist.addTextChangedListener {
            // TODO: 18/2/2564
            Log.d("TAG", "onActivityCreated: ${it.toString()}")
        }



        setSpinnertypejob()

        MainScope().launch {
//            Log.d("###", "onActivityCreated: 1")
            val locationProviderClient = LocationServices
                .getFusedLocationProviderClient(requireActivity())

//            Log.d("###", "onActivityCreated: 2")
            val googleMap = mapFragment?.getGoogleMap()

//            Log.d("###", "onActivityCreated: 3")
            val location = locationProviderClient.awaitLastLocation()

//            Log.d("###", "onActivityCreated: 4")
            latitude = location.latitude
            longitude = location.longitude

            mMarker = googleMap?.addMarker(MarkerOptions().position(LatLng(location.latitude,location.longitude)))

            // real time
//            locationProviderClient.locationFlow().collect {
            Toast.makeText(context, "${latitude}, ${longitude}", Toast.LENGTH_SHORT).show()
//            }

            setMapLongClick(googleMap)

            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude,location.longitude), 15f)
            googleMap?.animateCamera(cameraUpdate)
            googleMap?.isMyLocationEnabled = true

        }

    }

    private fun setSpinnertypejob() {
        viewModel.typejob.observe(this,{selectjob ->
//            val list=viewModel.seletjob()as MutableList<SeletTypejobModel>
            bar_spinner_typejob.adapter=SpinnertypeAdapter(requireContext(),
                selectjob as MutableList<SeletTypejobModel>
            )
            bar_spinner_typejob.onItemSelected<SeletTypejobModel> {
                type=it
                id=it.id
                name=it.type
            }
        })

    }

    private fun setMapLongClick(googleMap: GoogleMap?) {
        googleMap?.setOnMapClickListener { latLng ->
            val snippets = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            mMarker?.remove()
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            mMarker = googleMap.addMarker(MarkerOptions().position(latLng))


        }

    }

}
