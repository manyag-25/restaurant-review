package application.parser;

import application.command.*;
import application.exception.InvalidArgumentException;
import application.exception.MissingArgumentException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CommandParserTest {

    @Test
    public void getCommand_exit_success() throws InvalidArgumentException, MissingArgumentException {
        Command command = CommandParser.getCommand("exit");
        assertTrue(command instanceof ExitCommand);
        assertTrue(command.isTerminatingCommand());
    }

    @Test
    public void getCommand_list_success() throws InvalidArgumentException, MissingArgumentException {
        Command command = CommandParser.getCommand("list");
        assertTrue(command instanceof ListReviewsCommand);
    }

    @Test
    public void getCommand_unknown_returnsUnknownCommand() throws InvalidArgumentException, MissingArgumentException {
        Command command = CommandParser.getCommand("unknowncommand");
        assertTrue(command instanceof UnknownCommand);
    }

    @Test
    public void getCommand_addReview_success() throws InvalidArgumentException, MissingArgumentException {
        Command command = CommandParser.getCommand("review Great food! /food 5 /clean 4 /service 5 /tag Good Food");
        assertTrue(command instanceof AddReviewCommand);
    }

    @Test
    public void getCommand_delete_success() throws InvalidArgumentException, MissingArgumentException {
        Command command = CommandParser.getCommand("delete 1");
        assertTrue(command instanceof DeleteReviewCommand);
    }

    @Test
    public void getCommand_filter_success() throws InvalidArgumentException, MissingArgumentException {
        Command command = CommandParser.getCommand("filter /hastag Tag1 /notag Tag2 /condition food > 3");
        assertTrue(command instanceof FilterReviewsCommand);
    }

    @Test
    public void getCommand_sort_success() throws InvalidArgumentException, MissingArgumentException {
        Command command = CommandParser.getCommand("sort asc /by food");
        assertTrue(command instanceof SortReviewsCommand);
    }

    @Test
    public void getCommand_addTag_success() throws InvalidArgumentException, MissingArgumentException {
        Command command = CommandParser.getCommand("addtag 1 /tag NewTag");
        assertTrue(command instanceof AddTagsCommand);
    }

    @Test
    public void getCommand_deleteTag_success() throws InvalidArgumentException, MissingArgumentException {
        Command command = CommandParser.getCommand("deletetag 1 /tag OldTag");
        assertTrue(command instanceof DeleteTagsCommand);
    }

    @Test
    public void getCommand_resolve_returnsResolveReviewCommand() throws InvalidArgumentException, MissingArgumentException {
        Command command = CommandParser.getCommand("resolve 1");
        assertTrue(command instanceof ResolveReviewCommand);
    }

    @Test
    public void getCommand_unresolve_returnsUnresolveReviewCommand() throws InvalidArgumentException, MissingArgumentException {
        Command command = CommandParser.getCommand("unresolve 1");
        assertTrue(command instanceof UnresolveReviewCommand);
    }

    @Test
    public void getCommand_emptyInput_returnsUnknownCommand() throws InvalidArgumentException, MissingArgumentException {
        assertTrue(CommandParser.getCommand("") instanceof UnknownCommand);
        assertTrue(CommandParser.getCommand("   ") instanceof UnknownCommand);
    }
}
