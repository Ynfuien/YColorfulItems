package pl.ynfuien.ycolorfulitems.api;

import com.google.common.base.Preconditions;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.ycolorfulitems.commands.ItemnameCommand;

import java.util.UUID;

public class YColorfulItemsAPI {

    //// Signatures
    @Nullable
    public static UUID getItemSignature(@NotNull ItemStack item) {
        Preconditions.checkArgument(item != null, "Item cannot be null");

        return ItemnameCommand.getSignature(item);
    }

    public static void setItemSignature(@NotNull ItemStack item, @NotNull UUID uuid) {
        Preconditions.checkArgument(item != null, "Item cannot be null");
        Preconditions.checkArgument(uuid != null, "Uuid cannot be null");

        ItemnameCommand.setSignature(item, uuid);
    }

    public static void removeItemSignature(@NotNull ItemStack item) {
        Preconditions.checkArgument(item != null, "Item cannot be null");

        ItemnameCommand.removeSignature(item);
    }
}
