package com.wgtwo.example.receivesms

import com.wgtwo.api.sms.v0.SmsProto
import com.wgtwo.api.sms.v0.SmsServiceGrpc
import com.wgtwo.example.Shared
import io.grpc.Context
import io.grpc.Status
import io.grpc.StatusException
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

interface SmsReceiver {
    fun onReceived(sms: SmsProto.Text)
}

object ReceiveSmsService {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val context: Context.CancellableContext = Context.current().withCancellation()

    private val stub = SmsServiceGrpc
        .newStub(Shared.channel)
        .withCallCredentials(Shared.credentials)
        .withWaitForReady()

    val smsReceivedObservers = mutableSetOf<SmsReceiver>()

    init {
        executor.submit { listenForSms() }
    }

    fun listenForSms() {
        logger.info("Starting to listen for SMS")
        context.run {
            // call GRPC method receiveMessages and register ReceiveMessagesObserver
            stub.receiveText(
                SmsProto.ReceiveTextRequest.getDefaultInstance(),
                ReceiveMessagesObserver
            )
        }
    }

    fun close() {
        executor.shutdown()
        context.cancel(null)
        executor.awaitTermination(1, TimeUnit.SECONDS)
    }

    private object ReceiveMessagesObserver : StreamObserver<SmsProto.Text> {

        // Handle new messages
        override fun onNext(text: SmsProto.Text) {
            SmsReceivedAcker.ackMessage(text)
            smsReceivedObservers.parallelStream().forEach {
                it.onReceived(text)
            }
        }

        // Disconnects can happen and need to be handled
        override fun onError(t: Throwable) {
            logger.warn("${t.message}")

            val status = getStatus(t)
            if (status != Status.UNAVAILABLE) {
                logger.info("Sleeping for 1s...")
                restart(wait = Duration.ofSeconds(1))
            } else {
                restart()
            }
        }

        fun getStatus(throwable: Throwable): Status? = when (throwable) {
            is StatusRuntimeException -> throwable.status
            is StatusException -> throwable.status
            else -> null
        }

        // Stream can also be closed without error, and in this scenario you should also reconnect
        override fun onCompleted() {
            logger.info("onCompleted called")
            restart()
        }

        private fun restart(wait: Duration = Duration.ZERO) {
            // check that the operation hasn't been cancelled, e.g. by the server shutting down
            if (!context.isCancelled) {
                executor.schedule({ listenForSms() }, wait.toMillis(), TimeUnit.MILLISECONDS)
            }
        }


        private object SmsReceivedAcker {
            fun ackMessage(sms: SmsProto.Text) {
                val ackMessageRequest = SmsProto.AckRequest.newBuilder()
                    .setId(sms.id)
                    .setStatus(SmsProto.AckRequest.ReceiveStatus.RECEIVE_OK)
                    .build()
                stub.ack(ackMessageRequest, AckMessageObserver)
            }
        }

        object AckMessageObserver : StreamObserver<SmsProto.AckResponse> {
            override fun onNext(value: SmsProto.AckResponse) {}

            override fun onError(t: Throwable) {
                logger.warn("${t.message}")
            }

            override fun onCompleted() {
                logger.debug("Completed ack")
            }
        }
    }
}
