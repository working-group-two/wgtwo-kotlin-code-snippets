package com.wgtwo.example.sendsms

import com.wgtwo.example.Shared.channel
import com.wgtwo.example.Shared.credentials
import io.omnicate.messaging.protobuf.MessageCoreGrpc
import io.omnicate.messaging.protobuf.Messagecore

fun String.toAddress(type: Messagecore.Address.Type): Messagecore.Address = Messagecore.Address.newBuilder()
    .setNumber(this)
    .setType(type)
    .build()

object SendSmsDemo {
    private val blockingStub = MessageCoreGrpc.newBlockingStub(channel).withCallCredentials(credentials)

    fun sendSms(
        from: Messagecore.Address,
        to: Messagecore.Address,
        content: String,
        direction: Messagecore.Direction = Messagecore.Direction.OUTGOING
    ) {
        val message = Messagecore.TextMessage.newBuilder()
            .setFromAddress(from)
            .setToAddress(to)
            .setBody(content)
            .setDirection(direction)
            .build()

        val sendResult = blockingStub.sendTextMessage(message)
        val status = sendResult.status
        if (status == Messagecore.SendAttemptStatus.SEND_OK) {
            println("Successfully sent message to $to, from $from")
        } else {
            println("Failed to send message to $to, from $from. Got status: $status. Description: ${sendResult.description}")
        }
    }
}
