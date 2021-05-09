package org.github.p03w.quecee.api.gui

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.Items
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.text.Text
import org.github.p03w.quecee.api.gui.inventory.ItemActionMap
import org.github.p03w.quecee.api.gui.inventory.SimpleDefaultedInventory
import org.github.p03w.quecee.api.util.guiStack

abstract class QueCeeHandlerFactory<T>(
    val screenDisplayName: Text,
    val rowCount: Int,
    val state: T,
    val onCloseMethod: (T) -> Unit
) : NamedScreenHandlerFactory {
    private lateinit var defaulted: SimpleDefaultedInventory
    private lateinit var lastMade: QueCeeScreenHandler<T, *>

    override fun createMenu(
        syncId: Int,
        inv: PlayerInventory,
        player: PlayerEntity
    ): QueCeeScreenHandler<T, GenericContainerScreenHandler> {
        defaulted = SimpleDefaultedInventory(rowCount * 9, Items.LIGHT_GRAY_STAINED_GLASS_PANE.guiStack(""))
        val actionMap = generateActionMap(state)
        actionMap.copyIntoInventory(defaulted)

        lastMade =
            QueCeeScreenHandler(syncId, inv, defaulted, rowCount, rowsToType(rowCount), actionMap, state, onCloseMethod)
        return lastMade as QueCeeScreenHandler<T, GenericContainerScreenHandler>
    }

    fun rebuild() {
        val actionMap = generateActionMap(state)
        defaulted.reset()
        actionMap.copyIntoInventory(defaulted)
        lastMade.actions = actionMap
    }

    override fun getDisplayName(): Text {
        return screenDisplayName
    }

    abstract fun generateActionMap(state: T): ItemActionMap<T>

    private fun rowsToType(rowCount: Int): ScreenHandlerType<GenericContainerScreenHandler> {
        return when (rowCount) {
            1 -> ScreenHandlerType.GENERIC_9X1
            2 -> ScreenHandlerType.GENERIC_9X2
            3 -> ScreenHandlerType.GENERIC_9X3
            4 -> ScreenHandlerType.GENERIC_9X4
            5 -> ScreenHandlerType.GENERIC_9X5
            6 -> ScreenHandlerType.GENERIC_9X6
            else -> throw IllegalArgumentException("Cannot create type with rows $rowCount, must be in range 1..6")
        }
    }
}
