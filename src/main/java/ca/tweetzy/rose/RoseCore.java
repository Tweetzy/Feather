package ca.tweetzy.rose;

import ca.tweetzy.rose.plugin.PluginInfo;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Date Created: April 06 2022
 * Time Created: 11:05 a.m.
 *
 * @author Kiran Hart
 */
public final class RoseCore {

	private final static Logger logger = Logger.getLogger("RoseCore");

	private final static int coreRevision = 1;

	private final static String coreVersion = "1.0.0";

	private final static Set<PluginInfo> registeredPlugins = new HashSet<>();
	private ArrayList<BukkitTask> tasks = new ArrayList<>();

	private static RoseCore INSTANCE = null;
	private JavaPlugin piggybackedPlugin;
	private EventListener loginListener;
	private ShadedEventListener shadingListener;

	public static boolean hasShading() {
		// sneaky hack to check the package name since maven tries to re-shade all references to the package string
		return !RoseCore.class.getPackage().getName().equals(new String(new char[]{'c', 'o', 'm', '.', 's', 'o', 'n', 'g', 'o', 'd', 'a', '.', 'c', 'o', 'r', 'e'}));
	}

	/*
	-------------------------------------------------------------------------
	BEGIN REGISTER
	-------------------------------------------------------------------------
	 */
	public static void registerPlugin(JavaPlugin plugin, int pluginID, Material icon) {
		registerPlugin(plugin, pluginID, icon == null ? "STONE" : icon.name(), coreVersion);
	}

	public static void registerPlugin(JavaPlugin plugin, int pluginID, String icon) {
		registerPlugin(plugin, pluginID, icon, "?");
	}

	public static void registerPlugin(JavaPlugin plugin, int pluginID, String icon, String coreVersion) {
		if (INSTANCE == null) {
			// First: are there any other instances of RoseCore active?
			for (Class<?> clazz : Bukkit.getServicesManager().getKnownServices()) {
				if (clazz.getSimpleName().equals("RoseCore")) {
					try {
						// test to see if we're up-to-date
						int otherVersion;
						try {
							otherVersion = (int) clazz.getMethod("getCoreVersion").invoke(null);
						} catch (Exception ignore) {
							otherVersion = -1;
						}

						if (otherVersion >= getCoreVersion()) {
							// use the active service
							// assuming that the other is greater than R6 if we get here ;)
							clazz.getMethod("registerPlugin", JavaPlugin.class, int.class, String.class, String.class).invoke(null, plugin, pluginID, icon, coreVersion);

							if (hasShading()) {
								(INSTANCE = new RoseCore()).piggybackedPlugin = plugin;
								INSTANCE.shadingListener = new ShadedEventListener();
								Bukkit.getPluginManager().registerEvents(INSTANCE.shadingListener, plugin);
							}

							return;
						}

						// we are newer than the registered service: steal all of its registrations
						// grab the old core's registrations
						List<?> otherPlugins = (List<?>) clazz.getMethod("getPlugins").invoke(null);

						// destroy the old core
						Object oldCore = clazz.getMethod("getInstance").invoke(null);
						Method destruct = clazz.getDeclaredMethod("destroy");
						destruct.setAccessible(true);
						destruct.invoke(oldCore);

						// register ourselves as the RoseCore service!
						INSTANCE = new RoseCore(plugin);
						INSTANCE.init();
						INSTANCE.register(plugin, pluginID, icon, coreVersion);
						Bukkit.getServicesManager().register(RoseCore.class, INSTANCE, plugin, ServicePriority.Normal);

						// we need (JavaPlugin plugin, int pluginID, String icon) for our object
						if (!otherPlugins.isEmpty()) {
							Object testSubject = otherPlugins.get(0);
							Class otherPluginInfo = testSubject.getClass();
							Method otherPluginInfo_getJavaPlugin = otherPluginInfo.getMethod("getJavaPlugin");
							Method otherPluginInfo_getTweetzyId = otherPluginInfo.getMethod("getTweetzyId");
							Method otherPluginInfo_getCoreIcon = otherPluginInfo.getMethod("getCoreIcon");
							Method otherPluginInfo_getCoreLibraryVersion = otherVersion >= 6 ? otherPluginInfo.getMethod("getCoreLibraryVersion") : null;

							for (Object other : otherPlugins) {
								INSTANCE.register(
										(JavaPlugin) otherPluginInfo_getJavaPlugin.invoke(other),
										(int) otherPluginInfo_getTweetzyId.invoke(other),
										(String) otherPluginInfo_getCoreIcon.invoke(other),
										otherPluginInfo_getCoreLibraryVersion != null ? (String) otherPluginInfo_getCoreLibraryVersion.invoke(other) : "?");
							}
						}

						return;
					} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
						plugin.getLogger().log(Level.WARNING, "Error registering core service", ex);
					}
				}
			}

			// register ourselves as the RoseCore service!
			INSTANCE = new RoseCore(plugin);
			INSTANCE.init();
			Bukkit.getServicesManager().register(RoseCore.class, INSTANCE, plugin, ServicePriority.Normal);
		}

