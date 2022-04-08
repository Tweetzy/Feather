package ca.tweetzy.rose.gui.methods;


import ca.tweetzy.rose.gui.events.GuiDropItemEvent;

public interface Droppable {

    boolean onDrop(GuiDropItemEvent event);
}
