package com.radioyps.kidscomputermonitor

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Point
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe

import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.GooglePlayServicesUtil.getErrorDialog
import com.google.android.gms.security.ProviderInstaller
import com.google.android.material.snackbar.Snackbar
import com.radioyps.kidscomputermonitor.databinding.ActivityMainBinding
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import javax.net.ssl.SSLContext


/**
 * Show layout.activity_main and setup data binding.
 */
private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    /**
     * Inflate layout.activity_main and setup data binding.
     */
private lateinit var  viewModelLocal: MainViewModel
private lateinit var viewModelFactory: MainViewModel.MainViewModelFactory
//    private val viewModelLocal: MainViewModel by viewModels {
//        MainViewModel.MainViewModelFactory(
//            application
//        )
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        try {
            ProviderInstaller.installIfNeeded(applicationContext)
            val sslContext: SSLContext
            sslContext = SSLContext.getInstance("TLSv1.2")
            sslContext.init(null, null, null)
            sslContext.createSSLEngine()
        } catch (e: GooglePlayServicesRepairableException) {
            Log.v(TAG, "ProviderInstaller: GooglePlayServicesRepairableException")
            e.printStackTrace()
            val errorCode = e.connectionStatusCode
            Log.v(TAG, "ProviderInstaller: GooglePlayServicesRepairableException: error code: " + errorCode + " Start Error Dialog")
            GoogleApiAvailability.getInstance().getErrorDialog(this, errorCode,8989).show()

            Log.v(TAG, "ProviderInstaller: GooglePlayServicesRepairableException: error code: " + errorCode + " end Error Dialog")
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }

        /*
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
*/
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        binding.lifecycleOwner = this
        val rootLayout: ConstraintLayout = binding.rootLayout




        // Get MainViewModel by passing a database to the factory
        viewModelFactory = MainViewModel.MainViewModelFactory(application)
        val viewModel = ViewModelProviders
            .of(this, viewModelFactory)
            .get(MainViewModel::class.java)
        viewModelLocal = viewModel
        binding.viewModel = viewModel
        binding.clock.visibility = View.VISIBLE
        binding.clock.setTextColor(resources.getColor(R.color.clockColor))

        binding.timestamp.visibility = View.VISIBLE
        binding.timestamp.setTextColor(resources.getColor(R.color.timeStampColor))
//        binding.clock.setTextSize(2)











        viewModel.imageBitmap.observe(this) { bitmap ->

            if(bitmap != null){
                binding.imageView.setImageBitmap(bitmap)
            }
        }

        viewModel.imageTimeStamp.observe(this) { timeStamp ->

            if(timeStamp != null){
                binding.timestamp.setText(timeStamp)
            }
        }

        viewModel.timeOnScreen.observe(this) { time ->

            if(time != null){
                binding.clock.setText(time)
            }
        }


        viewModel.snackbar.observe(this) { text ->
            text?.let {
                Snackbar.make(rootLayout, text, Snackbar.LENGTH_SHORT).show()
                viewModel.onSnackbarShown()
            }
        }




        val metrics = DisplayMetrics()
        (this as Activity).windowManager.defaultDisplay.getMetrics(metrics)
        val screenWidth = metrics.widthPixels
        val screenHeight = metrics.heightPixels
        var size = Point()
        (this as Activity).windowManager.defaultDisplay.getSize(size)

        Log.v(
            TAG,
            "onCreate()>> : screenSize Width  ${size.x},  screenSize Height ${size.y} "
        )

        Log.v(
            TAG,
            "onCreate()>> : screenWidth  ${screenWidth},  screenHeight ${screenHeight} "
        )
        viewModel.setImageViewDimention(screenWidth, screenHeight)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() === R.id.only_kids) {
            Toast.makeText(
                this,
                "Only show two Kids computers",
                Toast.LENGTH_SHORT
            ).show()
            viewModelLocal.setOnlyShowKidsComputer()
        }else if (item.getItemId() === R.id.all_computers) {
            Toast.makeText(
                this,
                "Show all computers",
                Toast.LENGTH_SHORT
            ).show()
            viewModelLocal.clearOnlyShowKidsComputer()
         }else if (item.getItemId() === R.id.photos) {
            Toast.makeText(
                this,
                "Show photos",
                Toast.LENGTH_SHORT
            ).show()
            viewModelLocal.setShowPhotoOnly()
        }
        return true
    }


}
