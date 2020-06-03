package core;

public interface CommandListener {
    void listenCommands(String command);
    void close();
}
