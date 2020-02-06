package com.wgtwo.example.voicemail

import com.wgtwo.api.common.v0.PhoneNumberProto
import com.wgtwo.api.messaging.v0.VoicemailMediaServiceGrpc
import com.wgtwo.api.messaging.v0.VoicemailProto
import com.wgtwo.api.messaging.v0.VoicemailProto.GetVoicemailResponse.BytesCase
import com.wgtwo.example.Shared.channel
import com.wgtwo.example.Shared.credentials
import io.grpc.StatusRuntimeException
import javax.sound.sampled.AudioSystem

object VoicemailDemo {
    private val blockingStub = VoicemailMediaServiceGrpc.newBlockingStub(channel).withCallCredentials(credentials)

    fun listVoicemails(msisdn: String): MutableList<VoicemailProto.VoicemailMetadata>? {
        val phoneNumberProto = PhoneNumberProto.PhoneNumber.newBuilder().setE164(msisdn)
        val voicemailMetadataRequest =
            VoicemailProto.GetAllVoicemailMetadataRequest.newBuilder().setTo(phoneNumberProto).build()
        val metadataResponse = try {
            blockingStub.getAllVoicemailMetadata(voicemailMetadataRequest)
        } catch (e: StatusRuntimeException) {
            println(e)
            return null
        }

        return metadataResponse.metadataList
    }

    fun playVoicemail(voicemailId: String) {
        val voicemailRequest = VoicemailProto.GetVoicemailRequest.newBuilder().setVoicemailId(voicemailId).build()

        val voicemail = try {
            blockingStub.getVoicemail(voicemailRequest)
        } catch (e: StatusRuntimeException) {
            println(e)
            return
        }

        val tempFile = createTempFile(prefix = "voicemail", suffix = ".wav")
        println(tempFile.absoluteFile)
        val outputStream = tempFile.outputStream()

        if (voicemail.bytesCase != BytesCase.WAV) {
            println("Unexpected format for voicemail.")
            return
        }
        voicemail.wav.writeTo(outputStream)
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
        val readRequest = VoicemailProto.MarkVoicemailAsReadRequest.newBuilder().setVoicemailId(voicemailId).build()

        return try {
            blockingStub.markVoicemailAsRead(readRequest)
            true
        } catch (e: StatusRuntimeException) {
            println(e)
            false
        }
    }

    fun deleteVoicemail(voicemailId: String): Boolean {
        val deleteRequest = VoicemailProto.DeleteVoicemailRequest.newBuilder().setVoicemailId(voicemailId).build()

        return try {
            blockingStub.deleteVoicemail(deleteRequest)
            true
        } catch (e: StatusRuntimeException) {
            println(e)
            false
        }
    }

}
