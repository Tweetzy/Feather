package ca.tweetzy.rose.gui.methods;


import ca.tweetzy.rose.gui.events.GuiCloseEvent;

public interface Closable {

	void onClose(GuiCloseEvent event);
}
