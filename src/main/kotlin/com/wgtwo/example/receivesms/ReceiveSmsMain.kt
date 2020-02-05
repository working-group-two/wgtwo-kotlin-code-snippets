package com.wgtwo.example.receivesms

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import io.omnicate.messaging.protobuf.Messagecore

object ReceiveSms : CliktCommand() {
    val msisdn by argument()

    override fun run() {
        ReceiveSmsService.smsReceivedObservers.add(MySmsListener(msisdn))
    }
}

class MySmsListener(val msisdn: String) : SmsReceiver {
    override fun onReceived(sms: Messagecore.Message) {
        println("Received an SMS")

        val incomingSmsToMsisdn = sms.direction == Messagecore.Direction.INCOMING
                && sms.toAddress == msisdn.toAddressProto()

        val outgoingSmsFromMsisdn = sms.direction == Messagecore.Direction.OUTGOING
                && sms.fromAddress == msisdn.toAddressProto()

        if (incomingSmsToMsisdn) {
            println("$msisdn received sms from ${sms.fromAddress.number}")
        } else if (outgoingSmsFromMsisdn) {
            println("$msisdn sent sms to ${sms.toAddress.number}")
        }
    }
}

fun String.toAddressProto(): Messagecore.Address = Messagecore.Address.newBuilder()
    .setNumber(this)
    .setType(Messagecore.Address.Type.INTERNATIONAL_NUMBER)
    .build()

fun main(args: Array<String>) = ReceiveSms.main(args)
