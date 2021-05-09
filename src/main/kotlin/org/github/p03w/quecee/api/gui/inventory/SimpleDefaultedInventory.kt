/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.github.p03w.quecee.api.gui.inventory

import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import org.github.p03w.quecee.api.gui.QueCeeScreenHandler

/**
 * A simple inventory that fills all slots with the provided ItemStack
 */
class SimpleDefaultedInventory(val size: Int, val default: ItemStack) : SimpleInventory(size) {
    var containingHandler: QueCeeScreenHandler<*, *>? = null

    init {
        for (slot in 0 until size) {
            setStack(slot, default)
        }
    }

    fun reset() {
        for (slot in 0 until size) {
            setStack(slot, default)
        }
    }
}
