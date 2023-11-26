package pl.ynfuien.ycolorfulitems.api.event;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.ycolorfulitems.commands.ItemnameCommand;

/**
 * Event fired, when player wants to change item's display name, with the plugin's /itemname command.
 */
public class ItemNameChangeEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;

    private final Player player;
    private final ItemStack itemStack;
    private Component displayName;
    private final String input;
    private boolean useSignature;
    private Player signaturePlayer;


    public ItemNameChangeEvent(@NotNull Player player, @NotNull ItemStack itemStack, @Nullable Component displayName, @NotNull String input, boolean useSignature) {
        super(false);

        this.player = player;
        this.itemStack = itemStack;
        this.displayName = displayName;
        this.input = input;
        this.useSignature = useSignature;
        this.signaturePlayer = player;
    }

    /**
     * Gets player that used the command.
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets item stack whose display name will be changed.
     * @return item stack in player's main hand
     */
    @NotNull
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Gets formatted display name.
     * @return component or null if it's a name reset
     */
    @Nullable
    public Component getDisplayName() {
        return displayName;
    }

    /**
     * Gets text input that player used in the command.
     * @return string of arguments concatenated with space (" ")
     */
    @NotNull
    public String getInput() {
        return input;
    }

    /**
     * Gets whether to use signature. Value will be the same as in the plugin's config, till changed using set method.
     * @return whether to save signature in the item's NBT
     */
    public boolean useSignature() {
        return useSignature;
    }

    /**
     * Gets the player whose signature will be saved in the item's NBT.
     */
    @NotNull
    public Player getSignaturePlayer() {
        return signaturePlayer;
    }

    /**
     * Gets info whether player is resetting item's display name.
     * @return true if it is a reset
     */
    public boolean isReset() {
        return input.equalsIgnoreCase(ItemnameCommand.RESET_SUBCOMMAND);
    }


    /**
     * Sets display name to be used as name of the item stack. Set it to null to reset item's name.
     * @param displayName new display name component
     */
    public void setDisplayName(@Nullable Component displayName) {
        this.displayName = displayName;
    }

    /**
     * Sets whether to save player's signature in the item's NBT.
     * @param useSignature true to use signature
     */
    public void setUseSignature(boolean useSignature) {
        this.useSignature = useSignature;
    }

    /**
     * Sets the player whose signature will be saved in the item's NBT.
     */
    public void setSignaturePlayer(Player signaturePlayer) {
        this.signaturePlayer = signaturePlayer;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets whether to cancel this event. Note that plugin won't send any message to the player if event gets cancelled.
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
