package com.wgtwo.example

import com.wgtwo.api.util.auth.Clients
import com.wgtwo.api.util.auth.OperatorToken

object Shared {
    val channel = Clients.createChannel(Clients.Environment.PROD)
    val credentials = OperatorToken(Secrets.WGTWO_CLIENT_ID, Secrets.WGTWO_CLIENT_SECRET)
}
