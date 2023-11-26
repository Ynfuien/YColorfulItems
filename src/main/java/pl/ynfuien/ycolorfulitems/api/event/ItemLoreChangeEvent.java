package pl.ynfuien.ycolorfulitems.api.event;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Event fired, when player wants to change lore of an item in the hand, with the plugin's /lore command.
 */
public class ItemLoreChangeEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;

    private final Type type;
    private final Player player;
    private final ItemStack itemStack;
    private final int lineNumber;
    private Component formattedLine;
    private final String input;


    public ItemLoreChangeEvent(@NotNull Type type, @NotNull Player player, @NotNull ItemStack itemStack, int lineNumber, @Nullable Component formattedLine, @Nullable String input) {
        super(false);

        this.type = type;
        this.player = player;
        this.itemStack = itemStack;
        this.lineNumber = lineNumber;
        this.formattedLine = formattedLine;
        this.input = input;
    }

    /**
     * Gets type of the lore change.
     */
    @NotNull
    public Type getType() {
        return type;
    }

    /**
     * Gets player that used the command.
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets item stack whose lore will be changed.
     * @return item stack in player's main hand
     */
    @NotNull
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Gets line number to be set/removed/added. Numbers start with the 0 for the first line.
     * <p>It will be -1 if {@link #getType()} is {@link Type#CLEAR Type.CLEAR}</p>
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Gets formatted line that is set/added/removed.
     * <p>It will be null if {@link #getType()} is {@link Type#CLEAR Type.CLEAR}</p>
     * @return component or null
     */
    @Nullable
    public Component getFormattedLine() {
        return formattedLine;
    }

    /**
     * Gets text input that player used in the command.
     * <p>It will be null if {@link #getType()} is {@link Type#CLEAR Type.CLEAR} or {@link Type#REMOVE Type.REMOVE}</p>
     * @return string or null
     */
    @Nullable
    public String getInput() {
        return input;
    }

    /**
     * Gets info whether player used '-e' to set/add the line
     * <p>It will also return true if {@link #getType()} is {@link Type#CLEAR Type.CLEAR} or {@link Type#REMOVE Type.REMOVE}</p>
     * @return true if formatted line component is empty
     */
    public boolean isLineEmpty() {
        if (formattedLine == null) return true;
        return formattedLine.equals(Component.empty());
    }

    /**
     * Sets component to be used in the line.
     * <p>It has no effect if {@link #getType()} is {@link Type#CLEAR Type.CLEAR} or {@link Type#REMOVE Type.REMOVE}</p>
     * @param formattedLine new formatted component
     */
    public void setFormattedLine(@NotNull Component formattedLine) {
        Preconditions.checkArgument(formattedLine != null, "FormattedLine cannot be null");

        this.formattedLine = formattedLine;
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


    public enum Type {
        /** Event adding a new line to the lore. */
        ADD,
        /**
         * Event setting specified line in the lore.
         * <p>It can also add necessary lines, to be able to change provided line number.</p>
         */
        SET,
        /** Event removing specified line from the lore. */
        REMOVE,
        /** Event clearing the entire lore. */
        CLEAR
    }
}
