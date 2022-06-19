package ca.tweetzy.feather.gui.methods;


import ca.tweetzy.feather.gui.events.GuiClickEvent;

public interface Clickable {

    /**
     * When the user clicks on the GUI, do this.
     *
     * @param event The event that was fired.
     */
    void onClick(GuiClickEvent event);
}
