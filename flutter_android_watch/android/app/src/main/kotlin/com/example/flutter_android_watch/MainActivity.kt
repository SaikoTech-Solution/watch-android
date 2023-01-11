package com.example.flutter_android_watch
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import kotlin.random.Random
  class MainActivity : FlutterFragmentActivity() {
    private val CHANNEL = "flutter_android_watch"
    var methodChannelResult: MethodChannel.Result? = null

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        ).setMethodCallHandler { call, result ->
            methodChannelResult = result
            if (call.method == "isConnected") {
                WatchManager.isConnected(applicationContext) { resultValue ->
                    result.success(resultValue.toString())
                }
            }
            if (call.method == "disconnect") {
                WatchManager.disconnect(applicationContext) { resultValue ->
                    result.success(resultValue.toString())
                }
            }
            if (call.method == "startScan") {
                WatchManager.startScan(applicationContext) { resultValue ->
                    Log.d("WatchManager", resultValue.toString())
                    result.success(resultValue.toString())
                }
            }
            if (call.method == "startConnection") {
                val deviceAddress = call.argument<String>("deviceAddress")
                WatchManager.startConnection(applicationContext, deviceAddress.toString()) { resultValue ->
                    result.success(resultValue.toString())
                }
            }
            if (call.method == "getHeartRate") {
                val date = call.argument<String>("date")
                WatchManager.getHeartRate(applicationContext, date.toString()) { resultValue ->
                    result.success(resultValue.toString())
                }
            }
            if (call.method == "getBloodPressure") {
                val date = call.argument<String>("date")
                WatchManager.getBloodPressure(applicationContext, date.toString()) { resultValue ->
                    result.success(resultValue.toString())
                }
            }
            if (call.method == "getPedometer") {
                val date = call.argument<String>("date")
                WatchManager.getPedometer(applicationContext, date.toString()) { resultValue ->
                    result.success(resultValue.toString())
                }
            }
            if (call.method == "getSleep") {
                val date = call.argument<String>("date")
//                WatchManager.getSleep(applicationContext, date.toString()) { resultValue ->
//                    result.success(resultValue.toString())
//                }
            }
        }
    }
}
