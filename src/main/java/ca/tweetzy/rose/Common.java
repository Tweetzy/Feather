package ca.tweetzy.rose;

import ca.tweetzy.rose.utils.colors.ColorFormatter;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Date Created: April 07 2022
 * Time Created: 2:30 p.m.
 *
 * @author Kiran Hart
 */
public final class Common {

    public static String colorize(String string) {
        return ColorFormatter.process(string);
    }

    public static List<String> colorize(List<String> strings) {
        return strings.stream().map(Common::colorize).collect(Collectors.toList());
    }

    // ------------------------------------------------------------------------------------------------------------
    // Scheduling from https://github.com/kangarko/Foundation
    // ------------------------------------------------------------------------------------------------------------

    /**
     * Runs the task if the plugin is enabled correctly
     *
     * @param task the task
     *
     * @return the task or null
     */
    public static <T extends Runnable> BukkitTask runLater(final T task) {
        return runLater(1, task);
    }

    /**
     * Runs the task even if the plugin is disabled for some reason.
     *
     * @param delayTicks
     * @param task
     *
     * @return the task or null
     */
    public static BukkitTask runLater(final int delayTicks, Runnable task) {
        final BukkitScheduler scheduler = Bukkit.getScheduler();
        final JavaPlugin instance = RosePlugin.getInstance();

        try {
            return runIfDisabled(task) ? null : delayTicks == 0 ? task instanceof BukkitRunnable ? ((BukkitRunnable) task).runTask(instance) : scheduler.runTask(instance, task) : task instanceof BukkitRunnable ? ((BukkitRunnable) task).runTaskLater(instance, delayTicks) : scheduler.runTaskLater(instance, task, delayTicks);
        } catch (final NoSuchMethodError err) {

            return runIfDisabled(task) ? null
                    : delayTicks == 0
                    ? task instanceof BukkitRunnable ? ((BukkitRunnable) task).runTask(instance) : getTaskFromId(scheduler.scheduleSyncDelayedTask(instance, task))
                    : task instanceof BukkitRunnable ? ((BukkitRunnable) task).runTaskLater(instance, delayTicks) : getTaskFromId(scheduler.scheduleSyncDelayedTask(instance, task, delayTicks));
        }
    }

    /**
     * Runs the task async even if the plugin is disabled for some reason.
     * <p>
     * Schedules the run on the next tick.
     *
     * @param task
     *
     * @return
     */
    public static BukkitTask runAsync(final Runnable task) {
        return runLaterAsync(0, task);
    }

    /**
     * Runs the task async even if the plugin is disabled for some reason.
     * <p>
     * Schedules the run on the next tick.
     *
     * @param task
     *
     * @return
     */
    public static BukkitTask runLaterAsync(final Runnable task) {
        return runLaterAsync(0, task);
    }

    /**
     * Runs the task async even if the plugin is disabled for some reason.
     *
     * @param delayTicks
     * @param task
     *
     * @return the task or null
     */
    public static BukkitTask runLaterAsync(final int delayTicks, Runnable task) {
        final BukkitScheduler scheduler = Bukkit.getScheduler();
        final JavaPlugin instance = RosePlugin.getInstance();

        try {
            return runIfDisabled(task) ? null : delayTicks == 0 ? task instanceof BukkitRunnable ? ((BukkitRunnable) task).runTaskAsynchronously(instance) : scheduler.runTaskAsynchronously(instance, task) : task instanceof BukkitRunnable ? ((BukkitRunnable) task).runTaskLaterAsynchronously(instance, delayTicks) : scheduler.runTaskLaterAsynchronously(instance, task, delayTicks);

        } catch (final NoSuchMethodError err) {
            return runIfDisabled(task) ? null
                    : delayTicks == 0
                    ? getTaskFromId(scheduler.scheduleAsyncDelayedTask(instance, task))
                    : getTaskFromId(scheduler.scheduleAsyncDelayedTask(instance, task, delayTicks));
        }
    }

    /**
     * Runs the task timer even if the plugin is disabled.
     *
     * @param repeatTicks the delay between each execution
     * @param task        the task
     *
     * @return the bukkit task or null
     */
    public static BukkitTask runTimer(final int repeatTicks, final Runnable task) {
        return runTimer(0, repeatTicks, task);
    }

    /**
     * Runs the task timer even if the plugin is disabled.
     *
     * @param delayTicks  the delay before first run
     * @param repeatTicks the delay between each run
     * @param task        the task
     *
     * @return the bukkit task or null if error
     */
    public static BukkitTask runTimer(final int delayTicks, final int repeatTicks, Runnable task) {

        try {
            return runIfDisabled(task) ? null : task instanceof BukkitRunnable ? ((BukkitRunnable) task).runTaskTimer(RosePlugin.getInstance(), delayTicks, repeatTicks) : Bukkit.getScheduler().runTaskTimer(RosePlugin.getInstance(), task, delayTicks, repeatTicks);

        } catch (final NoSuchMethodError err) {
            return runIfDisabled(task) ? null
                    : getTaskFromId(Bukkit.getScheduler().scheduleSyncRepeatingTask(RosePlugin.getInstance(), task, delayTicks, repeatTicks));
        }
    }

    /**
     * Runs the task timer async even if the plugin is disabled.
     *
     * @param repeatTicks
     * @param task
     *
     * @return
     */
    public static BukkitTask runTimerAsync(final int repeatTicks, final Runnable task) {
        return runTimerAsync(0, repeatTicks, task);
    }

    /**
     * Runs the task timer async even if the plugin is disabled.
     *
     * @param delayTicks
     * @param repeatTicks
     * @param task
     *
     * @return
     */
    public static BukkitTask runTimerAsync(final int delayTicks, final int repeatTicks, Runnable task) {

        try {
            return runIfDisabled(task) ? null : task instanceof BukkitRunnable ? ((BukkitRunnable) task).runTaskTimerAsynchronously(RosePlugin.getInstance(), delayTicks, repeatTicks) : Bukkit.getScheduler().runTaskTimerAsynchronously(RosePlugin.getInstance(), task, delayTicks, repeatTicks);

        } catch (final NoSuchMethodError err) {
            return runIfDisabled(task) ? null
                    : getTaskFromId(Bukkit.getScheduler().scheduleAsyncRepeatingTask(RosePlugin.getInstance(), task, delayTicks, repeatTicks));
        }
    }

    /*
     * A compatibility method that converts the given task id into a bukkit task
     */
    private static BukkitTask getTaskFromId(int taskId) {

        for (final BukkitTask task : Bukkit.getScheduler().getPendingTasks())
            if (task.getTaskId() == taskId)
                return task;

        return null;
    }

    // Check our plugin instance if it's enabled
    // In case it is disabled, just runs the task and returns true
    // Otherwise we return false and the task will be run correctly in Bukkit scheduler
    // This is fail-safe to critical save-on-exit operations in case our plugin is improperly reloaded (PlugMan) or malfunctions
    private static boolean runIfDisabled(final Runnable run) {
        if (!RosePlugin.getInstance().isEnabled()) {
            run.run();
            return true;
        }
        return false;
    }

    /**
     * Call an event in Bukkit and return whether it was fired
     * successfully through the pipeline (NOT cancelled)
     *
     * @param event the event
     *
     * @return true if the event was NOT cancelled
     */
    public static boolean callEvent(final Event event) {
        Bukkit.getPluginManager().callEvent(event);

        return event instanceof Cancellable ? !((Cancellable) event).isCancelled() : true;
    }

    /**
     * Convenience method for registering events as our instance
     *
     * @param listener
     */
    public static void registerEvents(final Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, RosePlugin.getInstance());
    }
}