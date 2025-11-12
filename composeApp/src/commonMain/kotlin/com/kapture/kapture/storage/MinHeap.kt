package com.kapture.kapture.storage

class MinHeap {
    val items = mutableListOf<Item>()

    val size: Int get() = items.size
    fun isEmpty() = items.isEmpty()
    fun peek(): Item? = items.firstOrNull()

    fun add(item: Item) {
        items.add(item)
        heapifyUp(items.lastIndex)
        LocalStorage.save("MinHeap",this.items)
    }

    fun poll(): Item? {
        if (items.isEmpty()) return null
        val root = items.first()
        val last = items.removeLast()
        if (items.isNotEmpty()) {
            items[0] = last
            heapifyDown(0)
        }
        LocalStorage.save("MinHeap",this.items)
        return root
    }

    private fun heapifyUp(index: Int) {
        var i = index
        while (i > 0) {
            val parent = (i - 1) / 2
            if (items[i].releaseDate >= items[parent].releaseDate) break
            items.swap(i, parent)
            i = parent
        }
    }

    private fun heapifyDown(index: Int) {
        var i = index
        val lastIndex = items.lastIndex
        while (true) {
            val left = 2 * i + 1
            val right = 2 * i + 2
            var smallest = i

            if (left <= lastIndex && items[left].releaseDate < items[smallest].releaseDate)
                smallest = left
            if (right <= lastIndex && items[right].releaseDate < items[smallest].releaseDate)
                smallest = right
            if (smallest == i) break

            items.swap(i, smallest)
            i = smallest
        }
    }

    private fun MutableList<Item>.swap(a: Int, b: Int) {
        val temp = this[a]
        this[a] = this[b]
        this[b] = temp
    }

    fun clear() {
        items.clear()
    }
}