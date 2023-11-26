package pl.ynfuien.ycolorfulitems.hooks.placeholderapi.placeholders;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pl.ynfuien.ycolorfulitems.api.YColorfulItemsAPI;
import pl.ynfuien.ycolorfulitems.hooks.placeholderapi.Placeholder;

import java.util.UUID;

public class SignaturePlaceholders implements Placeholder {

    @Override
    public String name() {
        return "signature";
    }

    @Override
    public String getPlaceholder(String id, OfflinePlayer p) {
        if (p == null || !p.isOnline()) return "offline player";

        PlayerInventory inv = p.getPlayer().getInventory();
        ItemStack itemStack = inv.getItemInMainHand();
        if (itemStack.getType().isAir()) return "N/A";

        UUID signature = YColorfulItemsAPI.getItemSignature(itemStack);
        if (signature == null) return "none";

        // Placeholder: %yci_signature_uuid%
        // Returns: held item's signature
        if (id.equals("uuid")) {
            return signature.toString();
        }

        // Placeholder: %yci_signature_username%
        // Returns: signature's player's username
        if (id.equals("username")) {
            return Bukkit.getOfflinePlayer(signature).getName();
        }

        return null;
    }
}
