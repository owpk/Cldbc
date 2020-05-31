package core.commands;

import java.util.List;

//TODO Need to refactor all of "commands" pckg
public interface Command {
    boolean checkZeroParams();
    String getAlias();
    String getDBName();
    List<String> getParams();
}
