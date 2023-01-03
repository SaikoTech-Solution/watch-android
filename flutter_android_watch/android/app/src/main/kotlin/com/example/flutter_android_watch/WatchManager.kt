package com.example.flutter_android_watch

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.inuker.bluetooth.library.Code
import com.inuker.bluetooth.library.search.SearchResult
import com.inuker.bluetooth.library.search.response.SearchResponse
import com.veepoo.protocol.VPOperateManager
import com.veepoo.protocol.model.datas.HeartData
import io.flutter.Log
import com.veepoo.protocol.listener.base.IBleWriteResponse
import com.veepoo.protocol.listener.data.IPwdDataListener
import com.veepoo.protocol.listener.data.ISocialMsgDataListener
import com.veepoo.protocol.model.datas.FunctionSocailMsgData
import com.veepoo.protocol.model.datas.PwdData

object WatchManager {

    private var deviceArray = sortedSetOf<SearchResult>({ s1, s2 -> s2.rssi - s1.rssi })
    private val writeResponse = IBleWriteResponse {}

    fun startScan(context: Context, onComplete: (Result<List<Map<String, String>>>) -> Unit) {
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
                onComplete(Result.success(deviceArray.map {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT ) != PackageManager.PERMISSION_GRANTED) {
                        return
                    }
                    mapOf("name" to it.device.name, "deviceAddress" to it.device.address)
                }))
            }

            override fun onSearchCanceled() {
                Log.d("WatchManager", "onSearchCanceled")
            }
        })
    }

    fun addPeripheralToDeviceArray(searchResult: SearchResult) {
        deviceArray.add(searchResult)
    }

    fun startConnection(context: Context, deviceAddress: String, onComplete: (Result<String>) -> Unit) {
        VPOperateManager.getMangerInstance(context).stopScanDevice()
        VPOperateManager.getMangerInstance(context)
            .connectDevice(deviceAddress, { connectState, _, _ ->
                if (connectState == Code.REQUEST_SUCCESS) {
                    onComplete(Result.success("SUCCESS"))
                }
            }) { _ -> }
    }

    fun getHeartRate(context: Context, date: String, completion: (Result<String>) -> Unit) {
        VPOperateManager.getMangerInstance(context)
            .confirmDevicePwd({}, { _ ->
                VPOperateManager.getMangerInstance(context).startDetectHeart(writeResponse) {
                    completion(Result.success(it.toString()))
                    VPOperateManager.getMangerInstance(context).stopDetectHeart(writeResponse)
                }
            }, {}, object : ISocialMsgDataListener {
                override fun onSocialMsgSupportDataChange(p0: FunctionSocailMsgData?) {}
                override fun onSocialMsgSupportDataChange2(p0: FunctionSocailMsgData?) {}
            },
                "0000", false
            )
    }

    fun getBloodPressure(context: Context, date: String, completion: (Result<String>) -> Unit) {
        VPOperateManager.getMangerInstance(context)
            .confirmDevicePwd({}, { _ ->
                VPOperateManager.getMangerInstance(context).readDetectBP(writeResponse) {
                    completion(Result.success(it.toString()))
                }
            }, {}, object : ISocialMsgDataListener {
                override fun onSocialMsgSupportDataChange(p0: FunctionSocailMsgData?) {}
                override fun onSocialMsgSupportDataChange2(p0: FunctionSocailMsgData?) {}
            }, "0000", false)
    }

    fun getPedometer(context: Context, date: String, completion: (Result<String>) -> Unit) {
        VPOperateManager.getMangerInstance(context)
            .confirmDevicePwd({}, { _ ->
                VPOperateManager.getMangerInstance(context).readSportStep(writeResponse) {
                    completion(Result.success(it.toString()))
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