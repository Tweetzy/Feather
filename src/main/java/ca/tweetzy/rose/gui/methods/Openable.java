package ca.tweetzy.rose.gui.methods;


import ca.tweetzy.rose.gui.events.GuiOpenEvent;

public interface Openable {

    /**
     * This function is called when the GUI is opened.
     *
     * @param event The event that was fired.
     */
    void onOpen(GuiOpenEvent event);
}
