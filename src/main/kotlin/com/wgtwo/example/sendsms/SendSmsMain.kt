package com.wgtwo.example.sendsms

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.transformAll
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

object SendSms: CliktCommand() {
    private val from by option("-f", "--from", help = "From msisdn", envvar = "FROM").required()
    private val to by argument(help = "To msisdn")
    private val content: String by argument(help = "SMS body content").multiple(required = true)
        .transformAll { list ->
            list.joinToString(separator = " ")
        }

    override fun run() = SendSmsDemo.sendSms(
        from = from,
        to = to,
        content = content
    )
}

fun main(args: Array<String>) = SendSms.main(args)
