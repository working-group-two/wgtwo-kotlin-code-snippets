package com.wgtwo.example.sendsms

import com.wgtwo.api.util.auth.Clients
import com.wgtwo.api.util.auth.OperatorToken
import com.wgtwo.example.Secrets
import io.grpc.ManagedChannel
import io.omnicate.messaging.protobuf.MessageCoreGrpc
import io.omnicate.messaging.protobuf.Messagecore

typealias Msisdn = String

object SendSmsDemo {
    val channel: ManagedChannel = Clients.createChannel(Clients.Environment.PROD)
    val credentials: OperatorToken = OperatorToken(Secrets.WGTWO_CLIENT_ID, Secrets.WGTWO_CLIENT_SECRET)
    val blockingStub: MessageCoreGrpc.MessageCoreBlockingStub = MessageCoreGrpc.newBlockingStub(channel).withCallCredentials(credentials)

    fun sendSms(
        content: String,
        toAddress: Msisdn,
        fromAddress: Msisdn,
        direction: Messagecore.Direction = Messagecore.Direction.OUTGOING
    ) {
        val message = Messagecore.TextMessage.newBuilder()
            .setBody(content)
            .setToAddress(toAddress.toMessagecoreAddress())
            .setFromAddress(fromAddress.toMessagecoreAddress())
            .setDirection(direction)
            .build()

        val sendResult = blockingStub.sendTextMessage(message)
        val status = sendResult.status
        if (status == Messagecore.SendAttemptStatus.SEND_OK) {
            println("Successfully sent message to $toAddress, from $fromAddress")
        } else {
            println("Failed to send message to $toAddress, from $fromAddress. Got status: $status. Description: ${sendResult.description}")
        }
    }

    fun Msisdn.toMessagecoreAddress(): Messagecore.Address = Messagecore.Address.newBuilder()
        .setNumber(this)
        .setType(Messagecore.Address.Type.INTERNATIONAL_NUMBER)
        .build()
}
