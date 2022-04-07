package ca.tweetzy.rose.gui.events;

import ca.tweetzy.rose.gui.Gui;
import ca.tweetzy.rose.gui.GuiManager;
import org.bukkit.entity.Player;

public class GuiOpenEvent extends GuiEvent {
	public GuiOpenEvent(GuiManager manager, Gui gui, Player player) {
		super(manager, gui, player);
	}
}
