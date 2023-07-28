package com.radioyps.kidscomputermonitor

import android.app.Application
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*
import java.io.*
import java.net.*
import java.text.SimpleDateFormat
import java.util.*


const val COMPUTER_1_ENDPOINT = "/zihan"
const val COMPUTER_2_ENDPOINT = "/ziyi"
const val COMPUTER_3_ENDPOINT = "/bitmine"


const val COMPUTER_1_NAME = "ZIHAN"
const val COMPUTER_2_NAME = "ZIYI"
const val COMPUTER_3_NAME = "bitmap"

class MainViewModel(application: Application) : ViewModel() {
    val TAG = "MainViewModel"
    val context = application.applicationContext


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


    val isShowPhotos: LiveData<Boolean>
        get() = _isShowPhotos
    private var _isShowPhotos = MutableLiveData<Boolean>(false)




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

    var imageViewWidth: Int = 0
    var imageViewHeight: Int = 0

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
    private val _imageBitmap = MutableLiveData<Bitmap>()

    /**
     * Public view of tap live data.
     */
    val timeOnScreen: LiveData<String>
        get() = _timeOnScreen

    /**
     * LiveData with formatted tap count.
     */
    private val _timeOnScreen = MutableLiveData<String>()


    /**
     * Public view of tap live data.
     */
    val imageTimeStamp: LiveData<String>
        get() = _imageTimeStamp

    /**
     * LiveData with formatted tap count.
     */
    private val _imageTimeStamp = MutableLiveData<String>()


    /**
     * Public view of tap live data.
     */
    val imageBitmap: LiveData<Bitmap>
        get() = _imageBitmap




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


