/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.github.p03w.quecee.api.util

import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntArrayTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting

/**
 * Converts an ItemConvertible into an ItemStack with a custom name and a tag that makes it get cleaned up from creative
 */
fun ItemConvertible.guiStack(name: String = "", nameColor: Formatting = Formatting.WHITE): ItemStack {
    return this.guiStack(LiteralText(name), nameColor)
}

/**
 * Converts an ItemConvertible into an ItemStack with a custom name and a tag that makes it get cleaned up from creative
 */
fun ItemConvertible.guiStack(name: MutableText = LiteralText(""), nameColor: Formatting = Formatting.WHITE): ItemStack {
    return ItemStack(this)
        .setCustomName(
            name.setStyle(
                    Style.EMPTY
                        .withItalic(false)
                        .withFormatting(nameColor)
                )
        )
        .apply { orCreateTag.putBoolean("QUECEE-DELETE", true) }
}

/**
 * Adds each entry in loreLines as plain text to the tooltip
 *
 * Automatically adds an extra line break at the beginning
 */
fun ItemStack.withLore(loreLines: List<String>): ItemStack {
    val display = (this.orCreateTag.get("display") as CompoundTag?) ?: CompoundTag()
    display.put("Lore", ListTag().apply {
        add(StringTag.of("{\"text\":\"\"}"))
        for (line in loreLines) {
            add(StringTag.of("{\"text\":\"$line\",\"color\":\"white\",\"italic\":false}"))
        }
    })
    this.orCreateTag.put("display", display)
    return this
}

/**
 * Puts an empty Enchantments tag on the itemstack (replacing all enchantments)
 */
fun ItemStack.withGlint(doGlint: Boolean = true): ItemStack {
    if (doGlint) {
        this.orCreateTag.put("Enchantments", ListTag().apply {
            add(CompoundTag())
        })
    }
    return this
}

/**
 * Applies the skull data to the ItemStack
 */
fun ItemStack.applySkull(data: String, uuid: List<Int>): ItemStack {
    orCreateTag.put("SkullOwner", CompoundTag().apply {
        put("Id", IntArrayTag(uuid))
        put("Properties", CompoundTag().apply {
            put("textures", ListTag().apply {
                add(CompoundTag().apply {
                    putString("Value", data)
                })
            })
        })
    })
    return this
}
