package ca.tweetzy.rose.files.implementation;

import ca.tweetzy.rose.files.comments.YamlCommentDumper;
import ca.tweetzy.rose.files.comments.YamlCommentMapper;
import ca.tweetzy.rose.files.comments.YamlCommentParser;
import ca.tweetzy.rose.files.configuration.ConfigurationSection;
import ca.tweetzy.rose.files.configuration.comments.CommentType;
import ca.tweetzy.rose.files.exceptions.InvalidConfigurationException;
import ca.tweetzy.rose.files.file.YamlConfiguration;
import ca.tweetzy.rose.files.file.YamlConfigurationOptions;
import ca.tweetzy.rose.files.file.YamlFile;
import ca.tweetzy.rose.files.implementation.snakeyaml.SnakeYamlConstructor;
import ca.tweetzy.rose.files.implementation.snakeyaml.SnakeYamlImplementation;
import ca.tweetzy.rose.files.implementation.snakeyaml.SnakeYamlRepresenter;
import ca.tweetzy.rose.files.utils.SectionUtils;
import ca.tweetzy.rose.files.utils.SupplierIO;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Map;

/**
 * Default YAML implementation using snakeyaml high-level API and a custom comment parser / dumper.
 */
public class SimpleYamlImplementation extends SnakeYamlImplementation {

    public SimpleYamlImplementation() {
        super(new SnakeYamlRepresenter());
    }

    public SimpleYamlImplementation(final SnakeYamlRepresenter yamlRepresenter) {
        super(yamlRepresenter);
    }

    public SimpleYamlImplementation(final SnakeYamlConstructor yamlConstructor, final SnakeYamlRepresenter yamlRepresenter, final DumperOptions yamlOptions) {
        super(yamlConstructor, yamlRepresenter, yamlOptions);
    }

    @Override
    public void setComment(final String path, final String comment, final CommentType type) {
        if (this.yamlCommentMapper == null) {
            this.options.useComments(true);
            this.yamlCommentMapper = new YamlCommentMapper(this.options);
        }
        this.yamlCommentMapper.setComment(path, comment, type);
    }

    @Override
    @SuppressWarnings("DuplicateThrows")
    public void load(final SupplierIO.Reader readerSupplier, final ConfigurationSection section) throws IOException, InvalidConfigurationException {
        if (readerSupplier != null) {
            this.load(readerSupplier.get(), section);

            if (this.options.useComments()) {
                this.parseComments(readerSupplier.get());
            }
        }
    }

    @Override
    @SuppressWarnings("DuplicateThrows")
    public void load(final Reader reader, final ConfigurationSection section) throws IOException, InvalidConfigurationException {
        this.configure(this.options);

        if (reader != null && section != null) {
            try {
                final Map<?, ?> values = this.getYaml().load(reader);

                if (values != null) {
                    SectionUtils.convertMapsToSections(values, section);
                }
            } catch (final YAMLException e) {
                throw new InvalidConfigurationException(e);
            } catch (final ClassCastException e) {
                throw new InvalidConfigurationException("Top level is not a Map.");
            } finally {
                reader.close();
            }
        }
    }

    @Override
    public void dump(final Writer writer, final ConfigurationSection section) throws IOException {
        this.configure(this.options);

        if (this.hasContent(writer, section)) {
            if (this.options.useComments()) {
                final YamlCommentDumper commentDumper = new YamlCommentDumper(
                        this.parseComments(),
                        dumper -> super.dumpYaml(dumper, section),
                        writer
                );
                commentDumper.dump();
            } else {
                this.dumpYaml(writer, section);
            }
        }
    }

    /**
     * Parse comments from the current file configuration.
     *
     * @return a comment mapper with comments parsed
     *
     * @throws IOException if it hasn't been possible to parse the comments
     */
    private YamlCommentMapper parseComments() throws IOException {
        if (this.yamlCommentMapper != null) {
            return this.yamlCommentMapper;
        }
        final YamlConfiguration config = this.options.configuration();
        Reader reader = null;
        if (config instanceof YamlFile) {
            final File configFile = ((YamlFile) config).getConfigurationFile();
            if (configFile != null) {
                reader = configFile.exists() ? Files.newBufferedReader(configFile.toPath(), this.options.charset()) : null;
            }
        }
        return this.parseComments(reader);
    }

    /**
     * Parse comments from a reader.
     *
     * @param reader Reader of a Configuration to parse.
     *
     * @return a comment mapper with comments parsed
     *
     * @throws InvalidConfigurationException if it hasn't been possible to read the contents
     */
    public YamlCommentMapper parseComments(final Reader reader) throws InvalidConfigurationException {
        try {
            if (reader != null) {
                this.yamlCommentMapper = new YamlCommentParser(this.options, reader);
                ((YamlCommentParser) this.yamlCommentMapper).parse();
            } else {
                this.yamlCommentMapper = new YamlCommentMapper(this.options);
            }
            return this.yamlCommentMapper;
        } catch (IOException e) {
            throw new InvalidConfigurationException(e);
        }
    }

    @Override
    public void configure(final YamlConfigurationOptions options) {
        super.configure(options);

        // Use custom comment processor
        this.getLoaderOptions().setProcessComments(false);
        this.getDumperOptions().setProcessComments(false);
    }

}
