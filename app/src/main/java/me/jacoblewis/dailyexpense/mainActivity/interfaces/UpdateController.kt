package me.jacoblewis.dailyexpense.mainActivity.interfaces

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import me.jacoblewis.dailyexpense.mainActivity.interfaces.nav.NavUIUpdate

interface UpdateController : ActivityController {
    val listeners: MutableMap<LifecycleOwner, (NavUIUpdate) -> Unit>

    fun updateUI(navUIUpdate: NavUIUpdate) {
        // Automatically remove listeners when they are destroyed
        val expired = listeners.filter { it.component1().lifecycle.currentState == Lifecycle.State.DESTROYED }
        expired.forEach { (owner, _) -> listeners.remove(owner) }
        listeners.forEach { (_, update) -> update(navUIUpdate) }
    }

    fun listenForUIUpdate(owner: LifecycleOwner, updateReceived: (NavUIUpdate) -> Unit) {
        listeners[owner] = updateReceived
    }
}