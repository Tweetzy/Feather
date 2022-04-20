package ca.tweetzy.rose;

import ca.tweetzy.rose.database.DataManagerAbstract;
import ca.tweetzy.rose.files.ConfigSetting;
import ca.tweetzy.rose.files.file.YamlFile;
import ca.tweetzy.rose.utils.Common;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Date Created: April 06 2022
 * Time Created: 11:05 a.m.
 *
 * @author Kiran Hart
 */
public abstract class RosePlugin extends JavaPlugin implements Listener {

    protected YamlFile config = new YamlFile(getDataFolder() + "/config.yml");
    protected ConsoleCommandSender console = Bukkit.getConsoleSender();
    private boolean emergencyStop = false;

    /**
     * The instance of this plugin
     */
    private static volatile RosePlugin instance;

    public static RosePlugin getInstance() {
        if (instance == null) {
            instance = JavaPlugin.getPlugin(RosePlugin.class);
        }
        return instance;
    }

	/*
	-------------------------------------------------------------------------
	Handle the loading & disabling
	-------------------------------------------------------------------------
	 */

    @Override
    public final void onLoad() {
        try {
            getInstance();
            this.config.createOrLoadWithComments();
            this.config.addSetting(new ConfigSetting(this.config, "language", "english", "The default language for the plugin"));
            onWake();
        } catch (final Throwable throwable) {
            criticalErrorOnPluginStartup(throwable);
        }
    }

    @Override
    public final void onEnable() {
        if (this.emergencyStop) {
            setEnabled(false);
            return;
        }

        console.sendMessage(" "); // blank line to separate chatter
        console.sendMessage(Common.colorize("#00a87f============================="));
        console.sendMessage(Common.colorize(String.format("#00ce74%s &fv&e%s #CBCBCBby #00ce74Tweetzy", getDescription().getName(), getDescription().getVersion())));
        console.sendMessage(Common.colorize(String.format("#00ce74Developer#CBCBCB: &e%s", String.join(", ", getDescription().getAuthors()))));

        try {
            onFlight();

            if (emergencyStop) {
                console.sendMessage(Common.colorize("#8C1053~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"));
                console.sendMessage(" ");
                return;
            }

            // metrics
            if (this.getBStatsId() != -1) {
                console.sendMessage(Common.colorize(String.format("&8[#00ce74RoseCore&8]#CBCBCB Enabling metrics for #00ce74%s", getDescription().getName())));
                Metrics metrics = new Metrics(this, this.getBStatsId());

                if (!this.getCustomMetricCharts().isEmpty())
                    this.getCustomMetricCharts().forEach(metrics::addCustomChart);
            }

        } catch (final Throwable throwable) {
            criticalErrorOnPluginStartup(throwable);
            console.sendMessage(Common.colorize("#8C1053~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"));
            console.sendMessage(" ");
            return;
        }

        console.sendMessage(Common.colorize("#00a87f============================="));
        console.sendMessage(" ");
    }

    @Override
    public final void onDisable() {
        if (this.emergencyStop) {
            return;
        }

        onSleep();
    }

    @Override
    public final void reloadConfig() {
        try {
            this.config.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        onStretch();
    }


	/*
	-------------------------------------------------------------------------
	Loader
	-------------------------------------------------------------------------
	 */

    /**
     * Called during {@link JavaPlugin#onLoad()}
     */
    protected void onWake() {
    }

    /**
     * Called during {@link JavaPlugin#onEnable()}
     */
    protected abstract void onFlight();

    /**
     * Called during {@link JavaPlugin#onDisable()}
     */
    protected void onSleep() {
    }

    /**
     * Called during {@link JavaPlugin#reloadConfig()}
     */
    protected void onStretch() {
    }

    /*
    -------------------------------------------------------------------------
    Misc
    -------------------------------------------------------------------------
     */

    public String getPluginName() {
        return getDescription().getName();
    }

    public String getPluginDescription() {
        return getDescription().getDescription();
    }

    public String getVersion() {
        return getDescription().getVersion();
    }

    protected int getBStatsId() {
        return -1;
    }

    protected List<Metrics.CustomChart> getCustomMetricCharts() {
        return Collections.emptyList();
    }

    protected int getSpigotId() {
        return -1;
    }

    public ConsoleCommandSender getConsole() {
        return this.console;
    }

    public YamlFile getCoreConfig() {
        return this.config;
    }

    protected void emergencyStop() {
        this.emergencyStop = true;
        Bukkit.getPluginManager().disablePlugin(this);
    }


    protected void shutdownDataManager(DataManagerAbstract dataManager) {
        // 3 minutes is overkill, but we just want to make sure
        shutdownDataManager(dataManager, 15, TimeUnit.MINUTES.toSeconds(3));
    }

    protected void shutdownDataManager(DataManagerAbstract dataManager, int reportInterval, long secondsUntilForceShutdown) {
        dataManager.shutdownTaskQueue();

        while (!dataManager.isTaskQueueTerminated() && secondsUntilForceShutdown > 0) {
            long secondsToWait = Math.min(reportInterval, secondsUntilForceShutdown);

            try {
                if (dataManager.waitForShutdown(secondsToWait, TimeUnit.SECONDS)) {
                    break;
                }

                getLogger().info(String.format("A DataManager is currently working on %d tasks... " +
                                "We are giving him another %d seconds until we forcefully shut him down " +
                                "(continuing to report in %d second intervals)",
                        dataManager.getTaskQueueSize(), secondsUntilForceShutdown, reportInterval));
            } catch (InterruptedException ignore) {
            } finally {
                secondsUntilForceShutdown -= secondsToWait;
            }
        }

        if (!dataManager.isTaskQueueTerminated()) {
            int unfinishedTasks = dataManager.forceShutdownTaskQueue().size();

            if (unfinishedTasks > 0) {
                getLogger().log(Level.WARNING,
                        String.format("A DataManager has been forcefully terminated with %d unfinished tasks - " +
                                "This can be a serious problem, please report it to us (Tweetzy)!", unfinishedTasks));
            }
        }
    }

    /**
     * Logs one or multiple errors that occurred during plugin startup and calls {@link #emergencyStop()} afterwards
     *
     * @param th The error(s) that occurred
     */
    protected void criticalErrorOnPluginStartup(Throwable th) {
        Bukkit.getLogger().log(Level.SEVERE,
                String.format(
                        "Unexpected error while loading %s v%s c%s: Disabling plugin!",
                        getDescription().getName(),
                        getDescription().getVersion(),
                        RoseCore.getCoreLibraryVersion()
                ), th);

        emergencyStop();
    }
}
