package ca.tweetzy.rose.gui.helper;

import ca.tweetzy.rose.comp.enums.CompMaterial;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: December 23 2021
 * Time Created: 12:02 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
@UtilityClass
public final class InventorySafeMaterials {

    /**
     * It creates a temporary inventory, sets the item in the first slot to the material, and if the item is not null, it adds it to the list
     *
     * @return A list of all valid materials in the game.
     */
    public List<CompMaterial> get() {
        final List<CompMaterial> list = new ArrayList<>();

        final Inventory drawer = Bukkit.createInventory(null, 9, "Valid Materials");

        for (int i = 0; i < CompMaterial.values().length; i++) {
            final CompMaterial material = CompMaterial.values()[i];
            drawer.setItem(0, material.parseItem());
            if (drawer.getItem(0) != null) {
                drawer.setItem(0, null);
                list.add(material);
            }
        }

        return list.stream().sorted(Comparator.comparing(CompMaterial::name)).collect(Collectors.toList());
    }
}
