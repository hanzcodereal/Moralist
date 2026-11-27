package com.moralist.listentogether.codec

import com.google.protobuf.CodedInputStream
import com.google.protobuf.CodedOutputStream
import com.google.protobuf.WireFormat
import com.moralist.shared.listentogether.network.ChatHistoryPayload
import com.moralist.shared.listentogether.network.ChatMessagePayload
import com.moralist.shared.listentogether.network.MessageTypes
import com.moralist.shared.listentogether.network.SendChatPayload

/**
 * Format kabel pesan obrolan Dengar Bersama. Ditulis langsung dengan CodedInput/OutputStream
 * karena definisi proto bawaan (submodule metroproto) tidak memuat pesan obrolan.
 *
 * send_chat    : text(1)
 * chat_message : message_id(1) user_id(2) username(3) text(4) timestamp(5, int64)
 * chat_history : messages(1, berulang, chat_message)
 */
internal object ChatWireFormat {
    fun encode(payload: Any): ByteArray? =
        when (payload) {
            is SendChatPayload -> {
                val bytes = ByteArray(CodedOutputStream.computeStringSize(1, payload.text))
                val out = CodedOutputStream.newInstance(bytes)
                out.writeString(1, payload.text)
                out.checkNoSpaceLeft()
                bytes
            }

            else -> null
        }

    fun decode(
        msgType: String,
        bytes: ByteArray,
    ): Any =
        when (msgType) {
            MessageTypes.CHAT_MESSAGE -> readMessage(bytes)
            MessageTypes.CHAT_HISTORY -> readHistory(bytes)
            else -> throw IllegalArgumentException("Unsupported chat message type: $msgType")
        }

    private fun readHistory(bytes: ByteArray): ChatHistoryPayload {
        val input = CodedInputStream.newInstance(bytes)
        val messages = mutableListOf<ChatMessagePayload>()
        while (true) {
            val tag = input.readTag()
            if (tag == 0) break
            if (WireFormat.getTagFieldNumber(tag) == 1) {
                messages += readMessage(input.readBytes().toByteArray())
            } else {
                input.skipField(tag)
            }
        }
        return ChatHistoryPayload(messages)
    }

    private fun readMessage(bytes: ByteArray): ChatMessagePayload {
        val input = CodedInputStream.newInstance(bytes)
        var messageId = ""
        var userId = ""
        var username = ""
        var text = ""
        var timestamp = 0L
        while (true) {
            val tag = input.readTag()
            if (tag == 0) break
            when (WireFormat.getTagFieldNumber(tag)) {
                1 -> messageId = input.readString()
                2 -> userId = input.readString()
                3 -> username = input.readString()
                4 -> text = input.readString()
                5 -> timestamp = input.readInt64()
                else -> input.skipField(tag)
            }
        }
        return ChatMessagePayload(messageId, userId, username, text, timestamp)
    }
}
