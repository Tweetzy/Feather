package ca.tweetzy.rose.utils;

import ca.tweetzy.rose.Common;
import ca.tweetzy.rose.comp.NBTEditor;
import ca.tweetzy.rose.comp.enums.CompMaterial;
import ca.tweetzy.rose.comp.enums.ServerVersion;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date Created: April 07 2022
 * Time Created: 1:58 p.m.
 *
 * @author Kiran Hart
 */
public final class QuickItem {

    private ItemStack item;

    private ItemMeta meta;

    private CompMaterial material;

    private int amount = -1;

    @Getter
    private String name;

    private final List<String> lores = new ArrayList<>();

    private final Map<Enchantment, Integer> enchants = new HashMap<>();

    private final List<ItemFlag> flags = new ArrayList<>();

    private boolean unbreakable = false;

    private Color color;

    private boolean hideTags = false;

    private Integer modelData;

    private boolean glow = false;

    private final Map<String, String> tags = new HashMap<>();

    // ----------------------------------------------------------------------------------------
    // Builder methods
    // ----------------------------------------------------------------------------------------

    /**
     * Set the ItemStack for this item. We will reapply all other properties
     * on this ItemStack, make sure they are compatible (such as skullOwner requiring a skull ItemStack, etc.)
     *
     * @param item
     *
     * @return
     */
    public QuickItem item(ItemStack item) {
        this.item = item;

        return this;
    }

    /**
     * Set the ItemMeta we use to start building. All other properties in this
     * class will build on this meta and take priority.
     *
     * @param meta
     *
     * @return
     */
    public QuickItem meta(ItemMeta meta) {
        this.meta = meta;

        return this;
    }

    /**
     * Set the Material for the item. If an itemstack has already been set,
     * this material will take priority.
     *
     * @param material
     *
     * @return
     */
    public QuickItem material(CompMaterial material) {
        this.material = material;

        return this;
    }

    /**
     * Set the amount of ItemStack to create.
     *
     * @param amount
     *
     * @return
     */
    public QuickItem amount(int amount) {
        this.amount = amount;

        return this;
    }

    /**
     * Set a custom name for the item (& color codes are replaced automatically).
     */
    public QuickItem name(String name) {
        this.name = name;

        return this;
    }

    /**
     * Remove any previous lore from the item. Useful if you initiated this
     * class with an ItemStack or set the itemstack already, to clear old lore off of it.
     */
    public QuickItem clearLore() {
        this.lores.clear();
        return this;
    }

    /**
     * Append the given lore to the end of existing item lore.
     */
    public QuickItem lore(String... lore) {
        return this.lore(Arrays.asList(lore));
    }

    /**
     * Append the given lore to the end of existing item lore.
     */
    public QuickItem lore(List<String> lore) {
        this.lores.addAll(lore);
        return this;
    }

    /**
     * Add the given enchant to the item.
     */
    public QuickItem enchant(Enchantment enchantment) {
        return this.enchant(enchantment, 1);
    }

    /**
     * Add the given enchant to the item.
     */
    public QuickItem enchant(Enchantment enchantment, int level) {
        this.enchants.put(enchantment, level);
        return this;
    }

    /**
     * Add the given flags to the item.
     */
    public QuickItem flags(ItemFlag... flags) {
        this.flags.addAll(Arrays.asList(flags));
        return this;
    }

    /**
     * Set the item to be unbreakable.
     */
    public QuickItem unbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    /**
     * Set the stained or dye color in case your item is either of {@link LeatherArmorMeta},
     * or from a selected list of compatible items such as stained glass, wool, etc.
     */
    public QuickItem color(Color color) {
        this.color = color;
        return this;
    }

    /**
     * Removes all enchantment, attribute and other tags appended
     * at the end of item lore, typically with blue color.
     */
    public QuickItem hideTags(boolean hideTags) {
        this.hideTags = hideTags;
        return this;
    }

    /**
     * Set the Custom Model Data of this item, compatible with MC 1.14+
     */
    public QuickItem modelData(int modelData) {
        this.modelData = modelData;
        return this;
    }

    /**
     * Makes this item glow. Ignored if enchantments exists. Call {@link #hideTags(boolean)}
     * to hide enchantment lores instead.
     */
    public QuickItem glow(boolean glow) {
        this.glow = glow;
        return this;
    }

    /**
     * Places an invisible custom tag to the item, for most server instances it
     * will persist across saves/restarts (you should check just to be safe).
     */
    public QuickItem tag(String key, String value) {
        this.tags.put(key, value);
        return this;
    }

