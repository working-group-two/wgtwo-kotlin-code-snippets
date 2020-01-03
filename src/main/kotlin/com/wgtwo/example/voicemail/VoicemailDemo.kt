package com.wgtwo.example.voicemail

import com.wgtwo.example.Shared.channel
import com.wgtwo.example.Shared.credentials
import io.grpc.StatusRuntimeException
import io.omnicate.messaging.protobuf.Voicemail
import io.omnicate.messaging.protobuf.VoicemailMediaServiceGrpc
import javax.sound.sampled.AudioSystem

object VoicemailDemo {
    private val blockingStub = VoicemailMediaServiceGrpc.newBlockingStub(channel).withCallCredentials(credentials)

    fun listVoicemails(msisdn: String): MutableList<Voicemail.VoicemailMetadata>? {
        val voicemailMetadataRequest = Voicemail.GetAllVoicemailMetadataRequest.newBuilder().setMsisdn(msisdn).build()
        val metadataResponse = try {
            blockingStub.getAllVoicemailMetadata(voicemailMetadataRequest)
        } catch (e: StatusRuntimeException) {
            println(e)
            return null
        }

        val voicemailList = metadataResponse.voicemailsMetadataList
        return voicemailList
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
        println("Voicemail ended")
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
