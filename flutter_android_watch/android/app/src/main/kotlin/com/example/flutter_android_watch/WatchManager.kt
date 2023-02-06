package com.example.flutter_android_watch

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.inuker.bluetooth.library.Code
import com.inuker.bluetooth.library.search.SearchResult
import com.inuker.bluetooth.library.search.response.SearchResponse
import com.veepoo.protocol.VPOperateManager
import com.veepoo.protocol.listener.base.IABluetoothStateListener
import com.veepoo.protocol.listener.base.IBleWriteResponse
import com.veepoo.protocol.listener.data.IBPDetectDataListener
import com.veepoo.protocol.listener.data.ISocialMsgDataListener
import com.veepoo.protocol.model.datas.FunctionSocailMsgData
import com.veepoo.protocol.model.enums.EBPDetectModel
import io.flutter.Log
import java.util.Timer
import java.util.TimerTask

object WatchManager {

    private var peripherals = sortedSetOf<SearchResult>({ s1, s2 -> s2.rssi - s1.rssi })
    private val writeResponse = IBleWriteResponse {}
    private var isConnected = false

    fun isConnected(context: Context, completion: (Result<Pair<String, String>>) -> Unit) {
        val mBluetoothStateListener = object : IABluetoothStateListener() {
            override fun onBluetoothStateChanged(openOrClosed: Boolean) {
                isConnected = openOrClosed
            }
        }
        val managerInstance = VPOperateManager.getMangerInstance(context)
        managerInstance.registerBluetoothStateListener(mBluetoothStateListener)
        completion(Result.success(Pair("isConnected", isConnected.toString())))
    }
    fun startConnection(context: Context, deviceAddress: String, onComplete: (Result<Map<String, Boolean>>) -> Unit) {
        VPOperateManager.getMangerInstance(context).stopScanDevice()
        VPOperateManager.getMangerInstance(context)
            .connectDevice(deviceAddress, { connectState, _, _ ->
                if (connectState == Code.REQUEST_SUCCESS) {
                    isConnected = true
                    onComplete(Result.success(mapOf("startConnection" to true)))
                }
            }) { _ -> }
    }
    fun disconnect(context: Context, completion: (Result<Pair<String, String>>) -> Unit) {
        VPOperateManager.getMangerInstance(context).disconnectWatch {
            isConnected = false
            completion(Result.success(Pair("disconnect", true.toString())))
        }
    }

    fun startScan(context: Context, onComplete: (Result<Map<String, List<Map<String, String>>>>) -> Unit) {
        VPOperateManager.getMangerInstance(context).startScanDevice(object : SearchResponse {
            override fun onDeviceFounded(searchResult: SearchResult?) {
                if (searchResult == null) {
                    Log.d("WatchManager", "Peripheral data is nil")
                } else {
                    addPeripheralToDeviceArray(searchResult)
                }
            }

            override fun onSearchStarted() {
                Log.d("WatchManager", "onSearchStarted")
            }

            override fun onSearchStopped() {
                onComplete(Result.success(mapOf("deviceArray" to peripherals.map {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT ) != PackageManager.PERMISSION_GRANTED) {
                        return
                    }
                    mapOf("name" to it.device.name, "deviceAddress" to it.device.address)
                })))
            }


            override fun onSearchCanceled() {
                Log.d("WatchManager", "onSearchCanceled")
            }
        })
    }

    private fun addPeripheralToDeviceArray(searchResult: SearchResult) {
        peripherals.add(searchResult)
    }

    fun getHeartRate(context: Context, date: String,  completion: (Result<Pair<String, String>>) -> Unit) {
        VPOperateManager.getMangerInstance(context)
            .confirmDevicePwd({}, { _ ->

                VPOperateManager.getMangerInstance(context).startDetectHeart(writeResponse) {
                    val heartRate = it.data
                    if (heartRate != 0) {
                        completion(Result.success(Pair("HEART_RATE", heartRate.toString())))
                        VPOperateManager.getMangerInstance(context).stopDetectHeart(writeResponse)
                    } else {
                        val timer = Timer()
                        timer.schedule(object : TimerTask() {
                            override fun run() {
                                completion(Result.success(Pair("HEART_RATE", "0")))
                                timer.cancel()
                            }
                        }, 5000)
                    }
                }
            }, {}, object : ISocialMsgDataListener {
                override fun onSocialMsgSupportDataChange(p0: FunctionSocailMsgData?) {}
                override fun onSocialMsgSupportDataChange2(p0: FunctionSocailMsgData?) {}
            },
                "0000", false
            )
    }

    fun getBloodPressure(context: Context, date: String, completion: (Result<Pair<String, String>>) -> Unit) {
        VPOperateManager.getMangerInstance(context)
            .confirmDevicePwd({}, { _ ->
                val response = object : IBleWriteResponse {
                    override fun onResponse(var1: Int) {
                        // handle response
                    }
                }
                val listener = IBPDetectDataListener {
                    // handle data change
                    if (it.progress == 100) {
                        val bloodPressure = "${it.highPressure}/${it.lowPressure}"
                        completion(Result.success(Pair("BLOOD_PRESSURE", bloodPressure)))
                    } else {
                        println(it)
                    }
                }
                val model = EBPDetectModel.DETECT_MODEL_PUBLIC
                VPOperateManager.getMangerInstance(context).startDetectBP(response, listener, model)
            }, {}, object : ISocialMsgDataListener {
                override fun onSocialMsgSupportDataChange(p0: FunctionSocailMsgData?) {}
                override fun onSocialMsgSupportDataChange2(p0: FunctionSocailMsgData?) {}
            }, "0000", false)
    }

    fun getPedometer(context: Context, date: String, completion: (Result<Pair<String, String>>) -> Unit) {
        VPOperateManager.getMangerInstance(context)
            .confirmDevicePwd({}, { _ ->
                VPOperateManager.getMangerInstance(context).readSportStep(writeResponse) {
                    val steps = it.step
                    val distance = it.dis
                    val stepsAndDistance = "${it.step}/${it.dis}"
                    completion(Result.success(Pair("STEPS_AND_DISTANCE", stepsAndDistance)))
                }
            }, {}, object : ISocialMsgDataListener {
                override fun onSocialMsgSupportDataChange(p0: FunctionSocailMsgData?) {}
                override fun onSocialMsgSupportDataChange2(p0: FunctionSocailMsgData?) {}
            }, "0000", false)
    }

    /*
    fun getSleep(context: Context, date: String, completion: (Result<String>) -> Unit) {
        VPOperateManager.getMangerInstance(context)
            .confirmDevicePwd({}, { _ ->

                VPOperateManager.getMangerInstance(context).readSleepData(writeResponse, {}, {
                    completion(Result.success(it.toString()))
                }

            }, {}, object : ISocialMsgDataListener {
                override fun onSocialMsgSupportDataChange(p0: FunctionSocailMsgData?) {}
                override fun onSocialMsgSupportDataChange2(p0: FunctionSocailMsgData?) {}
            }, "0000", false)
    }
    */
}