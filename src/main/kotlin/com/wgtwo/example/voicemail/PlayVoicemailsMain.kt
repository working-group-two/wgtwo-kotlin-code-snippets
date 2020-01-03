package com.wgtwo.example.voicemail

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

fun main(args: Array<String>) = Voicemail()
    .subcommands(List(), Play(), MarkRead(), Delete())
    .main(args)

class Voicemail: CliktCommand() {
    override fun run() = Unit
}

class List: CliktCommand() {
    val msisdn by option("-m", "--msisdn", help = "Msisdn", envvar = "MSISDN").required()

    override fun run() {
        val voicemails = VoicemailDemo.listVoicemails(msisdn)

        if (voicemails == null) {
            println("Failed to get any voicemails")
            return
        }

        println("Number of voicemails: ${voicemails.size}")

        for (voicemailMetadata in voicemails) {
            println(voicemailMetadata)
        }
    }
}

class Play: CliktCommand() {
    val voicemailId by argument()

    override fun run() {
        VoicemailDemo.playVoicemail(voicemailId)
    }
}

class MarkRead: CliktCommand() {
    val voicemailId by argument()

    override fun run() {
        VoicemailDemo.markVoicemailAsRead(voicemailId)
    }
}

class Delete: CliktCommand() {
    val voicemailId by argument()

    override fun run() {
        VoicemailDemo.markVoicemailAsRead(voicemailId)
    }
}
