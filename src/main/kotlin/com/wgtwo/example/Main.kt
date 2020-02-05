package com.wgtwo.example

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.wgtwo.example.receivesms.ReceiveSms
import com.wgtwo.example.sendsms.SendSms
import com.wgtwo.example.voicemail.Voicemail

fun main(args: Array<String>) = Command
    .subcommands(SendSms, Voicemail, ReceiveSms)
    .main(args)

object Command: CliktCommand() {
    override fun run() = Unit
}
