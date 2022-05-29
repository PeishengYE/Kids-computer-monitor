package com.radioyps.kidscomputermonitor

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader

import java.net.*




const val ZIYI_COMPUTER_MAC = "00:10:f3:6b:2e:da"
const val ZIHAN_COMPUTER_MAC_1 = "34:e6:d7:17:b7:3a"
const val ZIHAN_COMPUTER_MAC_2 = "00:13:3b:99:31:af"




class MainViewModel() : ViewModel() {
    val TAG = "MainViewModel"


    /**
     * Request a snackbar to display a string.
     *
     * This variable is private because we don't want to expose MutableLiveData
     *
     * MutableLiveData allows anyone to set a value, and MainViewModel is the only
     * class that should be setting values.
     */
    private val _snackBar = MutableLiveData<String?>()

    /**
     * Request a snackbar to display a string.
     */
    val snackbar: LiveData<String?>
        get() = _snackBar

    private val _scanningProgress = MutableLiveData<Int>()

    /**
     * Request a snackbar to display a string.
     */
    val scanningProgress: LiveData<Int>
        get() = _scanningProgress



    private val _spinner = MutableLiveData<Boolean>(false)

    /**
     * Show a loading spinner if true
     */
    val spinner: LiveData<Boolean>
        get() = _spinner

    /**
     * Count of taps on the screen
     */
    private var tapCount = 0

    /**
     * LiveData with formatted tap count.
     */


    private val _waitingStatus = MutableLiveData<String>("")

    val waitingStatus: LiveData<String>
        get() = _waitingStatus

    /**
     * LiveData with formatted tap count.
     */
    private val _imageUrl = MutableLiveData<String>()

    /**
     * Public view of tap live data.
     */
    val imageUrl: LiveData<String>
        get() = _imageUrl

    /**
     * LiveData with formatted tap count.
     */
    private val _currentComputerOwner = MutableLiveData<String>()

    /**
     * Public view of tap live data.
     */
    val currentComputerOwner: LiveData<String>
        get() = _currentComputerOwner

    /**
     * LiveData with formatted tap count.
     */
    private val _currentComputerName = MutableLiveData<String>()

    /**
     * Public view of tap live data.
     */
    val currentComputerName: LiveData<String>
        get() = _currentComputerName




    private val _connectStatus = MutableLiveData<String>("kids computer disconnected")

    /**
     * Public view of tap live data.
     */
    val connectStatus: LiveData<String>
        get() = _connectStatus

    private var isContinueScanScreenshot = false



    data class  ComputerStatus(
        var owner: String,
        var computerName: String = "Unknown",
        var osType: String,
        var macAddrss: String,
        var ipAddress: String,
        var isOnLine: Boolean,
        var alive: Boolean = false,
        var portNumber: Int = 8088

    )

    private var kidsComputerStatusList = mutableListOf<ComputerStatus>()

    private  var isPortOpen = false

    private fun initKidsComputerStatus(){
        val zihan1 = ComputerStatus(
            owner = "ZIHAN",
            osType = "LINUX",
            computerName = "Zihan Linux",
            macAddrss = ZIHAN_COMPUTER_MAC_1,
            ipAddress = "192.168.106.231",
            isOnLine = false

        )

        kidsComputerStatusList.add(zihan1)

        val zihan2 = ComputerStatus(
            owner = "ZIHAN",
            osType = "WINDOWS",
            computerName = "Zihan Windows",
            macAddrss = ZIHAN_COMPUTER_MAC_1,
            ipAddress = "192.168.106.119",
            isOnLine = false
        )
        kidsComputerStatusList.add(zihan2)


        val zihan3 = ComputerStatus(
            owner = "ZIHAN",
            osType = "WINDOWS",
            computerName = "Zihan Windows",
            macAddrss = ZIHAN_COMPUTER_MAC_1,
            ipAddress = "192.168.106.115",
            portNumber = 8088,
            isOnLine = false
        )
        kidsComputerStatusList.add(zihan3)

        val zihan4 = ComputerStatus(
            owner = "ZIHAN",
            osType = "WINDOWS",
            computerName = "Zihan Windows",
            macAddrss = ZIHAN_COMPUTER_MAC_1,
            ipAddress = "192.168.106.114",
            portNumber = 8088,
            isOnLine = false
        )
        kidsComputerStatusList.add(zihan4)
        val zihan5 = ComputerStatus(
            owner = "ZIHAN",
            osType = "WINDOWS",
            computerName = "Zihan Windows",
            macAddrss = ZIHAN_COMPUTER_MAC_1,
            ipAddress = "192.168.106.113",
            portNumber = 8088,
            isOnLine = false
        )
        kidsComputerStatusList.add(zihan5)
        val ziyi = ComputerStatus(
            owner = "ZIYI",
            osType = "LINUX",
            computerName = "Ziyi Linux",
            macAddrss = ZIYI_COMPUTER_MAC,
            ipAddress = "192.168.106.221",
            isOnLine = false
        )
        kidsComputerStatusList.add(ziyi)

        val ziyi_from_office = ComputerStatus(
            owner = "ZIYI",
            osType = "LINUX",
            computerName = "Ziyi Linux",
            macAddrss = ZIYI_COMPUTER_MAC,
            ipAddress = "172.16.18.19",
            portNumber = 8081,
            isOnLine = false
        )
        kidsComputerStatusList.add(ziyi_from_office)

        val zihan_from_office_win = ComputerStatus(
            owner = "ZIHAN_WIN",
            osType = "LINUX",
            computerName = "Ziyi Linux",
            macAddrss = ZIYI_COMPUTER_MAC,
            ipAddress = "172.16.18.19",
            portNumber = 8082,
            isOnLine = false
        )
        kidsComputerStatusList.add(zihan_from_office_win)

        val zihan_from_office_lin = ComputerStatus(
            owner = "ZIHAN_LINUX",
            osType = "LINUX",
            computerName = "Ziyi Linux",
            macAddrss = ZIYI_COMPUTER_MAC,
            ipAddress = "172.16.18.19",
            portNumber = 8083,
            isOnLine = false
        )
        kidsComputerStatusList.add(zihan_from_office_lin)

        val bitMine_from_office = ComputerStatus(
            owner = "bitMine",
            osType = "Win10",
            computerName = "BitMine_WIN10",
            macAddrss = ZIYI_COMPUTER_MAC,
            ipAddress = "172.16.18.19",
            portNumber = 8084,
            isOnLine = false
        )
//        kidsComputerStatusList.add(bitMine_from_office)

        val bitMineWindows1 = ComputerStatus(
            owner = "Peisheng",
            osType = "Windows",
            computerName = "BitMine",
            macAddrss = ZIYI_COMPUTER_MAC,
            ipAddress = "192.168.106.110",
            portNumber = 8088,
            isOnLine = false
        )
        kidsComputerStatusList.add(bitMineWindows1)

        val bitMineWindows2 = ComputerStatus(
            owner = "Peisheng",
            osType = "Windows",
            computerName = "BitMine",
            macAddrss = ZIYI_COMPUTER_MAC,
            ipAddress = "192.168.106.116",
            portNumber = 8088,
            isOnLine = false
        )
        kidsComputerStatusList.add(bitMineWindows2)
    }



