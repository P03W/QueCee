package org.github.p03w.quecee

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.network.ServerPlayerEntity

object QueCeeMain : ModInitializer {
    override fun onInitialize() {
        ServerTickEvents.START_WORLD_TICK.register {
            it.players.forEach(::stripPlayerOfGuiItems)
        }

        ServerTickEvents.END_WORLD_TICK.register {
            it.players.forEach(::stripPlayerOfGuiItems)
        }
    }

    private fun stripPlayerOfGuiItems(player: ServerPlayerEntity) {
        player.inventory.remove(
            { stack -> stack.tag?.contains("QUECEE-DELETE") ?: false },
            Int.MAX_VALUE,
            player.playerScreenHandler.method_29281()
        )
    }
}
