/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.github.p03w.quecee.api.gui

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerListener
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.collection.DefaultedList
import org.github.p03w.quecee.api.gui.inventory.ItemActionMap
import org.github.p03w.quecee.api.gui.inventory.SimpleDefaultedInventory

/**
 * A custom screen handler for QueCee screens
 *
 * Suppresses any and all actions unless its clicking or right clicking, in which case it instead passes the info along
 *
 * Unfortunately shift clicking de-syncs client side, but that will be fixed when they click in the wrong place or close
 *
 * Unless they are in creative, in which case we need to track the tags and delete those
 */
class QueCeeScreenHandler<T, in R : GenericContainerScreenHandler>(
    syncId: Int,
    private val playerInventory: PlayerInventory,
    inv: SimpleDefaultedInventory,
    rowCount: Int,
    handlerType: ScreenHandlerType<R>,
    var actions: ItemActionMap<T>,
    private val state: T,
    private val onClose: (T) -> Unit
) :
    GenericContainerScreenHandler(
        handlerType,
        syncId,
        playerInventory,
        inv,
        rowCount
    ) {

    init {
        inv.containingHandler = this

        addListener(object : ScreenHandlerListener {
            override fun onHandlerRegistered(handlerx: ScreenHandler, stacks: DefaultedList<ItemStack>) {
                sendContentUpdates()
            }

            override fun onSlotUpdate(handlerx: ScreenHandler, slotId: Int, stack: ItemStack) {
                sendContentUpdates()
            }

            override fun onPropertyUpdate(handlerx: ScreenHandler, property: Int, value: Int) {
                sendContentUpdates()
            }
        })
    }

    override fun canInsertIntoSlot(slot: Slot?): Boolean {
        forceSync()
        return false
    }

    override fun insertItem(stack: ItemStack?, startIndex: Int, endIndex: Int, fromLast: Boolean): Boolean {
        forceSync()
        return false
    }

    override fun canInsertIntoSlot(stack: ItemStack?, slot: Slot?): Boolean {
        forceSync()
        return false
    }

    override fun setStackInSlot(slot: Int, stack: ItemStack) {
        forceSync()
    }

    override fun onContentChanged(inventory: Inventory) {
        forceSync()
        super.onContentChanged(inventory)
    }

    override fun transferSlot(player: PlayerEntity, index: Int): ItemStack {
        forceSync()
        return ItemStack.EMPTY
    }

    override fun updateSlotStacks(stacks: MutableList<ItemStack>) {
        forceSync()
    }

    override fun isNotRestricted(player: PlayerEntity?): Boolean {
        forceSync()
        return true
    }

    override fun onSlotClick(slot: Int, data: Int, actionType: SlotActionType, playerEntity: PlayerEntity): ItemStack {
        if (actionType == SlotActionType.PICKUP) {
            actions.runActionAt(slot, data, state)
        }

        forceSync()
        return ItemStack.EMPTY
    }

    override fun close(player: PlayerEntity) {
        forceSync()
        onClose(state)
    }

    private fun forceSync() {
        sendContentUpdates()
        playerInventory.cursorStack = ItemStack.EMPTY
        playerInventory.updateItems()
        playerInventory.player.playerScreenHandler.onContentChanged(playerInventory)
        (playerInventory.player as ServerPlayerEntity).updateCursorStack()
        playerInventory.remove(
            { stack -> stack.orCreateTag.contains("DELETE") },
            Int.MAX_VALUE,
            playerInventory.player.playerScreenHandler.method_29281()
        )
    }
}
