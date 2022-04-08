package ca.tweetzy.rose.gui.events;

import ca.tweetzy.rose.gui.Gui;
import ca.tweetzy.rose.gui.GuiManager;
import org.bukkit.entity.Player;

public class GuiCloseEvent extends GuiEvent {
    public GuiCloseEvent(GuiManager manager, Gui gui, Player player) {
        super(manager, gui, player);
    }
}
