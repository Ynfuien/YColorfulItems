package pl.ynfuien.ycolorfulitems.api.event;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Event fired, when player wants to set/clear the line on the sign, with the plugin's /sign command.
 */
public class SignEditEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;

    private final Type type;
    private final Sign sign;
    private final SignSide signSide;
    private final Player player;
    private final int lineNumber;
    private Component formattedLine;
    private final String input;


    public SignEditEvent(@NotNull Type type, @NotNull Sign sign, @NotNull SignSide signSide, @NotNull Player player, int lineNumber, @Nullable Component formattedLine, @Nullable String input) {
        super(false);

        this.type = type;
        this.sign = sign;
        this.signSide = signSide;
        this.player = player;
        this.lineNumber = lineNumber;
        this.formattedLine = formattedLine;
        this.input = input;
    }

    /**
     * Gets type of the sign edit.
     */
    @NotNull
    public Type getType() {
        return type;
    }

    /**
     * Gets the sign that is being edited.
     */
    @NotNull
    public Sign getSign() {
        return sign;
    }

    /**
     * Gets sign side that is being edited.
     */
    @NotNull
    public SignSide getSignSide() {
        return signSide;
    }

    /**
     * Gets player that used the command.
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }


    /**
     * Gets line number to be set/cleared. Numbers start with the 0 for the first line.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Gets formatted line that is set/cleared.
     * @return component
     */
    @NotNull
    public Component getFormattedLine() {
        return formattedLine;
    }

    /**
     * Gets text input that player used in the command.
     * <p>It will be null if {@link #getType()} is {@link Type#CLEAR Type.CLEAR}</p>
     * @return string or null
     */
    @Nullable
    public String getInput() {
        return input;
    }

    /**
     * Sets component to be used in the line.
     * <p>It has no effect if {@link #getType()} is {@link Type#CLEAR Type.CLEAR}</p>
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
        /** Event setting specified line on the sign. */
        SET,
        /** Event clearing specified line on the sign. */
        CLEAR
    }
}
