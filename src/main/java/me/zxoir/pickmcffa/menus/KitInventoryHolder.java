package me.zxoir.pickmcffa.menus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.zxoir.pickmcffa.customclasses.Kit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/15/2022
 */
@AllArgsConstructor
@Getter
public class KitInventoryHolder implements InventoryHolder {
    Kit kit;

    @Override
    public Inventory getInventory() {
        return null;
    }
}