    /**
     * Construct a valid {@link ItemStack} from all parameters of this class.
     *
     * @return the finished item
     */
    public ItemStack make() {
        ItemStack compiledItem = this.item != null ? this.item.clone() : this.material.parseItem();
        Object compiledMeta = item.hasItemMeta() ? this.meta != null ? this.meta.clone() : compiledItem.getItemMeta() : null;

        // Override with given material
        if (this.material != null) {
            compiledItem.setType(this.material.parseMaterial());

            if (ServerVersion.isServerVersionBelow(ServerVersion.V1_13))
                compiledItem.setData(new MaterialData(this.material.parseMaterial(), this.material.getData()));
        }

        // Skip if air
        if (CompMaterial.isAir(compiledItem.getType()))
            return compiledItem;

        // Apply specific material color if possible
        color:
        if (this.color != null) {
            if (compiledItem.getType().toString().contains("LEATHER")) {
                ((LeatherArmorMeta) compiledMeta).setColor(this.color);
            } else {

                // Hack: If you put WHITE_WOOL and a color, we automatically will change the material to the colorized version
                if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
                    final String dye = DyeColor.getByColor(this.color).toString();
                    final List<String> colorableMaterials = Arrays.asList("BANNER", "BED", "CARPET", "CONCRETE", "GLAZED_TERRACOTTA", "SHULKER_BOX", "STAINED_GLASS",
                            "STAINED_GLASS_PANE", "TERRACOTTA", "WALL_BANNER", "WOOL");

                    for (final String material : colorableMaterials) {
                        final String suffix = "_" + material;

                        if (compiledItem.getType().toString().endsWith(suffix)) {
                            compiledItem.setType(Material.valueOf(dye + suffix));

                            break color;
                        }
                    }
                } else {
                    try {
                        final byte dataValue = DyeColor.getByColor(this.color).getWoolData();

                        compiledItem.setData(new MaterialData(compiledItem.getType(), dataValue));
                        compiledItem.setDurability(dataValue);

                    } catch (final NoSuchMethodError err) {
                        // Ancient MC, ignore
                    }
                }
            }
        }

        if (compiledMeta != null) {
            if (this.glow && this.enchants.isEmpty()) {
                ((ItemMeta) compiledMeta).addEnchant(Enchantment.DURABILITY, 1, true);

                this.flags.add(ItemFlag.HIDE_ENCHANTS);
            }

            for (final Map.Entry<Enchantment, Integer> entry : this.enchants.entrySet()) {
                final Enchantment enchant = entry.getKey();
                final int level = entry.getValue();

                if (compiledMeta instanceof EnchantmentStorageMeta)
                    ((EnchantmentStorageMeta) compiledMeta).addStoredEnchant(enchant, level, true);

                else
                    ((ItemMeta) compiledMeta).addEnchant(enchant, level, true);
            }

            if (this.name != null && !"".equals(this.name))
                ((ItemMeta) compiledMeta).setDisplayName(Common.colorize("&r&f" + name));

            if (!this.lores.isEmpty()) {
                final List<String> coloredLores = new ArrayList<>();

                for (final String lore : this.lores)
                    if (lore != null)
                        for (final String subLore : lore.split("\n"))
                            coloredLores.add(Common.colorize("&7" + subLore));

                ((ItemMeta) compiledMeta).setLore(coloredLores);
            }
        }

        if (this.unbreakable) {
            this.flags.add(ItemFlag.HIDE_ATTRIBUTES);
            this.flags.add(ItemFlag.HIDE_UNBREAKABLE);
            this.item = NBTEditor.set(this.item, (byte) 1, "Unbreakable");
        }

        if (this.hideTags)
            for (final ItemFlag f : ItemFlag.values())
                if (!this.flags.contains(f))
                    this.flags.add(f);

        for (final ItemFlag flag : this.flags)
            try {
                ((ItemMeta) compiledMeta).addItemFlags(ItemFlag.valueOf(flag.toString()));
            } catch (final Throwable t) {
            }

        // Override with custom amount if set
        if (this.amount != -1)
            compiledItem.setAmount(this.amount);

        // Apply Bukkit metadata
        if (compiledMeta instanceof ItemMeta)
            compiledItem.setItemMeta((ItemMeta) compiledMeta);

        //
        // From now on we have to re-set the item
        //
        // Set custom model data
        if (this.modelData != null && ServerVersion.isServerVersionAtLeast(ServerVersion.V1_14))
            try {
                ((ItemMeta) compiledMeta).setCustomModelData(this.modelData);
            } catch (final Throwable t) {
            }

        for (final Map.Entry<String, String> entry : this.tags.entrySet())
            compiledItem = NBTEditor.set(compiledItem, entry.getValue(), entry.getKey());


        return compiledItem;
    }

    // ----------------------------------------------------------------------------------------
    // Static access
    // ----------------------------------------------------------------------------------------

    /**
     * Convenience method to get a new item creator with material, name and lore set
     *
     * @param material
     * @param name
     * @param lore
     *
     * @return new item creator
     */
    public static QuickItem of(final CompMaterial material, final String name, @NonNull final String... lore) {
        return new QuickItem().material(material).name(name).lore(lore).hideTags(true);
    }

    /**
     * Convenience method to get a wool
     *
     * @param color the wool color
     *
     * @return the new item creator
     */
    public static QuickItem ofWool(final Color color) {
        return of(CompMaterial.makeWool(color, 1)).color(color);
    }

    /**
     * Convenience method to get the creator of an existing itemstack
     *
     * @param item existing itemstack
     *
     * @return the new item creator
     */
    public static QuickItem of(final ItemStack item) {
        final QuickItem builder = new QuickItem();
        final ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.getLore() != null)
            builder.lore(meta.getLore());

        return builder.item(item);
    }

    /**
     * Get a new item creator from material
     *
     * @param mat existing material
     *
     * @return the new item creator
     */
    public static QuickItem of(final CompMaterial mat) {
        return new QuickItem().material(mat);
    }
}