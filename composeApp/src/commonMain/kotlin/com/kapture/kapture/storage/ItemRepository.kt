package com.kapture.kapture.storage

interface ItemRepository {
    fun load(): List<ItemModel>
    fun save(itemModels: List<ItemModel>)
    fun clear()
}
