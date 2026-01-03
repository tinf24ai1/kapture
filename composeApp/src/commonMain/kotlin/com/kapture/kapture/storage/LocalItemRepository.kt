package com.kapture.kapture.storage

object LocalItemRepository : ItemRepository {

    private const val LOCAL_ITEMS_KEY = "ideaList"

    override fun load(): List<ItemModel> {
        if (LocalStorage.isset(LOCAL_ITEMS_KEY))
            return LocalStorage.load<List<ItemModel>>(LOCAL_ITEMS_KEY)
        return emptyList()
    }

    override fun save(itemModels: List<ItemModel>) {
        LocalStorage.save<List<ItemModel>>(LOCAL_ITEMS_KEY, itemModels)
    }

    override fun clear() {
        LocalStorage.clear(LOCAL_ITEMS_KEY)
    }
}
