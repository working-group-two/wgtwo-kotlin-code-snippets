package com.wgtwo.example.receivesms

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.wgtwo.api.sms.v0.SmsProto

object ReceiveSms : CliktCommand() {
    val msisdn by argument()

    override fun run() {
        ReceiveSmsService.smsReceivedObservers.add(MySmsListener(msisdn))
    }
}

class MySmsListener(val msisdn: String) : SmsReceiver {
    override fun onReceived(sms: SmsProto.Text) {
        println("Received an SMS")

        val fromAddress: String = when (sms.fromAddressCase) {
            SmsProto.Text.FromAddressCase.FROM_E164 -> sms.fromE164.e164
            SmsProto.Text.FromAddressCase.FROM_NATIONAL_PHONE_NUMBER -> sms.fromNationalPhoneNumber.nationalPhoneNumber
            SmsProto.Text.FromAddressCase.FROM_TEXT_ADDRESS -> sms.fromTextAddress.textAddress
            else -> "unknown"
        }

        val toAddress: String = when (sms.toAddressCase) {
            SmsProto.Text.ToAddressCase.TO_E164 -> sms.toE164.e164
            SmsProto.Text.ToAddressCase.TO_NATIONAL_PHONE_NUMBER -> sms.toNationalPhoneNumber.nationalPhoneNumber
            else -> "unknown"
        }

        val shouldShow = when (sms.direction) {
            SmsProto.Text.Direction.FROM_SUBSCRIBER -> fromAddress == msisdn
            SmsProto.Text.Direction.TO_SUBSCRIBER -> toAddress == msisdn
            else -> false
        }

        if (shouldShow) {
            println("SMS: from=${fromAddress} to=${toAddress}")
        }
    }
}

fun main(args: Array<String>) = ReceiveSms.main(args)
