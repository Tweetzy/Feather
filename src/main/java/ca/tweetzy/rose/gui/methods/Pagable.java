package ca.tweetzy.rose.gui.methods;


import ca.tweetzy.rose.gui.events.GuiPageEvent;

public interface Pagable {

    /**
     * > This function is called when the page changes
     *
     * @param event The event that was fired.
     */
    void onPageChange(GuiPageEvent event);
}
