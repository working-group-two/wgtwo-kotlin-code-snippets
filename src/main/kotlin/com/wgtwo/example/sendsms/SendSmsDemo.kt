package com.wgtwo.example.sendsms

import com.wgtwo.api.common.v0.PhoneNumberProto
import com.wgtwo.api.sms.v0.SmsProto
import com.wgtwo.api.sms.v0.SmsServiceGrpc
import com.wgtwo.example.Shared.channel
import com.wgtwo.example.Shared.credentials
import io.grpc.StatusRuntimeException

object SendSmsDemo {
    private val blockingStub = SmsServiceGrpc.newBlockingStub(channel).withCallCredentials(credentials)

    fun sendToSubscriber(
        from: String,
        to: PhoneNumberProto.PhoneNumber,
        content: String
    ) {
        val message = SmsProto.SendTextToSubscriberRequest.newBuilder()
            .setToSubscriber(to)
            .setFromTextAddress(PhoneNumberProto.TextAddress.newBuilder().setTextAddress(from).build())
            .setContent(content)
            .build()

        val sendResult = try {
            blockingStub.sendTextToSubscriber(message)
        } catch (e: StatusRuntimeException) {
            println("Exception: ${e.status}")
            return
        }
        val status = sendResult.status
        if (status == SmsProto.SendResponse.SendStatus.SEND_OK) {
            println("Successfully sent message to ${to.e164}, from $from")
        } else {
            println("Failed to send message to ${to.e164}, from $from. Got status: $status. Description: ${sendResult.description}")
        }
    }
}
