package net.wb1016.fdnotif.minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface Message {

    String getMessage();

    MessageObjectType getType();

    TextType getTextType();

    default String getKey() {
        return "";
    }

    default Object[] getArgs() {
        return new Object[0];
    }

    default String getSource() {
        return "";
    }

    default UUID getAuthorUUID() {
        return UUID.randomUUID();
    }

    default <T extends Message> T setAuthorUUID(UUID uuid) {
        return (T) this;
    }

    default boolean hasAuthorUUID() {
        return false;
    }

    default String getCommandName() {
        return "";
    }

    default List<Message> getSibblings() {
        return new ArrayList<>();
    }

    default <T extends Message> T addSibbling(Message message) {
        return (T) this;
    }

    enum MessageObjectType {
        STRING,
        TEXT
    }

    enum TextType {
        TRANSLATABLE,
        LITERAL,
        COMMAND,
        UNKNOWN
    }
}
