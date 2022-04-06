package ca.tweetzy.rose;

import ca.tweetzy.rose.metrics.Metrics;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * Date Created: April 06 2022
 * Time Created: 11:05 a.m.
 *
 * @author Kiran Hart
 */
public abstract class RosePlugin extends JavaPlugin implements Listener {

	protected ConsoleCommandSender console = Bukkit.getConsoleSender();
	private boolean emergencyStop = false;

	static {
		/* NBT-API */
		MinecraftVersion.getLogger().setLevel(Level.WARNING);
		MinecraftVersion.disableUpdateCheck();
	}

	/*
	-------------------------------------------------------------------------
	Handle the loading & disabling
	-------------------------------------------------------------------------
	 */

	@Override
	public final void onLoad() {
		try {
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

		// TODO FANCY ENABLE MESSAGE

		try {
			// TODO LOCALE

			onFlight();

			// metrics
			if (this.getBStatsId() != -1) {
				Metrics metrics = new Metrics(this, this.getBStatsId());

				if (!this.getCustomMetricCharts().isEmpty())
					this.getCustomMetricCharts().forEach(metrics::addCustomChart);
			}

		} catch (final Throwable throwable) {
			criticalErrorOnPluginStartup(throwable);
			return;
		}

		console.sendMessage(" ");
	}

	@Override
	public final void onDisable() {
		if (this.emergencyStop) {
			return;
		}

		onSleep();
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

	/*
	-------------------------------------------------------------------------
	Misc
	-------------------------------------------------------------------------
	 */
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
		return console;
	}

	protected void emergencyStop() {
		this.emergencyStop = true;
		Bukkit.getPluginManager().disablePlugin(this);
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
