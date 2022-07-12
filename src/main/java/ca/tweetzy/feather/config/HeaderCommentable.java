package ca.tweetzy.feather.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

// Originally from SongodaCore
public interface HeaderCommentable {

    void setHeaderComment(@Nullable Supplier<String> comment);

    default void setHeaderComment(@Nullable String comment) {
        setHeaderComment(() -> comment);
    }

    @Nullable Supplier<String> getHeaderComment();

    @NotNull String generateHeaderCommentLines();
}
