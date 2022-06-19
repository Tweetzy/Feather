package ca.tweetzy.feather.gui.methods;


import ca.tweetzy.feather.gui.events.GuiPageEvent;

public interface Pagable {

    /**
     * > This function is called when the page changes
     *
     * @param event The event that was fired.
     */
    void onPageChange(GuiPageEvent event);
}
