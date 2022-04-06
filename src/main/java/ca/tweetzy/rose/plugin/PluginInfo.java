package ca.tweetzy.rose.plugin;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;

/**
 * Date Created: April 06 2022
 * Time Created: 11:27 a.m.
 *
 * @author Kiran Hart
 */
public final class PluginInfo {

	protected final JavaPlugin javaPlugin;
	protected final int tweetzyId;
	protected final String coreIcon;
	protected final String coreLibraryVersion;
	protected final Material icon;

	private boolean hasUpdate = false;
	private String latestVersion;
	private String notification;
	private String changeLog;
	private String spigotLink;
	private String songodaLink;
	private String polymartLink;
	private JSONObject json;

	public PluginInfo(JavaPlugin javaPlugin, int songodaId, String icon, String coreLibraryVersion) {
		this.javaPlugin = javaPlugin;
		this.tweetzyId = songodaId;
		this.coreIcon = icon;
		this.icon = Material.getMaterial(icon);
		this.coreLibraryVersion = coreLibraryVersion;
	}

	public String getLatestVersion() {
		return this.latestVersion;
	}

	public void setLatestVersion(String latestVersion) {
		this.latestVersion = latestVersion;
		this.hasUpdate = latestVersion != null && !latestVersion.isEmpty() && !this.javaPlugin.getDescription().getVersion().equalsIgnoreCase(latestVersion);
	}

	public String getNotification() {
		return this.notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}

	public boolean hasUpdate() {
		return this.hasUpdate;
	}

	public void setHasUpdate(boolean hasUpdate) {
		this.hasUpdate = hasUpdate;
	}

	public String getChangeLog() {
		return this.changeLog;
	}

	public void setChangeLog(String changeLog) {
		this.changeLog = changeLog;
	}

	public String getSpigotLink() {
		return spigotLink;
	}

	public void setSpigotLink(String spigotLink) {
		this.spigotLink = spigotLink;
	}

	public String getSongodaLink() {
		return songodaLink;
	}

	public void setSongodaLink(String songodaLink) {
		this.songodaLink = songodaLink;
	}

	public String getPolymartLink() {
		return polymartLink;
	}

	public void setPolymartLink(String polymartLink) {
		this.polymartLink = polymartLink;
	}

	public JSONObject getJson() {
		return this.json;
	}

	public void setJson(JSONObject json) {
		this.json = json;
	}

	public JavaPlugin getJavaPlugin() {
		return javaPlugin;
	}

	public int getTweetzyId() {
		return this.tweetzyId;
	}

	public String getCoreIcon() {
		return this.coreIcon;
	}

	public String getCoreLibraryVersion() {
		return this.coreLibraryVersion;
	}
}