    private fun isKidsComputerOnLine(){
        viewModelScope.launch {


            while (true){
                kidsComputerStatusList.forEach {

                    isPortForScreenshotOpenByHttp(it.ipAddress, it.portNumber)

                    if (isPortOpen){
                        Log.v(TAG, "isCheckingPortDone : open on ${it.ipAddress}")
                        if (it.isOnLine == false ){
                            _connectStatus.value = "${it.owner} connected"
                        }
                        it.isOnLine = true
                    }else{
                        Log.v(TAG, "isCheckingPortDone : close on ${it.ipAddress}")
                        if (it.isOnLine == true ){
                            _connectStatus.value = "${it.owner} disconnected"
                        }
                        it.isOnLine = false
                    }
                    delay(2000)
                }
            }


        }
    }




    private suspend fun isPortForScreenshotOpenByHttp(ip: String, port: Int) {

        withContext(Dispatchers.IO){
            var deferred: Deferred<Boolean> = async {
                var res = false
                var codeResponse = 0
                val mURL = URL("http://${ip}:${port}/getJson")
                try {
                    with((mURL.openConnection() as HttpURLConnection)) {
                        // optional default is GET
                        this.connectTimeout = 2000
                        this.readTimeout = 2000
                        requestMethod = "GET"

                        println("URL : $url")
                        println("Response Code : $responseCode")
                        codeResponse = responseCode
                        BufferedReader(InputStreamReader(inputStream)).use {
                            val response = StringBuffer()

                            var inputLine = it.readLine()
                            while (inputLine != null) {
                                response.append(inputLine)
                                inputLine = it.readLine()
                            }
                            it.close()
                            println("Response : $response")
                            if (response.contains("Nice work!")) {
                                res = true
                            }
                        }

                    }
                }catch (cause: Throwable) {
                    // If anything throws an exception, inform the caller

                    Log.v(TAG, "  ${ip}:$port is not available:  " + cause)
                    res = false
                    if (codeResponse == 404 ) res = true
                }
                res
            }
            isPortOpen = deferred.await()
        }


    }


    fun updateScreenShot(){
        viewModelScope.launch {
            var URL_SCREENSHOT = ""
            while (isContinueScanScreenshot) {
                kidsComputerStatusList.forEach {
                    if (it.isOnLine){
                        Log.v(TAG, "${it.owner} computer is alive!")
                        URL_SCREENSHOT = "http://${it.ipAddress}:${it.portNumber}//image/"
                        _currentComputerOwner.value = it.owner
                        _currentComputerName.value = it.computerName
                        _imageUrl.value = URL_SCREENSHOT



                    }
                    delay(5000)
                }
            }
        }
    }






    init {


        initKidsComputerStatus()

        isKidsComputerOnLine()
        isContinueScanScreenshot = true
        updateScreenShot()
    }

    /**
     * Called immediately after the UI shows the snackbar.
     */
    fun onSnackbarShown() {
        _snackBar.value = null
    }


}
