package com.zwyl.mqtt

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.lang.Exception
import java.util.*

/**
 * 管理mqtt
 */
class MqttManager {

    /**
     * 获取mqtt客户端，自定义一些操作

     * @return MqttAndroidClient`
     */
    var mqttAndroidClient: MqttAndroidClient? = null
        private set
    private var isDebug: Boolean? = false//是否调试
    private val topicsLisenter = HashMap<String, ArrayList<IMqttMessageListener>>()//维护注册的回调


    /**
     * 设置是否打开调试

     * @param debug 是否调试
     */
    fun setDebug(debug: Boolean?) {
        this.isDebug = debug
    }


    /**
     * 连接mqtt服务器

     * @param context  context
     * *                 *
     * *
     * @param clientId 唯一的客户端id(建议使用登录的用户名，这个是唯一的)
     * *                 *
     * *
     * @param server   server地址(ip或域名)
     * *                 *
     * *
     * @param port     端口号
     */
    fun connect(context: Context, clientId: String, server: String, port: Int) {
        val mqttUrlFormat = "tcp://%s:%d"
        val url = String.format(Locale.US, mqttUrlFormat, server, port)
        mqttAndroidClient = MqttAndroidClient(context, url, clientId)
        mqttAndroidClient!!.setCallback(object : MqttCallbackExtended {
            override fun connectionLost(cause: Throwable) {
                log("The Connection was lost.")
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                log("Incoming message: " + String(message.payload))
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {

            }

            override fun connectComplete(reconnect: Boolean, serverURI: String) {
                if (reconnect) {
                    log("Reconnected to : " + serverURI)
                    // Because Clean Session is true, we need to re-subscribe
                    for (s in topicsLisenter.keys) {
                        subscribeToTopic(s)
                    }

                } else {
                    log("Connected to: " + serverURI)
                }
            }
        })


        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = false

        try {
            //log("Connecting to " + serverUri);
            mqttAndroidClient!!.connect(mqttConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    val disconnectedBufferOptions = DisconnectedBufferOptions()
                    disconnectedBufferOptions.isBufferEnabled = true
                    disconnectedBufferOptions.bufferSize = 100
                    disconnectedBufferOptions.isPersistBuffer = false
                    disconnectedBufferOptions.isDeleteOldestMessages = false
                    mqttAndroidClient!!.setBufferOpts(disconnectedBufferOptions)
                    for (s in topicsLisenter.keys) {
                        subscribeToTopic(s)
                    }
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    log("Failed to connect to: " + url)
                }
            })
        } catch (ex: MqttException) {
            ex.printStackTrace()
        }


    }

    /**
     * 用于debug调试

     * @param mainText 打印的信息
     */
    private fun log(mainText: String) {
        if (isDebug!!) {
            Log.i(MqttManager::class.java.simpleName, "LOG: " + mainText)
        }

    }


    /**
     * 订阅主题，查看推送消息

     * @param subscriptionTopic 订阅的主题
     * *                          *
     * *
     * @param listener          订阅的回调
     */
    fun subscribeToTopic(subscriptionTopic: String, listener: IMqttMessageListener) {
        subscribeToTopic(subscriptionTopic, 2, listener)
    }

    /**
     * 订阅主题，查看推送消息

     * @param subscriptionTopic 订阅的主题
     * *                          *
     * *
     * @param listener          订阅的回调
     */
    fun subscribeToTopic(subscriptionTopic: String, level: Int, listener: IMqttMessageListener) {
        var list: ArrayList<IMqttMessageListener>? = topicsLisenter[subscriptionTopic]
        if (list == null) {
            list = ArrayList<IMqttMessageListener>()
            topicsLisenter.put(subscriptionTopic, list)
            subscribeToTopic(subscriptionTopic, level)
        }
        list.add(listener)

    }


    /**
     * 取消订阅

     * @param subscriptionTopic 订阅的主题
     * *                          *
     * *
     * @param listener          订阅的回调
     */
    fun unSubscribeToTopic(subscriptionTopic: String, listener: IMqttMessageListener) {
        val list = topicsLisenter[subscriptionTopic]
        if (list != null) {
            list.remove(listener)
        } else {
            try {
                mqttAndroidClient!!.unsubscribe(subscriptionTopic)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    /**
     * @param subscriptionTopic 订阅主题
     */
    private fun subscribeToTopic(subscriptionTopic: String, level: Int = 2 ) {
        try {
            mqttAndroidClient!!.subscribe(subscriptionTopic, level, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    log(subscriptionTopic + ":  Subscribed!")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    log(subscriptionTopic + ":  Failed to subscribe")
                }

            })

            // THIS DOES NOT WORK!
            mqttAndroidClient!!.subscribe(subscriptionTopic, level) { topic, message ->
                // message Arrived!
                log("Message: " + topic + " : " + String(message.payload))
                val list = topicsLisenter[subscriptionTopic]
                if (list != null) {
                    for (lisenter in list) {
                        lisenter.messageArrived(topic, message)
                    }
                }
            }

        } catch (ex: Exception) {
            log("Exception whilst subscribing")
        }

    }

    /**
     * 像指定的主题发送消息

     * @param publishTopic   主题
     * *                       *
     * *
     * @param publishMessage 发布消息
     */
    fun publishMessage(publishTopic: String, publishMessage: String) {
        try {
            val message = MqttMessage()
            message.payload = publishMessage.toByteArray()
            mqttAndroidClient!!.publish(publishTopic, message)
            log("Message Published")
            if (!mqttAndroidClient!!.isConnected) {
                log(mqttAndroidClient!!.bufferedMessageCount.toString() + " messages in buffer.")
            }
        } catch (e: Exception) {
            log("Error Publishing: " + e.message)
        }

    }

    /**
     * 关闭连接
     */
    fun disconnect() {
        try {
            mqttAndroidClient!!.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mqttAndroidClient = null
        topicsLisenter.clear()
    }

}
