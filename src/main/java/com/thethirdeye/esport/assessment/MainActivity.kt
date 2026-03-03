package com.thethirdeye.esport.assessment

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.thethirdeye.esport.assessment.databinding.ActivityMainBinding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import yuku.ambilwarna.AmbilWarnaDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupCustomization()
        fetchResume()
        getLocation()
    }

    // ---------------- FETCH RESUME ----------------

    private fun fetchResume() {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://expressjs-api-resume-random.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ResumeApi::class.java)

        api.getResume("Sonu").enqueue(object : Callback<ResumeModel> {

            override fun onResponse(
                call: Call<ResumeModel>,
                response: Response<ResumeModel>
            ) {

                if (response.isSuccessful && response.body() != null) {

                    val data = response.body()

                    val resumeText = """
                        Name: ${data?.name}
                        
                        Skills:
                        ${data?.skills?.joinToString("\n")}
                        
                        Projects:
                        ${data?.projects?.joinToString("\n")}
                    """.trimIndent()

                    binding.tvResume.text = resumeText

                } else {
                    binding.tvResume.text =
                        "Server Error!\nResponse Code: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<ResumeModel>, t: Throwable) {

                binding.tvResume.text =
                    "Network Error!\nPlease check your internet connection.\n\nDetails: ${t.localizedMessage}"
            }
        })
    }

    // ---------------- CUSTOMIZATION ----------------

    private fun setupCustomization() {

        binding.seekBarFont.progress = 16

        binding.seekBarFont.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvResume.textSize = progress.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.btnFontColor.setOnClickListener {
            openColorPicker(true)
        }

        binding.btnBgColor.setOnClickListener {
            openColorPicker(false)
        }
    }

    private fun openColorPicker(isFont: Boolean) {

        val colorPicker = AmbilWarnaDialog(
            this,
            Color.BLACK,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {

                    if (isFont) {
                        binding.tvResume.setTextColor(color)
                    } else {
                        binding.tvResume.setBackgroundColor(color)
                    }
                }

                override fun onCancel(dialog: AmbilWarnaDialog?) {}
            })

        colorPicker.show()
    }

    // ---------------- LOCATION ----------------

    private fun getLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                binding.tvLocation.text =
                    "Lat: ${it.latitude}, Long: ${it.longitude}"
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation()
        }
    }
}