		INSTANCE.register(plugin, pluginID, icon, coreVersion);
	}

	RoseCore() {
	}

	RoseCore(JavaPlugin javaPlugin) {
		piggybackedPlugin = javaPlugin;
		loginListener = new EventListener();
	}

	private void init() {
		shadingListener = new ShadedEventListener();
		Bukkit.getPluginManager().registerEvents(loginListener, piggybackedPlugin);
		Bukkit.getPluginManager().registerEvents(shadingListener, piggybackedPlugin);
	}

	private void register(JavaPlugin plugin, int pluginID, String icon, String libraryVersion) {
		logger.info(getPrefix() + "Hooked " + plugin.getName() + ".");
		PluginInfo info = new PluginInfo(plugin, pluginID, icon, libraryVersion);

		// todo locale updater
		registeredPlugins.add(info);
//		tasks.add(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> update(info), 60L));
	}

	/**
	 * Used to yield this core to a newer core
	 */
	private void destroy() {
		Bukkit.getServicesManager().unregister(RoseCore.class, INSTANCE);

		tasks.stream().filter(Objects::nonNull)
				.forEach(task -> Bukkit.getScheduler().cancelTask(task.getTaskId()));

		HandlerList.unregisterAll(loginListener);
		if (!hasShading()) {
			HandlerList.unregisterAll(shadingListener);
		}

		registeredPlugins.clear();
		loginListener = null;
	}

	/*
	-------------------------------------------------------------------------
	GETTERS
	-------------------------------------------------------------------------
	 */
	public static List<PluginInfo> getPlugins() {
		return new ArrayList<>(registeredPlugins);
	}

	public static int getCoreVersion() {
		return coreRevision;
	}

	public static String getCoreLibraryVersion() {
		return coreVersion;
	}

	public static String getPrefix() {
		return "[RoseCore] ";
	}

	public static Logger getLogger() {
		return logger;
	}

	public static boolean isRegistered(String plugin) {
		return registeredPlugins.stream().anyMatch(p -> p.getJavaPlugin().getName().equalsIgnoreCase(plugin));
	}

	public static JavaPlugin getHijackedPlugin() {
		return INSTANCE == null ? null : INSTANCE.piggybackedPlugin;
	}

	public static RoseCore getInstance() {
		return INSTANCE;
	}

	private static class ShadedEventListener implements Listener {
		boolean via;
		boolean proto = false;

		ShadedEventListener() {
			via = Bukkit.getPluginManager().isPluginEnabled("ViaVersion");
			// TODO CLIENT VERSION
//			if (via) {
//				Bukkit.getOnlinePlayers().forEach(p -> ClientVersion.onLoginVia(p, getHijackedPlugin()));
//				return;
//			}
//
//			proto = Bukkit.getPluginManager().isPluginEnabled("ProtocolSupport");
//			if (proto) {
//				Bukkit.getOnlinePlayers().forEach(p -> ClientVersion.onLoginProtocol(p, getHijackedPlugin()));
//			}
		}

		@EventHandler
		void onLogin(PlayerLoginEvent event) {
//			if (via) {
//				ClientVersion.onLoginVia(event.getPlayer(), getHijackedPlugin());
//				return;
//			}
//
//			if (proto) {
//				ClientVersion.onLoginProtocol(event.getPlayer(), getHijackedPlugin());
//			}
		}

		@EventHandler
		void onLogout(PlayerQuitEvent event) {
//			if (via) {
//				ClientVersion.onLogout(event.getPlayer());
//			}
		}

		@EventHandler
		void onEnable(PluginEnableEvent event) {
			// technically shouldn't have online players here, but idk
//			if (!via && (via = event.getPlugin().getName().equals("ViaVersion"))) {
//				Bukkit.getOnlinePlayers().forEach(p -> ClientVersion.onLoginVia(p, getHijackedPlugin()));
//			} else if (!proto && (proto = event.getPlugin().getName().equals("ProtocolSupport"))) {
//				Bukkit.getOnlinePlayers().forEach(p -> ClientVersion.onLoginProtocol(p, getHijackedPlugin()));
//			}
		}
	}

	private class EventListener implements Listener {
		final HashMap<UUID, Long> lastCheck = new HashMap<>();

		@EventHandler
		void onLogin(PlayerLoginEvent event) {
			final Player player = event.getPlayer();

			// don't spam players with update checks
			long now = System.currentTimeMillis();
			Long last = lastCheck.get(player.getUniqueId());

			if (last != null && now - 10000 < last) {
				return;
			}

			lastCheck.put(player.getUniqueId(), now);

			// is this player good to revieve update notices?
			if (!event.getPlayer().isOp() && !player.hasPermission("tweetzy.updatecheck")) return;

			// check for updates! ;)
			for (PluginInfo plugin : getPlugins()) {
				if (plugin.getNotification() != null && plugin.getJavaPlugin().isEnabled())
					Bukkit.getScheduler().runTaskLaterAsynchronously(plugin.getJavaPlugin(), () ->
							player.sendMessage("[" + plugin.getJavaPlugin().getName() + "] " + plugin.getNotification()), 10L);
			}
		}

		@EventHandler
		void onDisable(PluginDisableEvent event) {
			// don't track disabled plugins
			PluginInfo pi = registeredPlugins.stream().filter(p -> event.getPlugin() == p.getJavaPlugin()).findFirst().orElse(null);

			if (pi != null) {
				registeredPlugins.remove(pi);
			}

			if (event.getPlugin() == piggybackedPlugin) {
				// uh-oh! Abandon ship!!
				Bukkit.getServicesManager().unregisterAll(piggybackedPlugin);

				// can we move somewhere else?
				if ((pi = registeredPlugins.stream().findFirst().orElse(null)) != null) {
					// move ourselves to this plugin
					piggybackedPlugin = pi.getJavaPlugin();

					Bukkit.getServicesManager().register(RoseCore.class, INSTANCE, piggybackedPlugin, ServicePriority.Normal);
					Bukkit.getPluginManager().registerEvents(loginListener, piggybackedPlugin);
					Bukkit.getPluginManager().registerEvents(shadingListener, piggybackedPlugin);
				}
			}
		}
	}
}
