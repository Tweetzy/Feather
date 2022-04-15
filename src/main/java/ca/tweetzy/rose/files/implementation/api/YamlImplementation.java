package ca.tweetzy.rose.files.implementation.api;

import ca.tweetzy.rose.files.configuration.ConfigurationSection;
import ca.tweetzy.rose.files.exceptions.InvalidConfigurationException;
import ca.tweetzy.rose.files.file.YamlConfigurationOptions;
import ca.tweetzy.rose.files.utils.SupplierIO;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * A YAML implementation to load/parse and dump/save a {@link ConfigurationSection}.
 */
public interface YamlImplementation {

    /**
     * Load Yaml to a configuration section.
     *
     * @param reader  a reader of Yaml contents to load
     * @param section the configuration to fill with the contents
     *
     * @throws IOException                   if cannot read contents.
     * @throws InvalidConfigurationException if contents is not a valid Yaml configuration
     */
    void load(final Reader reader, final ConfigurationSection section) throws IOException, InvalidConfigurationException;

    /**
     * Load Yaml to a configuration section.
     *
     * @param readerSupplier a function providing a reader of Yaml contents to load
     * @param section        the configuration to fill with the contents
     *
     * @throws IOException                   if cannot read contents.
     * @throws InvalidConfigurationException if contents is not a valid Yaml configuration
     */
    default void load(final SupplierIO.Reader readerSupplier, final ConfigurationSection section) throws IOException, InvalidConfigurationException {
        this.load(readerSupplier.get(), section);
    }

    /**
     * Load Yaml to a configuration section.
     *
     * @param contents a Yaml string with contents to load
     * @param section  the configuration to fill with the contents
     *
     * @throws IOException                   if cannot read contents.
     * @throws InvalidConfigurationException if contents is not a valid Yaml string
     */
    default void load(final String contents, final ConfigurationSection section) throws IOException, InvalidConfigurationException {
        this.load(new StringReader(contents), section);
    }

    /**
     * Dump section values to Yaml.
     *
     * @param writer  writer to dump values
     * @param section section with values to dump
     */
    void dump(final Writer writer, final ConfigurationSection section) throws IOException;

    /**
     * Dump section values to a Yaml string.
     *
     * @param section section with values to dump
     *
     * @return the values as a valid Yaml string
     */
    default String dump(final ConfigurationSection section) throws IOException {
        final StringWriter stringWriter = new StringWriter();

        this.dump(stringWriter, section);

        return stringWriter.toString();
    }

    /**
     * Apply the configuration options to this implementation.
     *
     * @param options yaml options
     */
    void configure(final YamlConfigurationOptions options);

}
