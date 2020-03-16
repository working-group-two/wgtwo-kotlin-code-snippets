package com.wgtwo.example.sendbinarysms

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.transformAll
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.choice
import io.omnicate.messaging.protobuf.Messagecore

object SendBinarySms : CliktCommand() {
    private val from by option("-f", "--from", help = "Sender", envvar = "FROM").required()
    private val to by argument(help = "Receiver")
    private val content: String by argument(help = "SMS body content as hexadecimal").multiple(required = true)
        .transformAll { list ->
            list.joinToString(separator = " ")
        }
    private val messageClass by option("--message-class")
        .choice(
            mapOf(
                Messagecore.SmsFragmentedContent.MessageClass.CLASS0.toString()
                        to Messagecore.SmsFragmentedContent.MessageClass.CLASS0,
                Messagecore.SmsFragmentedContent.MessageClass.CLASS1.toString()
                        to Messagecore.SmsFragmentedContent.MessageClass.CLASS1,
                Messagecore.SmsFragmentedContent.MessageClass.CLASS2.toString()
                        to Messagecore.SmsFragmentedContent.MessageClass.CLASS2,
                Messagecore.SmsFragmentedContent.MessageClass.CLASS3.toString()
                        to Messagecore.SmsFragmentedContent.MessageClass.CLASS3
            )
        )
        .required()
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


    override fun run() = SendBinarySmsDemo.sendBinarySms(
        from = from.toAddressProto(fromType),
        to = to.toAddressProto(toType),
        content = content,
        direction = direction,
        messageClass = messageClass
    )
}

fun main(args: Array<String>) = SendBinarySms.main(args)
