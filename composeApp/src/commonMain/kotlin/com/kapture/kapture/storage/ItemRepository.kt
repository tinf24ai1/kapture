package com.kapture.kapture.storage

// Repository interface for loading and saving ItemModel data
interface ItemRepository {
    fun load(): List<ItemModel>
    fun save(itemModels: List<ItemModel>)
    fun clear()
}
