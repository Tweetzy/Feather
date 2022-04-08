package ca.tweetzy.rose.utils;

import ca.tweetzy.rose.comp.enums.ServerVersion;
import lombok.NonNull;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Set;

/**
 * Date Created: April 07 2022
 * Time Created: 2:46 p.m.
 *
 * @author Kiran Hart
 */
public final class PlayerUtil {

    public static ItemStack getHand(@NonNull final Player player) {
        return getHand(player, Hand.MAIN);
    }

    public static ItemStack getHand(@NonNull final Player player, Hand hand) {
        if (ServerVersion.isServerVersionBelow(ServerVersion.V1_9)) {
            return player.getInventory().getItemInHand();
        }

        return hand == Hand.MAIN ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
    }

    public static int getNumberPermission(@NonNull final Player player, @NonNull final String permission, final int def) {
        final Set<PermissionAttachmentInfo> permissions = player.getEffectivePermissions();

        boolean set = false;
        int highest = 0;

        for (PermissionAttachmentInfo info : permissions) {
            final String perm = info.getPermission();

            if (!perm.startsWith(permission)) {
                continue;
            }

            final int index = perm.lastIndexOf('.');

            if (index == -1 || index == perm.length()) {
                continue;
            }

            String numStr = perm.substring(perm.lastIndexOf('.') + 1);
            if (numStr.equals("*")) {
                return def;
            }

            final int number = Integer.parseInt(numStr);

            if (number >= highest) {
                highest = number;
                set = true;
            }
        }

        return set ? highest : def;
    }

    public static boolean isInventoryEmpty(@NonNull final Player player) {
        final ItemStack[] everything = (ItemStack[]) ArrayUtils.addAll(player.getInventory().getContents(), player.getInventory().getArmorContents());

        for (final ItemStack i : everything)
            if (i != null && i.getType() != Material.AIR)
                return false;

        return true;
    }
}

enum Hand {
    MAIN, OFF
}
