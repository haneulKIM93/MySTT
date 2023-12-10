package com.example.stt

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.stt.databinding.ActivityMainBinding

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var speechRecognizer: SpeechRecognizer
    private var isListening = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 권한 설정
        requestPermission()

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this@MainActivity)

        // RecognizerIntent 생성
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)    // 여분의 키
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")         // 언어 설정

        // <말하기> 버튼 눌러서 음성인식 시작
        binding.btnSpeech.setOnClickListener {
            // 새 SpeechRecognizer 를 만드는 팩토리 메서드
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this@MainActivity)
            speechRecognizer.setRecognitionListener(recognitionListener)    // 리스너 설정
            speechRecognizer.startListening(intent)                         // 듣기 시작
            isListening = true
        }

        // '멈춤' 버튼 클릭 시 음성 인식 중지
        binding.btnStop.setOnClickListener {
            if (isListening) {
                speechRecognizer.stopListening()
                binding.tvState.text = "음성인식 중지"
                isListening = false
            } else {
                Toast.makeText(applicationContext, "음성인식 중이 아닙니다.", Toast.LENGTH_SHORT).show()
            }
        }


    }

    // 권한 설정 메소드
    private fun requestPermission() {
        // 버전 체크, 권한 허용했는지 체크
        if (Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(Manifest.permission.RECORD_AUDIO), 0)
        }
    }

    // 리스너 설정
    private val recognitionListener: RecognitionListener = object : RecognitionListener {
        // 말하기 시작할 준비가되면 호출
        override fun onReadyForSpeech(params: Bundle) {
            Toast.makeText(applicationContext, "음성인식 시작", Toast.LENGTH_SHORT).show()
            binding.tvState.text = "이제 말씀하세요!"
        }
        // 말하기 시작했을 때 호출
        override fun onBeginningOfSpeech() {
            binding.tvState.text = "잘 듣고 있어요."
        }
        // 입력받는 소리의 크기를 알려줌
        override fun onRmsChanged(rmsdB: Float) {}
        // 말을 시작하고 인식이 된 단어를 buffer에 담음
        override fun onBufferReceived(buffer: ByteArray) {}
        // 말하기를 중지하면 호출
        override fun onEndOfSpeech() {
            binding.tvState.text = "끝!"
        }
        // 오류 발생했을 때 호출
        override fun onError(error: Int) {
            val message = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "오디오 에러"
                SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "퍼미션 없음"
                SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트웍 타임아웃"
                SpeechRecognizer.ERROR_NO_MATCH -> "찾을 수 없음"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RECOGNIZER 가 바쁨"
                SpeechRecognizer.ERROR_SERVER -> "서버가 이상함"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "말하는 시간초과"
                else -> "알 수 없는 오류임"
            }
            binding.tvState.text = "에러 발생: $message"
        }
        // 인식 결과가 준비되면 호출
        override fun onResults(results: Bundle) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            for (i in matches!!.indices) binding.textView.text = matches[i]
        }
        // 부분 인식 결과를 사용할 수 있을 때 호출
        override fun onPartialResults(partialResults: Bundle) {}
        // 향후 이벤트를 추가하기 위해 예약
        override fun onEvent(eventType: Int, params: Bundle) {}
    }
}
//
//import com.example.stt.interfaces.DataApiService
//import android.content.pm.PackageManager
//import android.media.MediaRecorder
//import android.os.Build
//import android.os.Bundle
//import android.os.Environment
//import android.widget.Button
//import android.widget.Toast
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.example.stt.databinding.ActivityMainBinding
//import java.io.IOException
//import java.util.Date
//
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//
//
//
//class MainActivity : AppCompatActivity() {
//    val CLIENT_ID = "d3ccdeb5-103e-49ef-9ad4-dc5645dbeddf"
//
//    val retrofit = Retrofit.Builder()
//        .baseUrl("http://aiopen.etri.re.kr:8000/")
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    val dateApiService = retrofit.create(DataApiService::class.java)
//    val callPostTransferSound = dateApiService.transferSound(CLIENT_ID, )
//
//
//    private var outputPath: String? = null
//    private var mediaRecorder : MediaRecorder? = null
//    private var state : Boolean = false
//
//    @RequiresApi(Build.VERSION_CODES.R) // api 연결을 위한 최소한의 버전 R
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        val binding= ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val startBtn = findViewById<Button>(R.id.button_start)
//        val stopBtn = findViewById<Button>(R.id.button_stop)
//
//        // 녹음 시작 버튼
//        startBtn.setOnClickListener {
//            // 권한 부여 여부
//            val isEmpower = ContextCompat.checkSelfPermission(this,
//                android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
//            // 권한 부여 되지 않았을경우
//            if (isEmpower) {
//                empowerRecordAudioAndWriteReadStorage()
//                // 권한 부여 되었을 경우
//            } else {
//                startRecording()
//            }
//        }
//        // 녹음 중지 버튼
//        stopBtn.setOnClickListener {
//            stopRecording()
//        }
//    }
//
//    // 레코딩, 파일 읽기 쓰기 권한부여
//    private fun empowerRecordAudioAndWriteReadStorage(){
//        val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
//        ActivityCompat.requestPermissions(this, permissions,0)
//    }
//
//    private fun startRecording(){
//
//        val fileName: String = Date().getTime().toString() + ".mp3"
//
//        outputPath = Environment.getExternalStorageDirectory().absolutePath + "/Download/" + fileName //내장메모리 밑에 위치
//        mediaRecorder = MediaRecorder()
//        mediaRecorder?.setAudioSource((MediaRecorder.AudioSource.MIC))
//        mediaRecorder?.setOutputFormat((MediaRecorder.OutputFormat.MPEG_4))
//        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
//        mediaRecorder?.setOutputFile(outputPath)
//
//        try {
//            mediaRecorder?.prepare()
//            mediaRecorder?.start()
//            state = true
//            Toast.makeText(this, "녹음이 시작되었습니다.", Toast.LENGTH_SHORT).show()
//        } catch (e: IllegalStateException){
//            e.printStackTrace()
//        } catch (e: IOException){
//            e.printStackTrace()
//        }
//    }
//
//    private fun stopRecording(){
//        if(state){
//            mediaRecorder?.stop()
//            mediaRecorder?.reset()
//            mediaRecorder?.release()
//            state = false
//            Toast.makeText(this, "녹음이 되었습니다.", Toast.LENGTH_SHORT).show()
////            TTSApi()
//        } else {
//            Toast.makeText(this, "녹음 상태가 아닙니다.", Toast.LENGTH_SHORT).show()
//        }
//    }
//
////    @RequiresApi(Build.VERSION_CODES.O)
////    private fun TTSApi(){
////        val openApiURL = "http://aiopen.etri.re.kr:8000/WiseASR/Recognition"
////        val accessKey = "YOUR_ACCESS_KEY" // 발급받은 API Key
////
////        val languageCode = "LANGUAGE_CODE" // 언어 코드
////
////        val audioFilePath = "AUDIO_FILE_PATH" // 녹음된 음성 파일 경로
////
////        var audioContents: String? = null
////
////        val gson = Gson()
////
////        val request: MutableMap<String, Any> = HashMap()
////        val argument: MutableMap<String, String?> = HashMap()
////
////        try {
////            val path: Path = Paths.get(audioFilePath)
////            val audioBytes: ByteArray = Files.readAllBytes(path)
////            audioContents = Base64.getEncoder().encodeToString(audioBytes)
////        } catch (e: IOException) {
////            e.printStackTrace()
////        }
////
////        argument["language_code"] = languageCode
////        argument["audio"] = audioContents
////
////        request["argument"] = argument
////
////        val url: URL
////        var responseCode: Int? = null
////        var responBody: String? = null
////        try {
////            url = URL(openApiURL)
////            val con = url.openConnection() as HttpURLConnection
////            con.requestMethod = "POST"
////            con.doOutput = true
////            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
////            con.setRequestProperty("Authorization", accessKey)
////            val wr = DataOutputStream(con.outputStream)
////            wr.write(gson.toJson(request).getBytes("UTF-8"))
////            wr.flush()
////            wr.close()
////            responseCode = con.responseCode
////            val `is` = con.inputStream
////            val buffer = ByteArray(`is`.available())
////            val byteRead = `is`.read(buffer)
////            responBody = kotlin.String(buffer)
////            println("[responseCode] $responseCode")
////            println("[responBody]")
////            println(responBody)
////        } catch (e: MalformedURLException) {
////            e.printStackTrace()
////        } catch (e: IOException) {
////            e.printStackTrace()
////        }
////    }
//}
