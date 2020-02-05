package com.wgtwo.example.receivesms

import com.wgtwo.example.Shared
import io.grpc.Context
import io.grpc.Status
import io.grpc.StatusException
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import io.omnicate.messaging.protobuf.MessageCoreGrpc
import io.omnicate.messaging.protobuf.Messagecore
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

interface SmsReceiver {
    fun onReceived(sms: Messagecore.Message)
}

object ReceiveSmsService {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val context: Context.CancellableContext = Context.current().withCancellation()

    private val messageCoreStub = MessageCoreGrpc
        .newStub(Shared.channel)
        .withCallCredentials(Shared.credentials)
        .withWaitForReady()


    /*
    initialize ReceiveSmsService and then add your SmsReceiver observer to this set to be
    notified when there are new sms received
    ```
        smsReceivedObservers.add(object : SmsReceiver {
            override fun onReceived(sms: Messagecore.Message) {
                // do something with the sms
            }
        })
    ```
    */
    val smsReceivedObservers = mutableSetOf<SmsReceiver>()

    init {
        executor.submit { listenForSms() }
    }

    fun listenForSms() {
        logger.info("Starting to listen for SMS")
        context.run {
            // call GRPC method receiveMessages and register ReceiveMessagesObserver
            messageCoreStub.receiveMessages(
                Messagecore.ReceiveMessagesRequest.getDefaultInstance(),
                ReceiveMessagesObserver
            )
        }
    }

    fun close() {
        executor.shutdown()
        context.cancel(null)
        executor.awaitTermination(1, TimeUnit.SECONDS)
    }

    private object ReceiveMessagesObserver : StreamObserver<Messagecore.MessageBox> {

        // Handle new messages
        override fun onNext(messageBox: Messagecore.MessageBox) {
            logger.info("Received messageBox with ${messageBox.messagesList.size} messages")
            messageBox.messagesList.forEach { message ->

                // Each message needs to be acked or it will be resent
                SmsReceivedAcker.ackMessage(message)
                smsReceivedObservers.parallelStream().forEach {
                    it.onReceived(message)
                }
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
    }


    private object SmsReceivedAcker {
        fun ackMessage(it: Messagecore.Message) {
            val ackMessageRequest = Messagecore.AckMessageRequest.newBuilder()
                .setAckStatus(
                    Messagecore.ReceiveStatus.newBuilder()
                        .setMessageId(it.messageId)
                        .setStatus(Messagecore.ReceiveAttemptStatus.RECEIVE_OK)
                        .build()
                )
                .build()
            messageCoreStub.ackMessage(ackMessageRequest, AckMessageObserver)
        }
    }

    object AckMessageObserver : StreamObserver<Messagecore.AckMessageResponse> {
        override fun onNext(value: Messagecore.AckMessageResponse) {}

        override fun onError(t: Throwable) {
            logger.warn("${t.message}")
        }

        override fun onCompleted() {
            logger.debug("Completed ack")
        }
    }
}
