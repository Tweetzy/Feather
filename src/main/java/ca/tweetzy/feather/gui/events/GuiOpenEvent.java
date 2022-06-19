package ca.tweetzy.feather.gui.events;

import ca.tweetzy.feather.gui.Gui;
import ca.tweetzy.feather.gui.GuiManager;
import org.bukkit.entity.Player;

public class GuiOpenEvent extends GuiEvent {
    public GuiOpenEvent(GuiManager manager, Gui gui, Player player) {
        super(manager, gui, player);
    }
}
