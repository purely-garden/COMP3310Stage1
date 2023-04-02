import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class Job {
    public int id;
    private int core;
    private int mem;
    private int disk;
    private String code;
    private int[] jobInfo;
    

    public Job(int jobID, int recCore, int recMem, int recDisk) {
        id = jobID;
        core = recCore;
        mem = recMem;
        disk = recDisk;
    }

    public Job(String job) {
        // JOBN 37 0 653 3 700 3800
        // submitTime jobID estRuntime resource-Requirements(core memory disk)
        Pattern patternInt = Pattern.compile("[0-9]+");
        Pattern patternAlp = Pattern.compile("[a-z]+", Pattern.CASE_INSENSITIVE);
        Matcher matchInt = patternInt.matcher(job);
        Matcher matchAlpha = patternAlp.matcher(job);

        if (matchAlpha.find()) {
            code = matchAlpha.group();
        }

        jobInfo = new int[6];
        for (int k = 0; k < 6; k++) {
            if (matchInt.find()) {
                jobInfo[k] = Integer.parseInt(matchInt.group(0));
            }
        }
        id = jobInfo[1];
        core = jobInfo[3];
        mem = jobInfo[4];
        disk = jobInfo[5];
    }
    
    public boolean canRun(String server) {
            Pattern serverSpec = Pattern.compile("\\d\\s(\\d+)\\s(\\d+)\\s(\\d+)");
            //serverType serverID state curStartTime core memory disk #wJobs #rJobs
            Matcher serverPerfMatcher = serverSpec.matcher(server);
            int coreR = -1;
            int memR = -1;
            int diskR = -1;
            if(serverPerfMatcher.find()) {
                coreR = Integer.parseInt(serverPerfMatcher.group(1));
                memR = Integer.parseInt(serverPerfMatcher.group(2));
                diskR = Integer.parseInt(serverPerfMatcher.group(3));
            }
            return core <= coreR && mem <= memR && disk <= diskR;
    }

    public List<String> findCapable(String[] serverList) {
        Pattern patternLetters = Pattern.compile("[a-z]+", Pattern.CASE_INSENSITIVE);
        ArrayList<String> capableServers = new ArrayList<>();
        for (int i = 0; i < serverList.length; i++) {
            Matcher wordMatcher = patternLetters.matcher(serverList[i]);
            if (canRun(serverList[i]) && wordMatcher.find()) {
                capableServers.add(wordMatcher.group());
            }
        }
        return capableServers;
    }

    public int getId() {
        return id;
    }

    public int getCore() {
        return core;
    }

    public int getMem() {
        return mem;
    }

    public int getDisk() {
        return disk;
    }

    public int[] getInfo() {
        return new int[] { id, core, mem, disk };
    }

    public String getCode() {
        return code;
    }

    public boolean isNONE() {
        return code.compareTo("NONE") == 0;
    }

    public int[] getArray() {
        return jobInfo;
    }
}
