package com.kapture.kapture.storage

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList

// Local implementation of StorageRepository using LocalStorage for Archive List
object LocalArchiveRepository : StorageRepository {

    private const val LOCAL_ARCHIVE_KEY = "archiveList"

    override fun load(): SnapshotStateList<ItemModel> {
        if (LocalStorage.isset(LOCAL_ARCHIVE_KEY))
            return LocalStorage.load<List<ItemModel>>(LOCAL_ARCHIVE_KEY).toMutableStateList()
        return mutableStateListOf()
    }

    override fun save(itemModels: List<ItemModel>) {
        LocalStorage.save<List<ItemModel>>(LOCAL_ARCHIVE_KEY, itemModels.toList())
    }

    override fun clear() {
        LocalStorage.clear(LOCAL_ARCHIVE_KEY)
    }
}
