public class Server {
    private String name = "unnamed";
    private int id = -1;
    private int[] serverSpec = new int[] { 0, 0, 0 };

    public Server(String serverName, int serverID) {
        name = serverName;
        id = serverID;
    }

    public Server(String serverName, int serverID, int[] spec) {
        name = serverName;
        id = serverID;
        serverSpec = spec;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return id;
    }

    public int[] getSpec() {
        return serverSpec;
    }
}
