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



const val COMPUTER_1_ENDPOINT = "/zihan"
const val COMPUTER_2_ENDPOINT = "/ziyi"
const val COMPUTER_3_ENDPOINT = "/bitmine"


const val COMPUTER_1_NAME = "ZIHAN"
const val COMPUTER_2_NAME = "ZIYI"
const val COMPUTER_3_NAME = "bitmap"

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

    val isShowOnlyKidsComputer: LiveData<Boolean>
        get() = _isShowOnlyKidsComputer
    private var _isShowOnlyKidsComputer = MutableLiveData<Boolean>(true)
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

        var name: String = "Unknown",

        var isOnLine: Boolean,
        var alive: Boolean = false,
        var httpEndPoint: String = "/Unknown"


    )

    private var kidsComputerStatusList = mutableListOf<ComputerStatus>()

    private  var isPortOpen = false
    private  var isPublicUrlfound = false
    private  var ngrokPublicUrl = ""

    fun setOnlyShowKidsComputer(){
        _isShowOnlyKidsComputer.value = true
    }

    fun clearOnlyShowKidsComputer(){
        _isShowOnlyKidsComputer.value = false
    }

    private fun initKidsComputerStatus(){
        val computer1 = ComputerStatus(
            name = COMPUTER_1_NAME,

            httpEndPoint = COMPUTER_1_ENDPOINT,
            isOnLine = false

        )

        kidsComputerStatusList.add(computer1)


        val computer2 = ComputerStatus(
            name = COMPUTER_2_NAME,
            httpEndPoint = COMPUTER_2_ENDPOINT,
            isOnLine = false

        )

        kidsComputerStatusList.add(computer2)

        val computer3 = ComputerStatus(
            name = COMPUTER_3_NAME,
            httpEndPoint = COMPUTER_3_ENDPOINT,
            isOnLine = false

        )

        kidsComputerStatusList.add(computer3)



    }



    private fun isKidsComputerOnLine(){
        viewModelScope.launch {


            while (true){

                    getNgrokPublicUrl()
                    if (!ngrokPublicUrl.isNullOrEmpty()){
                        getHomeComputersInfo()
                    }

                    delay(10000)

            }


        }
    }

    private fun setComputerStatus(jsonString: String){
        var name = ""
        var status = false
        if (jsonString.contains(COMPUTER_1_NAME, ignoreCase = true)){
            name = COMPUTER_1_NAME
            status = true
        }else{
            name = COMPUTER_1_NAME
            status = false
        }

        kidsComputerStatusList.forEach{
            if (it.name == name){
                it.isOnLine = status
                if(status)
                   Log.v(TAG, "${it.name} is on line ")
            }
        }


        if (jsonString.contains(COMPUTER_2_NAME, ignoreCase = true)){
            name = COMPUTER_2_NAME
            status = true
        }else{
            name = COMPUTER_2_NAME
            status = false
        }

        kidsComputerStatusList.forEach{
            if (it.name == name){
                it.isOnLine = status
                if(status)
                    Log.v(TAG, "${it.name} is on line ")
            }
        }

        if (jsonString.contains(COMPUTER_3_NAME, ignoreCase = true)){
            name = COMPUTER_3_NAME
            status = true
        }else{
            name = COMPUTER_3_NAME
            status = false
        }

        kidsComputerStatusList.forEach{
            if (it.name == name){
                it.isOnLine = status
                if(status)
                    Log.v(TAG, "${it.name} is on line ")
            }
        }



    }

    private suspend fun  getHomeComputersInfo(){
        withContext(Dispatchers.IO){
            var deferred: Deferred<Boolean> = async {
                var res = false
                var codeResponse = 0
                val mURL = URL(ngrokPublicUrl+"/json")
                try {
                    with((mURL.openConnection() as HttpURLConnection)) {
                        // optional default is GET
                        this.connectTimeout = 2000
                        this.readTimeout = 2000


                        this.requestMethod = "GET"

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
                            setComputerStatus(response.toString())

                        }

                    }
                }catch (cause: Throwable) {
                    // If anything throws an exception, inform the caller

                    Log.v(TAG, "The Ngrok server is not available:  " + cause)
                    res = false

                }
                res
            }
            deferred.await()
        }
    }

    private fun parseNgrokUrl(input: String) : String{

        var regex = Regex("""(\"public_url\"):\"(https://[a-z0-9-]+\.ngrok\.io)\"""")
        var ids = regex.find(input)
        if (ids == null){
            Log.v(TAG,"no valide string from Ngrok ")
                return ""
        }
        var info = ids.destructured.toList()

        if (info.size != 2){
            return ""
        }else{
            Log.v(TAG,"valide string from Ngrok: ${info[1]} " )
            return info[1]
        }
//        Log.v(TAG,"valide string from Ngrok ")
        /*
        logTimber!!.log(
            Log.VERBOSE,
            "Sensor Name: ${info[0]}, timestamp: ${info[1]}, sessionID: ${info[2]}"
        )

         */
        return ""
    }

    private suspend fun getNgrokPublicUrl() {

        withContext(Dispatchers.IO){
            var deferred: Deferred<Boolean> = async {
                var res = false
                var codeResponse = 0
                val mURL = URL("https://api.ngrok.com/tunnels")
                try {
                    with((mURL.openConnection() as HttpURLConnection)) {
                        // optional default is GET
                        this.connectTimeout = 2000
                        this.readTimeout = 2000
                        this.setRequestProperty( "Content-Type", "application/json")
                        this.setRequestProperty( "Authorization", "Bearer 28Mb0yujy0qi6kmZviiB364lV2Z_4QDJTayEKNuy5ZxYVJo34")
                        this.setRequestProperty( "Ngrok-Version","2")

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
                            ngrokPublicUrl = parseNgrokUrl(response.toString())
                            Log.v(TAG,"-----------> Get Ngrok Public Url: ${ngrokPublicUrl} " )
                            if (ngrokPublicUrl.isNullOrEmpty()){
                                res = false
                            }else{
                                res = true
                            }
                        }

                    }
                }catch (cause: Throwable) {
                    // If anything throws an exception, inform the caller

                    Log.v(TAG, "The Ngrok server is not available:  " + cause)
                    res = false

                }
                res
            }
            isPublicUrlfound = deferred.await()
        }


    }



    fun updateScreenShot(){
        viewModelScope.launch {
            var URL_SCREENSHOT = ""
            var someoneOnline = false
            var noOneOnlineCount = 0
            while (isContinueScanScreenshot) {
                someoneOnline = false
                kidsComputerStatusList.forEach {
                    if((it.name.contains("ziyi", ignoreCase = true))
                        ||(it.name.contains("zihan", ignoreCase = true))
                        || (!(_isShowOnlyKidsComputer.value?:false))
                    ) {
                        if (it.isOnLine) {
                            Log.v(TAG, "${it.name} computer is alive!")
                            URL_SCREENSHOT = "${ngrokPublicUrl}${it.httpEndPoint}"
                            _currentComputerOwner.value = it.name
                            _currentComputerName.value = it.name
                            _imageUrl.value = URL_SCREENSHOT
                            _connectStatus.value = "${it.name} connected"
                            someoneOnline = true
                        }else{
                            _connectStatus.value = "${it.name} disconnected"
                        }
                    }
                    delay(5000)
                }
                if (!someoneOnline){
                    noOneOnlineCount++
                    if (noOneOnlineCount>= 5){
                        URL_SCREENSHOT = "${ngrokPublicUrl}/image"
                        _imageUrl.value = URL_SCREENSHOT
                    }
                }else{
                    noOneOnlineCount = 0
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
