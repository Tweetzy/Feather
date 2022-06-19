package ca.tweetzy.feather.gui.template;

import ca.tweetzy.feather.comp.enums.CompMaterial;
import ca.tweetzy.feather.gui.Gui;
import ca.tweetzy.feather.gui.events.GuiClickEvent;
import ca.tweetzy.feather.gui.helper.InventoryBorder;
import ca.tweetzy.feather.gui.helper.InventorySafeMaterials;
import ca.tweetzy.feather.utils.Common;
import ca.tweetzy.feather.utils.Inflector;
import ca.tweetzy.feather.utils.QuickItem;
import ca.tweetzy.feather.utils.input.TitleInput;
import lombok.NonNull;
import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Date Created: April 20 2022
 * Time Created: 2:02 p.m.
 *
 * @author Kiran Hart
 */
public final class MaterialPickerGUI extends BaseGUI {

    private final Gui parent;
    private final String inputTitle, inputSubtitle;
    private final String searchQuery;
    private final BiConsumer<GuiClickEvent, CompMaterial> selected;


    public MaterialPickerGUI(final Gui parent, final String titleOverride, final String searchQuery, final String inputTitle, final String inputSubtitle, @NonNull final BiConsumer<GuiClickEvent, CompMaterial> selected) {
        super(parent, titleOverride == null ? "&eMaterial Selector" : titleOverride);
        this.searchQuery = searchQuery;
        this.inputTitle = inputTitle;
        this.inputSubtitle = inputSubtitle;
        this.selected = selected;
        this.parent = parent;
        draw();
    }

    public MaterialPickerGUI(final String titleOverride, final String inputTitle, final String inputSubtitle, final String searchQuery, @NonNull final BiConsumer<GuiClickEvent, CompMaterial> selected) {
        this(null, titleOverride, searchQuery, inputTitle, inputSubtitle, selected);
    }

    public MaterialPickerGUI(final String titleOverride, final String searchQuery, @NonNull final BiConsumer<GuiClickEvent, CompMaterial> selected) {
        this(null, titleOverride, searchQuery, "&eMaterial Search", "&fType the search term into chat", selected);
    }

    public MaterialPickerGUI(Gui parent, String title, String searchQuery, BiConsumer<GuiClickEvent, CompMaterial> selected) {
        this(parent, title, searchQuery, "&eMaterial Search", "&fType the search term into chat", selected);
    }

    @Override
    protected void draw() {
        reset();

        List<CompMaterial> validMaterials = InventorySafeMaterials.get();
        if (this.searchQuery != null) {
            validMaterials = validMaterials.stream().filter(mat -> Common.match(this.searchQuery, mat.name()) || Common.match(this.searchQuery, Inflector.getInstance().pluralize(mat.name()))).collect(Collectors.toList());
        }

        final List<CompMaterial> itemsToFill = validMaterials.stream().skip((page - 1) * (long) this.fillSlots().size()).limit(this.fillSlots().size()).collect(Collectors.toList());
        pages = (int) Math.max(1, Math.ceil(validMaterials.size() / (double) this.fillSlots().size()));

        setPrevPage(5, 3, this.getPreviousPageButton());
        setNextPage(5, 5, this.getNextPageButton());
        setOnPage(e -> draw());

        for (int i = 0; i < this.rows * 9; i++) {
            if (this.fillSlots().contains(i) && this.fillSlots().indexOf(i) < itemsToFill.size()) {
                final CompMaterial material = itemsToFill.get(this.fillSlots().indexOf(i));
                setButton(i, buildIcon(material), click -> this.selected.accept(click, material));
            }
        }

        setButton(5, 4, buildSearchButton(), click -> {
            click.gui.exit();

            new TitleInput(click.player, Common.colorize(this.inputTitle), Common.colorize(this.inputSubtitle)) {
                @Override
                public boolean onResult(String string) {
                    if (string.isEmpty()) return false;
                    click.manager.showGUI(click.player, new MaterialPickerGUI(MaterialPickerGUI.this.parent, MaterialPickerGUI.this.title, string, MaterialPickerGUI.this.inputTitle, MaterialPickerGUI.this.inputSubtitle, MaterialPickerGUI.this.selected));
                    return true;
                }

                @Override
                public void onExit(Player player) {
                    click.manager.showGUI(click.player, MaterialPickerGUI.this);
                }
            };
        });

        if (this.searchQuery != null)
            setButton(5, 7, buildResetButton(), click -> click.manager.showGUI(click.player, new MaterialPickerGUI(MaterialPickerGUI.this.parent, MaterialPickerGUI.this.title, null, MaterialPickerGUI.this.inputTitle, MaterialPickerGUI.this.inputSubtitle, this.selected)));

        applyBackExit(this.parent);
    }

    protected ItemStack buildIcon(@NonNull final CompMaterial compMaterial) {
        return QuickItem.of(compMaterial)
                .name("&e&l" + WordUtils.capitalizeFully(compMaterial.name().replace("_", " ")))
                .lore("&7Click to select this material")
                .make();
    }

    protected ItemStack buildSearchButton() {
        return QuickItem.of(CompMaterial.DARK_OAK_SIGN).name("&b&lSearch").lore("&7Click to search materials").make();
    }

    protected ItemStack buildResetButton() {
        return QuickItem
                .of(CompMaterial.LAVA_BUCKET)
                .name("&c&lClear Search")
                .lore("&7Click to clear your search")
                .make();
    }

    @Override
    protected List<Integer> fillSlots() {
        return InventoryBorder.getInsideBorders(5);
    }
}
