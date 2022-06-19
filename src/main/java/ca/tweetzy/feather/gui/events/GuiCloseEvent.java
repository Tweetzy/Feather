package ca.tweetzy.feather.gui.events;

import ca.tweetzy.feather.gui.Gui;
import ca.tweetzy.feather.gui.GuiManager;
import org.bukkit.entity.Player;

public class GuiCloseEvent extends GuiEvent {
    public GuiCloseEvent(GuiManager manager, Gui gui, Player player) {
        super(manager, gui, player);
    }
}
