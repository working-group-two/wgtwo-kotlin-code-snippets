package com.wgtwo.example.voicemail

fun main() {
    VoicemailDemo.listVoicemails(Config.MSISDN)
    VoicemailDemo.playAllVoicemailForMsisdn(Config.MSISDN)
}
