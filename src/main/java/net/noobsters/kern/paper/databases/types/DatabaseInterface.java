package net.noobsters.kern.paper.databases.types;

public interface DatabaseInterface {
    public boolean connect() throws Exception;

    public boolean disconnect() throws Exception;

    public boolean isConnected();

    public String performQuerry(Object... objects);

}
