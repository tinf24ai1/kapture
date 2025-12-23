package com.kapture.kapture.storage

object HeapUpdate {
    fun markNotified(itemId: String) {
        val items: List<Item> = LocalStorage.restore("MinHeap") ?: emptyList()
        val updated = items.map { if (it.id == itemId) it.copy(notified = true) else it }
        LocalStorage.save("MinHeap", updated)
    }
}
