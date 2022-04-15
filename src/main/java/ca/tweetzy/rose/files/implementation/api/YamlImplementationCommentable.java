package ca.tweetzy.rose.files.implementation.api;

import ca.tweetzy.rose.files.comments.YamlCommentMapper;
import ca.tweetzy.rose.files.configuration.comments.CommentType;
import ca.tweetzy.rose.files.configuration.comments.Commentable;
import ca.tweetzy.rose.files.file.YamlConfigurationOptions;

/**
 * A YAML implementation capable of processing comments.
 */
public abstract class YamlImplementationCommentable implements YamlImplementation, Commentable {

    /**
     * A comment mapper to add comments to sections or values
     **/
    protected YamlCommentMapper yamlCommentMapper;

    /**
     * Configuration options for loading and dumping Yaml.
     */
    protected YamlConfigurationOptions options;

    @Override
    public void setComment(final String path, final String comment, final CommentType type) {
        if (this.yamlCommentMapper != null) {
            this.yamlCommentMapper.setComment(path, comment, type);
        }
    }

    @Override
    public String getComment(final String path, final CommentType type) {
        if (this.yamlCommentMapper == null) {
            return null;
        }
        return this.yamlCommentMapper.getComment(path, type);
    }

    /**
     * Get the comment mapper to get or set comments.
     *
     * @return the comment mapper or null if parsing comments is not enabled
     */
    public YamlCommentMapper getCommentMapper() {
        return this.yamlCommentMapper;
    }

    @Override
    public void configure(final YamlConfigurationOptions options) {
        this.options = options;
    }
}
