package com.wgtwo.example.voicemail

fun main() {
    val msisdn = Config.MSISDN
    val voicemails = VoicemailDemo.listVoicemails(msisdn)

    if (voicemails == null) {
        println("Failed to get any voicemails")
        return
    }

    println("Number of voicemails: ${voicemails.size}")
    
    for (voicemailMetadata in voicemails) {
        println(voicemailMetadata)
        VoicemailDemo.playVoicemail(voicemailMetadata.voicemailId)
    }
}
