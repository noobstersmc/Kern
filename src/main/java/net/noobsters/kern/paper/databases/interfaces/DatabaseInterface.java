package net.noobsters.kern.paper.databases.interfaces;

public interface DatabaseInterface {
    public boolean connect() throws Exception;

    public boolean disconnect() throws Exception;

    public boolean isConnected();

    public String performQuerry(Object... objects);

}
