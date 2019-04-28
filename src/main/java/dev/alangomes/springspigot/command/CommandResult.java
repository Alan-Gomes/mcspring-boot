package dev.alangomes.springspigot.command;

import lombok.Value;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Value
public class CommandResult {

    private static final CommandResult UNKNOWN_COMMAND = new CommandResult(null, false, false);

    boolean errored;

    boolean exists;

    List<String> output;

    private CommandResult(Collection<String> output, boolean errored, boolean exists) {
        this.errored = errored;
        this.exists = exists;
        this.output = output != null ? Collections.unmodifiableList(new LinkedList<>(output)) : Collections.emptyList();
    }

    public CommandResult(Collection<String> output) {
        this(output, false, true);
    }

    public CommandResult(String output, boolean errored) {
        this(Collections.singletonList(output), errored, true);
    }

    public static CommandResult unknown() {
        return UNKNOWN_COMMAND;
    }

}
