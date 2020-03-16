package com.wgtwo.example.sendbinarysms

import com.google.protobuf.ByteString
import com.wgtwo.example.Shared.channel
import com.wgtwo.example.Shared.credentials
import io.omnicate.messaging.protobuf.MessageCoreGrpc
import io.omnicate.messaging.protobuf.Messagecore
import java.time.Instant
import java.time.temporal.TemporalUnit

const val MAX_FRAGMENT_LENGTH = 160

fun String.toAddressProto(type: Messagecore.Address.Type): Messagecore.Address = Messagecore.Address.newBuilder()
    .setNumber(this)
    .setType(type)
    .build()

object SendBinarySmsDemo {
    private val blockingStub = MessageCoreGrpc.newBlockingStub(channel).withCallCredentials(credentials)

    private fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    fun sendBinarySms(
        from: Messagecore.Address,
        to: Messagecore.Address,
        content: String,
        direction: Messagecore.Direction = Messagecore.Direction.OUTGOING,
        messageClass: Messagecore.SmsFragmentedContent.MessageClass
    ) {
        val binaryContent = ByteString.copyFrom(hexStringToByteArray(content))
        if (binaryContent.size() > MAX_FRAGMENT_LENGTH) {
            println("Failed to send message: content is too big.")
            return
        }
        val message = with(Messagecore.Message.newBuilder()) {
            this.fromAddress = from
            this.toAddress = to
            this.fragments = with(Messagecore.SmsFragmentedContent.newBuilder()) {
                this.messageClass = messageClass
                this.of = 1
                addFragments(
                    with(Messagecore.SmsFragment.newBuilder()) {
                        this.part = 1
                        this.content = binaryContent
                        this.encoding = Messagecore.Charset.GSM8
                        build()
                    }
                )
                build()
            }
            this.direction = direction
            this.expires = Instant.now().plusSeconds(60 * 20).toEpochMilli()
            build()
        }

        val sendResult = blockingStub.sendMessage(message)
        val status = sendResult.status
        if (status == Messagecore.SendAttemptStatus.SEND_OK) {
            println("Successfully sent message to $to, from $from")
        } else {
            println("Failed to send message to $to, from $from. Got status: $status. Description: ${sendResult.description}")
        }
    }
}
