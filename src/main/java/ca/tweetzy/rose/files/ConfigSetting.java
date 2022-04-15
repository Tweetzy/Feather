package ca.tweetzy.rose.files;

import ca.tweetzy.rose.comp.enums.CompMaterial;
import ca.tweetzy.rose.files.file.YamlFile;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Date Created: April 14 2022
 * Time Created: 7:49 p.m.
 *
 * @author Kiran Hart
 */
@Getter
public final class ConfigSetting {

    private final YamlFile config;
    private final String key;
    private final Object defaultValue;
    private final String[] comments;

    public ConfigSetting(@NotNull YamlFile config, @NotNull String key, @NotNull Object defaultValue, String... comments) {
        this.config = config;
        this.key = key;
        this.defaultValue = defaultValue;
        this.comments = comments;
        config.addSetting(this);
    }

    public List<Integer> getIntegerList() {
        return config.getIntegerList(key);
    }

    public List<String> getStringList() {
        return config.getStringList(key);
    }

    public boolean getBoolean() {
        return config.getBoolean(key);
    }

    public boolean getBoolean(boolean def) {
        return config.getBoolean(key, def);
    }

    public int getInt() {
        return config.getInt(key);
    }

    public int getInt(int def) {
        return config.getInt(key, def);
    }

    public long getLong() {
        return config.getLong(key);
    }

    public long getLong(long def) {
        return config.getLong(key, def);
    }

    public double getDouble() {
        return config.getDouble(key);
    }

    public double getDouble(double def) {
        return config.getDouble(key, def);
    }

    public String getString() {
        return config.getString(key);
    }

    public String getString(String def) {
        return config.getString(key, def);
    }

    public Object getObject() {
        return config.get(key);
    }

    public Object getObject(Object def) {
        return config.get(key, def);
    }

    @NotNull
    public CompMaterial getMaterial() {
        return CompMaterial.matchCompMaterial(config.getString(key)).orElse(CompMaterial.STONE);
    }

    @NotNull
    public CompMaterial getMaterial(@NotNull CompMaterial def) {
        String val = config.getString(key);
        CompMaterial mat = val != null ? CompMaterial.matchCompMaterial(config.getString(key)).orElse(CompMaterial.STONE) : null;
        return mat != null ? mat : def;
    }
}
