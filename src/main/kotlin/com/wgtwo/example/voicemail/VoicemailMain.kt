package com.wgtwo.example.voicemail

fun main() {
    val msisdn = "4799900111" // replace with your desired msisdn

    VoicemailDemo.listVoicemails(msisdn)
    VoicemailDemo.playAllVoicemailForMsisdn(msisdn)
}
