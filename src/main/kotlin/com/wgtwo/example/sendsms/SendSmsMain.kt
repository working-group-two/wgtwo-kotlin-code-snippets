package com.wgtwo.example.sendsms

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.transformAll
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.choice
import io.omnicate.messaging.protobuf.Messagecore

object SendSms : CliktCommand() {
    private val from by option("-f", "--from", help = "Sender", envvar = "FROM").required()
    private val to by argument(help = "Receiver")
    private val content: String by argument(help = "SMS body content").multiple(required = true)
        .transformAll { list ->
            list.joinToString(separator = " ")
        }
    private val direction by option("-d", "--direction", envvar = "DIRECTION")
        .choice(
            mapOf(
                Messagecore.Direction.OUTGOING.toString() to Messagecore.Direction.OUTGOING,
                Messagecore.Direction.INCOMING.toString() to Messagecore.Direction.INCOMING
            )
        )
        .default(Messagecore.Direction.OUTGOING)
    private val fromType by option("--from-type")
        .choice(
            mapOf(
                Messagecore.Address.Type.INTERNATIONAL_NUMBER.toString() to Messagecore.Address.Type.INTERNATIONAL_NUMBER,
                Messagecore.Address.Type.TEXT.toString() to Messagecore.Address.Type.TEXT
            )
        )
        .default(Messagecore.Address.Type.INTERNATIONAL_NUMBER)

    private val toType by option("--to-type")
        .choice(
            mapOf(
                Messagecore.Address.Type.INTERNATIONAL_NUMBER.toString() to Messagecore.Address.Type.INTERNATIONAL_NUMBER,
                Messagecore.Address.Type.TEXT.toString() to Messagecore.Address.Type.TEXT
            )
        )
        .default(Messagecore.Address.Type.INTERNATIONAL_NUMBER)

    override fun run() = SendSmsDemo.sendSms(
        from = from.toAddressProto(fromType),
        to = to.toAddressProto(toType),
        content = content,
        direction = direction
    )
}

fun main(args: Array<String>) = SendSms.main(args)
