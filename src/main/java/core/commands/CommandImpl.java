package core.commands;

import java.util.*;

public abstract class CommandImpl implements Command {
    protected String rowCommandLine;
    protected String[] rowCommands;
    protected String name;
    protected Map<String, String> options;
    protected List<String> params;

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    @Override
    public boolean checkZeroParams() {
        return params.size() == 0;
    }

    public CommandImpl(String rowCommandLine) {
        this.rowCommandLine = rowCommandLine;
        options = new HashMap<>();
        params = new ArrayList<>();
        rowCommands = rowCommandLine.split(" ");
        name = rowCommands[0].trim();
        parseCommands(rowCommands);
    }


    private void parseCommands(String[] rowCommands) {
        Arrays.stream(rowCommands).skip(1).forEach(params::add);
    }

}
