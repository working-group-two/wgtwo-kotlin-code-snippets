package com.wgtwo.example.voicemail

import com.wgtwo.api.auth.Clients
import com.wgtwo.api.common.OperatorToken
import com.wgtwo.example.Secrets
import io.grpc.StatusRuntimeException
import io.omnicate.messaging.protobuf.Voicemail
import io.omnicate.messaging.protobuf.VoicemailMediaServiceGrpc
import javax.sound.sampled.AudioSystem

object VoicemailDemo {
    val channel = Clients.createChannel(Clients.Environment.PROD)
    val credentials = OperatorToken(Secrets.WGTWO_CLIENT_ID, Secrets.WGTWO_CLIENT_SECRET)
    val blockingStub = VoicemailMediaServiceGrpc.newBlockingStub(channel).withCallCredentials(credentials)

    fun listVoicemails(msisdn: String): MutableList<Voicemail.VoicemailMetadata>? {
        val voicemailMetadataRequest = Voicemail.GetAllVoicemailMetadataRequest.newBuilder().setMsisdn(msisdn).build()
        val metadataResponse = try {
            blockingStub.getAllVoicemailMetadata(voicemailMetadataRequest)
        } catch (e: StatusRuntimeException) {
            println(e)
            return null
        }

        val voicemailList = metadataResponse.voicemailsMetadataList
        if (voicemailList.isEmpty()) {
            println("No voicemails for msisdn $msisdn")
        }

        for (voicemailMetadata in voicemailList) {
            println(voicemailMetadata)
        }
        return voicemailList
    }

    fun playAllVoicemailForMsisdn(msisdn: String) {
        val voicemails = listVoicemails(msisdn) ?: return
        for (voicemail in voicemails)
            playVoicemail(voicemail.voicemailId)
    }

    fun playVoicemail(voicemailId: String) {
        val voicemailRequest = Voicemail.GetVoicemailRequest.newBuilder().setVoicemailId(voicemailId).build()

        val voicemail = try {
            blockingStub.getVoicemail(voicemailRequest)
        } catch (e: StatusRuntimeException) {
            println(e)
            return
        }

        val tempFile = createTempFile(prefix = "voicemail", suffix = ".mp3")
        println(tempFile.absoluteFile)
        val outputStream = tempFile.outputStream()
        voicemail.voicemailFile.writeTo(outputStream)
        outputStream.close()

        val audioInputStream = AudioSystem.getAudioInputStream(tempFile)
        val clip = AudioSystem.getClip()
        clip.open(audioInputStream)
        println("Playing voicemail: $voicemailId")
        clip.start()
        Thread.sleep(clip.microsecondLength / 1000)
    }

    fun markVoicemailAsRead(voicemailId: String): Boolean {
        val readRequest = Voicemail.MarkVoicemailAsReadRequest.newBuilder().setVoicemailId(voicemailId).build()

        return try {
            blockingStub.markVoicemailAsRead(readRequest)
            true
        } catch (e: StatusRuntimeException) {
            println(e)
            false
        }
    }

    fun deleteVoicemail(voicemailId: String): Boolean {
        val deleteRequest = Voicemail.DeleteVoicemailRequest.newBuilder().setVoicemailId(voicemailId).build()

        return try {
            blockingStub.deleteVoicemail(deleteRequest)
            true
        } catch (e: StatusRuntimeException) {
            println(e)
            false
        }
    }

}
