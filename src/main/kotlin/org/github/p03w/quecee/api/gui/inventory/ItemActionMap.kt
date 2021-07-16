/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.github.p03w.quecee.api.gui.inventory

import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import org.github.p03w.quecee.util.DualHashMap
import org.github.p03w.quecee.util.GuiAction

/**
 * A simple wrapper that indexes `GuiAction`s and `ItemStack`s into slot numbers + some utility functions
 */
@Suppress("unused")
class ItemActionMap<T>(method: ItemActionMap<T>.() -> Unit) {
    private val backingMap: DualHashMap<Int, GuiAction<T>, ItemStack> = DualHashMap()

    init {
        this.method()
    }

    fun runActionAt(slotId: Int, data: Int, state: T) {
        if (backingMap.contains(slotId)) {
            backingMap[slotId].first.invoke(data, state)
        }
    }

    fun addEntry(slot: Int, stack: ItemStack, action: GuiAction<T>) {
        backingMap.set(slot, action, stack)
    }

    fun copyIntoInventory(inv: Inventory) {
        backingMap.forEach { slot, _, item -> inv.setStack(slot, item) }
    }
}
