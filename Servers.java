
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Servers {
    String[] list;
    String largestServers;
    int largestServerCount;
    Pattern patternLetters = Pattern.compile("^([\\w\\-\\.]+)\\w", Pattern.CASE_INSENSITIVE);
    Pattern patternServerSpec = Pattern.compile("\\d\\s(\\d+)\\s(\\d+)\\s(\\d+)");
    // serverType serverID state curStartTime core memory disk #wJobs #rJobs

    public Servers(String[] serverList) {
        list = serverList;
    }

    public boolean canRun(Job anEmployment, String server) {
        int[] serverSpec = serverHardware(server);
        int[] positionRequirements = anEmployment.getArray();
        return positionRequirements[1] <= serverSpec[0] && positionRequirements[2] <= serverSpec[1]
                && positionRequirements[3] <= serverSpec[2];
    }

    public int[] serverHardware(String server) {
        Matcher serverPerfMatcher = patternServerSpec.matcher(server);
        int[] hardware = new int[3];
        if (serverPerfMatcher.find()) {
            hardware[0] = Integer.parseInt(serverPerfMatcher.group(1));
            hardware[1] = Integer.parseInt(serverPerfMatcher.group(2));
            hardware[2] = Integer.parseInt(serverPerfMatcher.group(3));
        }
        return hardware;
    }

    public List<String> findCapable(Job anEmployment) {
        ArrayList<String> capableServers = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            Matcher wordMatcher = patternLetters.matcher(list[i]);
            if (canRun(anEmployment, list[i]) && wordMatcher.find()) {
                capableServers.add(wordMatcher.group());
            }
        }
        return capableServers;
    }

    public String findLargest() {
        String largestServerName = "";
        int serverCount = 1;
        int[] largest = new int[] { 0, 0, 0 };
        for (int i = 0; i < list.length; i++) {
            int[] current = serverHardware(list[i]);
            Matcher wordMatcher = patternLetters.matcher(list[i]);
            if (wordMatcher.find()) {
                largestServerName = wordMatcher.group();
                if (current[0] > largest[0] && current[1] > largest[1] && current[2] > largest[2]) {
                    largest[0] = current[0];
                    largest[1] = current[1];
                    largest[2] = current[2];
                    serverCount = 1;
                } else if (current[0] == largest[0] && current[1] == largest[1] && current[2] == largest[2]) {
                    serverCount++;
                }
            }
        }
        largestServers = largestServerName;
        largestServerCount = serverCount;
        return largestServers;
    }

    public String getLargestServers() {
        return largestServers;
    }

    public int getLargestServerCount() {
        return largestServerCount;
    }
}
