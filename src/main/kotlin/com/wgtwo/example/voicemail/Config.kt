package com.wgtwo.example.voicemail

import java.lang.System.getenv

object Config {
    val MSISDN = getenv("MSISDN") ?: throw IllegalStateException("Missing MSISDN from environment")
}
