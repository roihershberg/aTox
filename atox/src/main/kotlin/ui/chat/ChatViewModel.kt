// SPDX-FileCopyrightText: 2019-2021 aTox contributors
//
// SPDX-License-Identifier: GPL-3.0-only

package ltd.evilcorp.atox.ui.chat

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ltd.evilcorp.atox.R
import ltd.evilcorp.atox.ui.NotificationHelper
import ltd.evilcorp.core.vo.ConnectionStatus
import ltd.evilcorp.core.vo.Contact
import ltd.evilcorp.core.vo.FileTransfer
import ltd.evilcorp.core.vo.Message
import ltd.evilcorp.core.vo.MessageType
import ltd.evilcorp.domain.feature.CallManager
import ltd.evilcorp.domain.feature.CallState
import ltd.evilcorp.domain.feature.ChatManager
import ltd.evilcorp.domain.feature.ContactManager
import ltd.evilcorp.domain.feature.FileTransferManager
import ltd.evilcorp.domain.tox.PublicKey

private const val TAG = "ChatViewModel"

enum class CallAvailability {
    Unavailable,
    Available,
    Active,
}

class ChatViewModel @Inject constructor(
    private val callManager: CallManager,
    private val chatManager: ChatManager,
    private val contactManager: ContactManager,
    private val fileTransferManager: FileTransferManager,
    private val notificationHelper: NotificationHelper,
    private val resolver: ContentResolver,
    private val context: Context,
    private val scope: CoroutineScope,
) : ViewModel() {
    private var publicKey = PublicKey("")
    private var sentTyping = false

    val contact: LiveData<Contact> by lazy { contactManager.get(publicKey).asLiveData() }
    val messages: LiveData<List<Message>> by lazy { chatManager.messagesFor(publicKey).asLiveData() }
    val fileTransfers: LiveData<List<FileTransfer>> by lazy { fileTransferManager.transfersFor(publicKey).asLiveData() }

    val callState get() = contactManager.get(publicKey)
        .transform { emit(it.connectionStatus != ConnectionStatus.None) }
        .combine(callManager.inCall) { contactOnline, callState ->
            if (!contactOnline) return@combine CallAvailability.Unavailable
            when (callState) {
                CallState.NotInCall -> CallAvailability.Available
                is CallState.InCall -> {
                    if (callState.publicKey == publicKey) {
                        CallAvailability.Active
                    } else {
                        CallAvailability.Unavailable
                    }
                }
            }
        }.asLiveData()

    var contactOnline = false

    fun send(message: String, type: MessageType) = chatManager.sendMessage(publicKey, message, type)

    fun clearHistory() = scope.launch {
        chatManager.clearHistory(publicKey)
        fileTransferManager.deleteAll(publicKey)
    }

    fun setActiveChat(pk: PublicKey) {
        if (pk.string().isEmpty()) {
            Log.i(TAG, "Clearing active chat")
            setTyping(false)
        } else {
            Log.i(TAG, "Setting active chat ${pk.fingerprint()}")
        }

        publicKey = pk
        notificationHelper.dismissNotifications(publicKey)
        chatManager.activeChat = publicKey.string()
    }

    fun setTyping(typing: Boolean) {
        if (publicKey.string().isEmpty()) return
        if (sentTyping != typing) {
            chatManager.setTyping(publicKey, typing)
            sentTyping = typing
        }
    }

    fun acceptFt(id: Int) = scope.launch {
        fileTransferManager.accept(id)
    }

    fun rejectFt(id: Int) = scope.launch {
        fileTransferManager.reject(id)
    }

    fun createFt(file: Uri) = scope.launch {
        fileTransferManager.create(publicKey, file)
    }

    fun delete(msg: Message) = scope.launch {
        if (msg.type == MessageType.FileTransfer) {
            fileTransferManager.delete(msg.correlationId)
        }
        chatManager.deleteMessage(msg.id)
    }

    fun exportFt(id: Int, dest: Uri) = scope.launch {
        fileTransferManager.get(id).take(1).collect { ft ->
            launch(Dispatchers.IO) {
                try {
                    FileInputStream(File(Uri.parse(ft.destination).path!!)).use { ins ->
                        resolver.openOutputStream(dest).use { os ->
                            ins.copyTo(os!!)
                        }
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, R.string.export_file_success, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, R.string.export_file_failure, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun setDraft(draft: String) = contactManager.setDraft(publicKey, draft)
    fun clearDraft() = setDraft("")
}
