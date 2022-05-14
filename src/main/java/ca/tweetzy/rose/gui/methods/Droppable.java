package ca.tweetzy.rose.gui.methods;


import ca.tweetzy.rose.gui.events.GuiDropItemEvent;

public interface Droppable {

    /**
     * This function is called when a player drops an item
     *
     * @param event The event that was fired.
     * @return A boolean value.
     */
    boolean onDrop(GuiDropItemEvent event);
}
