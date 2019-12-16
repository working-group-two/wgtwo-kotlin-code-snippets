package com.wgtwo.example

import java.lang.System.getenv

object Config {
    val MSISDN = getenv("MSISDN") ?: throw IllegalStateException("Missing MSISDN from environment")
}
