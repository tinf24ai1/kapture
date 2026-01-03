package com.kapture.kapture.storage

// Item List implementation for sorting Items based on releaseDate
// Used for reciving the next upcoming Item
// Persisted in LocalStorage as a List<Item>
class ItemList {
    private val itemModels = mutableListOf<ItemModel>()
    fun peek(): ItemModel? = itemModels.firstOrNull()

    fun add(itemModel: ItemModel) {
        itemModels.add(itemModel)
        itemModels.sortBy { item: ItemModel -> item.releaseDate }
        LocalItemRepository.save(this.itemModels)
    }

    fun poll(): ItemModel? {

        if (itemModels.isEmpty())
            return null

        val polledItem = itemModels.removeAt(0)
        LocalItemRepository.save(this.itemModels)

        return polledItem
    }

}