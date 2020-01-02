package com.wgtwo.example.sendsms

import io.omnicate.messaging.protobuf.Messagecore
import java.lang.System.getenv

object Config {
    val FROM = getenv("FROM") ?: throw IllegalStateException("Missing FROM from environment")
    val TO = getenv("TO") ?: throw IllegalStateException("Missing TO from environment")
    val CONTENT = getenv("CONTENT") ?: throw IllegalStateException("Missing CONTENT from environment")
    val DIRECTION = getenv("DIRECTION") ?: Messagecore.Direction.OUTGOING.name // optional
}
