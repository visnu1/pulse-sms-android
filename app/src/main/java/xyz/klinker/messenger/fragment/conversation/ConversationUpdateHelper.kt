package xyz.klinker.messenger.fragment.conversation

import android.support.v4.app.FragmentActivity
import xyz.klinker.messenger.R
import xyz.klinker.messenger.shared.data.model.Message
import xyz.klinker.messenger.shared.data.pojo.ConversationUpdateInfo
import xyz.klinker.messenger.shared.receiver.ConversationListUpdatedReceiver

class ConversationUpdateHelper(private val fragment: ConversationListFragment) {

    private val activity: FragmentActivity? by lazy { fragment.activity }
    private val updatedReceiver: ConversationListUpdatedReceiver = ConversationListUpdatedReceiver(fragment)

    var newConversationTitle: String? = null
    var updateInfo: ConversationUpdateInfo? = null

    fun notifyOfSentMessage(m: Message?) {
        if (m == null) {
            return
        }

        fragment.messageListManager.expandedConversation?.conversation?.timestamp = m.timestamp
        fragment.messageListManager.expandedConversation?.conversation?.read = m.read

        if (m.mimeType != null && m.mimeType == "text/plain") {
            fragment.messageListManager.expandedConversation?.conversation?.snippet = m.data
            fragment.messageListManager.expandedConversation?.summary?.text = m.data
        }

        if (fragment.messageListManager.expandedConversation != null && fragment.messageListManager.expandedConversation!!.conversation != null) {
            updateInfo = ConversationUpdateInfo(
                    fragment.messageListManager.expandedConversation!!.conversation!!.id,
                    fragment.getString(R.string.you) + ": " + m.data, true)
        }
    }

    fun createReceiver() { activity?.registerReceiver(updatedReceiver, ConversationListUpdatedReceiver.intentFilter) }
    fun destroyReceiver() { activity?.unregisterReceiver(updatedReceiver) }

    fun broadcastUpdateInfo() {
        if (updateInfo != null) {
            ConversationListUpdatedReceiver.sendBroadcast(activity, updateInfo!!)
            updateInfo = null
        }
    }

    fun broadcastTitleChange(contractedId: Long) {
        if (newConversationTitle != null) {
            ConversationListUpdatedReceiver.sendBroadcast(activity, contractedId, newConversationTitle!!)
            newConversationTitle = null
        }
    }
}