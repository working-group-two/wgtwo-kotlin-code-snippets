package com.wgtwo.example.voicemail

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

fun main(args: Array<String>) = Voicemail.main(args)

object Voicemail : CliktCommand() {
    override fun run() = Unit

    init {
        subcommands(List, Play, MarkRead, Delete)
    }
}

object List : CliktCommand() {
    val e164 by option(
        "-e",
        "--e164",
        help = "Ex. +12024561111",
        envvar = "E164"
    ).required()

    override fun run() {
        val voicemails = VoicemailDemo.listVoicemails(e164)

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

object Play : CliktCommand() {
    val voicemailId by argument()

    override fun run() {
        VoicemailDemo.playVoicemail(voicemailId)
    }
}

object MarkRead : CliktCommand() {
    val voicemailId by argument()

    override fun run() {
        VoicemailDemo.markVoicemailAsRead(voicemailId)
    }
}

object Delete : CliktCommand() {
    val voicemailId by argument()

    override fun run() {
        VoicemailDemo.deleteVoicemail(voicemailId)
    }
}
