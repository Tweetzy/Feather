/*
 * Feather
 * Copyright 2022 Kiran Hart
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ca.tweetzy.feather.utils;

import ca.tweetzy.feather.FeatherPlugin;
import lombok.NonNull;

import java.io.File;
import java.io.FileFilter;

public final class FileScanner {

    public static File[] scan(@NonNull String directory, @NonNull String extension) {
        // Remove initial dot, if any
        if (extension.startsWith("."))
            extension = extension.substring(1);

        final File dataFolder = new File(FeatherPlugin.getInstance().getDataFolder(), directory);

        if (!dataFolder.exists())
            dataFolder.mkdirs();

        final String finalExtension = extension;

        return dataFolder.listFiles((FileFilter) file -> !file.isDirectory() && file.getName().endsWith("." + finalExtension));
    }
}
