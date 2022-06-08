package com.radioyps.kidscomputermonitor

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe

import com.google.android.material.snackbar.Snackbar
import com.radioyps.kidscomputermonitor.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.InputStream


/**
 * Show layout.activity_main and setup data binding.
 */
private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    /**
     * Inflate layout.activity_main and setup data binding.
     */
    private lateinit var  viewModelLocal: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        binding.lifecycleOwner = this
        val rootLayout: ConstraintLayout = binding.rootLayout




        // Get MainViewModel by passing a database to the factory

        val viewModel = ViewModelProviders
            .of(this)
            .get(MainViewModel::class.java)
        viewModelLocal = viewModel
        binding.viewModel = viewModel
        binding.clock.visibility = View.GONE





        viewModel.currentComputerName.observe(this) { value ->
            value?.let {
                binding.currentComputer.setText(value)
            }
        }

        viewModel.currentComputerOwner.observe(this) { value ->
            value?.let {
                Log.v(TAG, "currentImagename: ${it}")
                var tint = 0
                if (it.contains("ZIYI", ignoreCase = true)) {
                    Log.v(TAG, "currentImagename: ${it}, hit ziyi")
                    tint = ContextCompat.getColor(applicationContext, R.color.highlightGreen);
                    ImageViewCompat.setImageTintList(binding.kid1Status, ColorStateList.valueOf(tint))

                    tint = ContextCompat.getColor(applicationContext, R.color.primaryTextColor);
                    ImageViewCompat.setImageTintList(binding.kid2Status, ColorStateList.valueOf(tint));
                }else if (it.contains("ZIHAN", ignoreCase = true)){
                    Log.v(TAG, "currentImagename: ${it}, hit zihan")
                    tint = ContextCompat.getColor(applicationContext, R.color.primaryTextColor);
                    ImageViewCompat.setImageTintList(binding.kid1Status, ColorStateList.valueOf(tint))

                    tint = ContextCompat.getColor(applicationContext, R.color.highlightGreen);
                    ImageViewCompat.setImageTintList(binding.kid2Status, ColorStateList.valueOf(tint));
                }else{
                    Log.v(TAG, "currentImagename: ${it}, but not hit")
                }
            }
        }

        viewModel.connectStatus.observe(this) { value ->
            value?.let {
                Log.v(TAG, "connectStatus: ${value}")
                if (it.contains("Ziyi", ignoreCase = true)){
                    if (it.contains("disconnected")){
                        binding.kid1Status.setImageResource(R.drawable.ic_connection_error)

                    }else{
                        binding.kid1Status.setImageResource(R.drawable.round_cast_connected_20)
                    }
                }
                if (it.contains("Zihan",ignoreCase = true)){
                    if (it.contains("disconnected")){

                        binding.kid2Status.setImageResource(R.drawable.ic_connection_error)
                    }else{
                        binding.kid2Status.setImageResource(R.drawable.round_cast_connected_20)
                    }
                }
            }
        }



        viewModel.snackbar.observe(this) { text ->
            text?.let {
                Snackbar.make(rootLayout, text, Snackbar.LENGTH_SHORT).show()
                viewModel.onSnackbarShown()
            }
        }


        viewModel.isShowOnlyKidsComputer.observe(this) { isOnlyShowKidsComputer ->
            isOnlyShowKidsComputer?.let {
                if (isOnlyShowKidsComputer?:false){
                    binding.choice.setText("KIDS ONLY")
                }else{
                    binding.choice.setText("ALL")
                }
            }
        }


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
        }
        return true
    }


}
