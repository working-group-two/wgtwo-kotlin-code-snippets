package com.wgtwo.example

import java.lang.System.getenv

object Secrets {
    val WGTWO_CLIENT_ID = getenv("WGTWO_CLIENT_ID") ?: throw IllegalStateException("Missing WGTWO_CLIENT_ID from environment")
    val WGTWO_CLIENT_SECRET = getenv("WGTWO_CLIENT_SECRET") ?: throw IllegalStateException("Missing WGTWO_CLIENT_SECRET from environment")
}
