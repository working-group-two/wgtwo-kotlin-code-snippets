package com.wgtwo.example.voicemail

import com.wgtwo.example.Config

fun main() {
    VoicemailDemo.listVoicemails(Config.MSISDN)
    VoicemailDemo.playAllVoicemailForMsisdn(Config.MSISDN)
}
