package com.wgtwo.example.sendsms

import io.omnicate.messaging.protobuf.Messagecore

fun main(args: Array<String>) {
    SendSmsDemo.sendSms(
        content = "Hi! 🤗",
        direction = Messagecore.Direction.OUTGOING
    )
}
