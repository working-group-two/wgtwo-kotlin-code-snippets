package com.wgtwo.example.sendsms

import com.wgtwo.api.util.auth.Clients
import com.wgtwo.api.util.auth.OperatorToken
import com.wgtwo.example.Secrets
import io.omnicate.messaging.protobuf.MessageCoreGrpc
import io.omnicate.messaging.protobuf.Messagecore

typealias Msisdn = String

object SendSmsDemo {
    private val channel = Clients.createChannel(Clients.Environment.PROD)
    private val credentials = OperatorToken(Secrets.WGTWO_CLIENT_ID, Secrets.WGTWO_CLIENT_SECRET)
    private val blockingStub = MessageCoreGrpc.newBlockingStub(channel).withCallCredentials(credentials)

    fun sendSms(
        from: Msisdn,
        to: Msisdn,
        content: String,
        direction: Messagecore.Direction = Messagecore.Direction.OUTGOING
    ) {
        val message = Messagecore.TextMessage.newBuilder()
            .setBody(content)
            .setToAddress(to.toAddress())
            .setFromAddress(from.toAddress())
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

    fun Msisdn.toAddress(): Messagecore.Address = Messagecore.Address.newBuilder()
        .setNumber(this)
        .setType(Messagecore.Address.Type.INTERNATIONAL_NUMBER)
        .build()
}
