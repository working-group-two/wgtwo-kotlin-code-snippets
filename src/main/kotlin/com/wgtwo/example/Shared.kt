package com.wgtwo.example

import com.wgtwo.api.common.Environment
import com.wgtwo.api.util.auth.Clients
import com.wgtwo.api.util.auth.OperatorToken
import io.grpc.ManagedChannel

object Shared {
    val channel: ManagedChannel = Clients.createChannel(Environment.PROD)
    val credentials = OperatorToken(Secrets.WGTWO_CLIENT_ID, Secrets.WGTWO_CLIENT_SECRET)
}
