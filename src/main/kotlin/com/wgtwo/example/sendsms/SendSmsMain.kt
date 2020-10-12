package com.wgtwo.example.sendsms

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.transformAll
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.wgtwo.api.util.phonenumber.PhoneNumbers

object SendSms : CliktCommand() {
    private val regionCode by option("--region", help = "Default region code", envvar = "REGION_CODE").default("ZZ")
    private val from by option("-f", "--from", help = "Sender", envvar = "FROM").required()
    private val to by argument(help = "Receiver")
    private val content: String by argument(help = "SMS body content").multiple(required = true)
        .transformAll { list ->
            list.joinToString(separator = " ")
        }

    override fun run() {
        val subscriber = parsePhoneNumber(to, regionCode) ?: throw Exception("Invalid number")

        SendSmsDemo.sendToSubscriber(
            from = from,
            to = subscriber,
            content = content
        )
    }

    private fun parsePhoneNumber(number: String, regionCode: String) =
        PhoneNumbers.of(number, regionCode)?.let { PhoneNumbers.toPhoneNumberProto(it) }
}

fun main(args: Array<String>) = SendSms.main(args)