    private var bmp: Bitmap? = null

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
        _isShowPhotos.value = false
    }

    fun clearOnlyShowKidsComputer(){
        _isShowOnlyKidsComputer.value = false
        _isShowPhotos.value = false
    }

    fun setShowPhotoOnly(){
        _isShowPhotos.value = true
    }

    fun setImageViewDimention(width: Int,height : Int){
        imageViewHeight = height
        imageViewWidth = width
    }

    private fun initKidsComputerStatus(){
        val computer1 = ComputerStatus(
            name = COMPUTER_1_NAME,

            httpEndPoint = COMPUTER_1_ENDPOINT,
            isOnLine = false

        )

        kidsComputerStatusList.add(computer1)

        _isShowPhotos.value = false
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


    fun saveBitmapAsPNG(): Boolean {
        return try {
            // Get the directory for external storage
            val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "kids")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            // Create the file in the external storage directory
            val file = File(directory, "kids.png")

            // Compress the bitmap and save it to the file
            val stream = FileOutputStream(file)
            bmp?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()

            true // Return true if the file was successfully saved
        } catch (e: IOException) {
            e.printStackTrace()
            false // Return false if there was an error saving the file
        }
    }

    fun drawTextToBitmap(
        name: String?,
        image: Bitmap
    ): Bitmap? {
        var gText = ""

        var image = image
        var bitmapConfig = image.config
        // set default bitmap config if none
        if (bitmapConfig == null) {
//            bitmapConfig = Bitmap.Config.ARGB_8888
            bitmapConfig = Bitmap.Config.RGB_565
        }


        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
//        image = image.copy(bitmapConfig, true)
          val canvas = Canvas(image)
        // new antialised Paint
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        // text color - #3D3D3D
        paint.color = Color.rgb(0, 0, 0xff)
        // text size in pixels
        if (image.width < 1500){
            paint.setTextSize((14 * 2.0).toFloat())
        }else{
            paint.setTextSize((14 * 10.0).toFloat())
        }

        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.RED)

        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        gText = sdf.format(Date()) + " " + name
        // draw text to the Canvas center
        val bounds = Rect()
        paint.getTextBounds(gText, 0, gText.length, bounds)
        val x = (image.width - bounds.width()) / 2
        val y = (image.height + bounds.height()) / 2-100
        /* Drawing X: 891 Y: 552 */
        Log.v(TAG, "Drawing image: width: ${image.width}, height: ${image.height} X: ${x} Y: ${y}")
        canvas.drawText(gText, x.toFloat(), y.toFloat(), paint)
        return image
    }


    private fun updateTime(){
        viewModelScope.launch {


            while (true){

                val sdf = SimpleDateFormat("MMdd_HH:mm:ss", Locale.getDefault())
                val currentDateandTime: String = sdf.format(Date())
//                Log.v(TAG, " updateTime()>> " + currentDateandTime)
                _timeOnScreen.value = currentDateandTime
                delay(1000)

            }


        }
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

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {

        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        Log.v(TAG, " calculateInSampleSize()>> imageWidth: " + width + " imageHeight: " + height)
        Log.v(TAG, " calculateInSampleSize()>> reqWidth: " + reqWidth + " reqHeight: " + reqHeight)

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight || halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        Log.v(TAG, " calculateInSampleSize()>> inSampleSize: " + inSampleSize)
        return inSampleSize
    }


    private  fun decodeSampledBitmap(
        input: ByteArray

    ): Bitmap? {
        // First decode with inJustDecodeBounds=true to check dimensions
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(input, 0, input.size,this)

            // Calculate inSampleSize
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
//                 /* reduce the memory usage to prevent out of memory issue */
//                 inSampleSize = calculateInSampleSize(this, imageViewWidth, imageViewHeight)*2
//            else{
//                inSampleSize = calculateInSampleSize(this, imageViewWidth, imageViewHeight)
//            }
            inSampleSize = calculateInSampleSize(this, imageViewWidth, imageViewHeight)
            // Decode bitmap with inSampleSize set
            inJustDecodeBounds = false
            Log.v(TAG, " decodeSampledBitmap()>> inSampleSize: " + inSampleSize + ", Start decoding...")
            try{
                BitmapFactory.decodeByteArray(input, 0, input.size,this)
            } catch (ex: Exception) {

            Log.e("Exception", ex.toString())
            null
        }

        }
    }


     private suspend fun downloadImageFromPath(path: String?) {


        withContext(Dispatchers.IO) {
            var deferred: Deferred<Bitmap?> = async {
                var res: Bitmap? = null
                var inputStreamImage: ByteArray? = null

                var responseCode = -1
                try {
                    val url = URL(path)
                    Log.v(TAG, " downloadImageFromPath()>> downloading from " + path)
                    val con = url.openConnection() as HttpURLConnection
                    con.doInput = true
                    con.connect()
                    responseCode = con.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Log.v(TAG, " downloadImageFromPath()>> Start downloading image from ngrok server")

                        //download
                        /* Out of memory error when downloading a big JPEG file */

                        inputStreamImage = con.inputStream.readBytes()


//                        val options = BitmapFactory.Options()
//                        options.inPreferredConfig = Bitmap.Config.RGB_565
//                        options.inSampleSize = 3

//                        res = BitmapFactory.decodeStream(inputStreamImage, null, options)
                        res = decodeSampledBitmap(inputStreamImage)
                        if (res != null){

//                            res = Bitmap.createScaledBitmap(res, 800,285, false)
//                            res = drawTextToBitmap(path?.substringAfterLast('/'), res)

                            Log.v(
                                TAG,
                                " downloadImageFromPath()>> downloaded bmp file successfully"
                            )

                        }else{
                            Log.v(TAG, " downloadImageFromPath()>> downloaded bmp file failed")
                        }

                    } else {

                        Log.v(
                            TAG,
                            " downloadImageFromPath()>> downloaded bmp file failed with HTTP connection error"
                        )
                    }

                } catch (ex: Exception) {
                    Log.v(
                        TAG,
                        " downloadImageFromPath()>> downloaded bmp file failed with Exception"
                    )
                    Log.e("Exception", ex.toString())

                } catch (ex: OutOfMemoryError){
                    Log.v(
                        TAG,
                        " downloadImageFromPath()>> downloaded bmp file failed with Out of memory Exception"
                    )
                }

                res
            }
            bmp = deferred.await()
        }
    }

    private suspend fun  getHomeComputersInfo(){
        withContext(Dispatchers.IO){
            var deferred: Deferred<Boolean> = async {
                var res = false
                var codeResponse = 0
                val mURL = URL(ngrokPublicUrl + "/json")
                try {
                    with((mURL.openConnection() as HttpURLConnection)) {
                        // optional default is GET
                        this.connectTimeout = 4000
                        this.readTimeout = 4000


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

        var regex = Regex("""(\"public_url\"):\"(https://[a-z0-9-.]+)\".\"""")
        var ids = regex.find(input)
        if (ids == null){
            Log.v(TAG, "no valide string from Ngrok ")
                return ""
        }
        var info = ids.destructured.toList()

        if (info.size != 2){
            return ""
        }else{
            Log.v(TAG, "valide string from Ngrok: ${info[1]} ")
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
                var ngrokApiKey = MonitorApplication.ngrokApiKey
                Log.v(TAG, "The Ngrok API key:  " + ngrokApiKey)
                val mURL = URL("https://api.ngrok.com/tunnels")
                try {
                    with((mURL.openConnection() as HttpURLConnection)) {
                        // optional default is GET
                        this.connectTimeout = 4000
                        this.readTimeout = 4000
                        this.setRequestProperty("Content-Type", "application/json")
                        this.setRequestProperty(
                            "Authorization",
                            "Bearer " + ngrokApiKey
                        )
                        this.setRequestProperty("Ngrok-Version", "2")

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
                            Log.v(TAG, "-----------> Get Ngrok Public Url: ${ngrokPublicUrl} ")
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

    private suspend fun getImage(url: String):Boolean{
        if(bmp != null){
            /* reduce the memory usage to prevent out of memory issue
            *  Very important to recycle the bitmap as soon as we do not need it */
            bmp?.recycle()
            bmp = null
        }

        var res = false
        Log.v(TAG, " getImage()>>  " + url)
        downloadImageFromPath(url)
        if (bmp != null){
            _imageBitmap.value = bmp

            val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val gText = sdf.format(Date()) + " " + url?.substringAfterLast('/')
            _imageTimeStamp.value = gText
        }
        if (bmp != null) {
            saveBitmapAsPNG()
            res = true
        }
        return res
    }


    fun updateScreenShot(){
        viewModelScope.launch {
            var URL_SCREENSHOT = ""
            var someoneOnline = false
            var noOneOnlineCount = 0
            while (isContinueScanScreenshot) {
                if (_isShowPhotos.value?:false){
                    URL_SCREENSHOT = "${ngrokPublicUrl}/image"
                    val goodDownloading = getImage(URL_SCREENSHOT)
                    if (goodDownloading)
                       delay(25000)
                    continue
                }
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
                            var count = 5
                            while (count > 0){
                                val goodDownloading = getImage(URL_SCREENSHOT)
                                if (goodDownloading){
                                   break
                                }
                                delay(200)
                                count --
                            }
                            delay(5000)

                            _connectStatus.value = "${it.name} connected"
                            someoneOnline = true
                        }else{
                            _connectStatus.value = "${it.name} disconnected"
                        }
//                        delay(5000)
                    }

                }
                if (!someoneOnline){
                    noOneOnlineCount++
                    if (noOneOnlineCount>= 5){
                        URL_SCREENSHOT = "${ngrokPublicUrl}/image"
                        val goodDownloading = getImage(URL_SCREENSHOT)
                        if (goodDownloading)
                            delay(5000)
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
        updateTime()
        isContinueScanScreenshot = true
        updateScreenShot()
    }

    /**
     * Called immediately after the UI shows the snackbar.
     */
    fun onSnackbarShown() {
        _snackBar.value = null
    }

    class MainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                MainViewModel(application) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }


}
