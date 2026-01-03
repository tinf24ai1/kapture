package com.kapture.kapture.storage

// Object for sending Notification on Storage-Update
object StorageUpdate {
    fun markNotified(itemId: String) {
        val itemModels: List<ItemModel> = LocalItemRepository.load()
        val updated = itemModels.map { if (it.id == itemId) it.copy(notified = true) else it }
        LocalItemRepository.save(updated)
    }
}
