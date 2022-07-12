package ca.tweetzy.feather.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

// Originally from SongodaCore
public interface NodeCommentable {
    void setNodeComment(@NotNull String key, @Nullable Supplier<String> comment);

    default void setNodeComment(@NotNull String key, @Nullable String comment) {
        setNodeComment(key, () -> comment);
    }

    @Nullable Supplier<String> getNodeComment(@Nullable String key);
}
