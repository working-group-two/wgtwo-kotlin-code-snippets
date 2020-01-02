package com.wgtwo.example.sendsms

import io.omnicate.messaging.protobuf.Messagecore

fun main() {
    SendSmsDemo.sendSms(
        from = Config.FROM,
        to = Config.TO,
        content = Config.CONTENT,
        direction = Messagecore.Direction.valueOf(Config.DIRECTION)
    )
}
