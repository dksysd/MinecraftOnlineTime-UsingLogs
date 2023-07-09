import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Users {
    private final HashMap<String, OnlineTimes> USERS = new HashMap<>();

    public void addTime(String id, LocalTime time, boolean isJoin) {
        if (!id.equals("[Textile Backup] Not enough space")) {
            id = id.replace("[AFK]", "").trim();
            OnlineTimes onlineTimes;
            if (USERS.containsKey(id)) {
                onlineTimes = USERS.get(id);
            } else {
                onlineTimes = new OnlineTimes();
                USERS.put(id, onlineTimes);
            }
            onlineTimes.addTime(time, isJoin);
        }
    }

    public boolean[] getTimeStamp() {
        boolean[] timeStamps = new boolean[3600 * 24 + 1];

        for (Map.Entry<String, OnlineTimes> entry : USERS.entrySet()) {
            int start = -1;
            for (OnlineTimes.OnlineTime onlineTime : entry.getValue().ONLINE_TIMES) {
                if (onlineTime.isJoin) {
                    start = onlineTime.time.toSecondOfDay();
                } else {
                    if (start > 0) {
                        for (int i = start, end = onlineTime.time.toSecondOfDay(); i <= end; i++) {
                            timeStamps[i] = true;
                        }
                    }
                }
            }
        }

        return timeStamps;
    }

    public long onlineTimes() {
        return onlineTimes(getTimeStamp());
    }

    public long onlineTimes(boolean[] timeStamp) {
        long onlineTimes = 0;
        for (boolean stamp : timeStamp) {
            if (stamp) {
                onlineTimes++;
            }
        }

        return onlineTimes;
    }

    public String printTimeStamp(boolean[] timeStamp) {
        StringBuilder sb = new StringBuilder();
        sb.append("TIMESTAMP=\n0\t");
        for (int i = 60, h = 1; i < timeStamp.length; i += 60) {
            sb.append(timeStamp[i] ? "■" : "□");
            if (i % 3600 == 0 && h < 24) {
                sb.append('\n').append(h++).append("\t");
            }
        }
        return sb.append("\nTOTAL_TIMES=").append(toHMS(onlineTimes(timeStamp))).toString();
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("USERS={");
        if (!USERS.isEmpty()) {
            for (Map.Entry<String, OnlineTimes> entry : USERS.entrySet()) {
                sb.append("\n\t").append(entry.getKey()).append(" : ").append(entry.getValue().toString()).append(",");
            }
            sb.delete(sb.lastIndexOf(","), sb.length()).append("\n");
        }
        sb.append("}");

        return sb.toString();
    }

    public String toHMS(long seconds) {
        long h = seconds / 3600;
        seconds %= 3600;
        long m = seconds / 60;
        seconds %= 60;
        return h + "h " + m + "m " + seconds + "s";
    }

    public static class OnlineTimes {
        private final ArrayList<OnlineTime> ONLINE_TIMES = new ArrayList<>();

        public void addTime(LocalTime time, boolean isJoin) {
            ONLINE_TIMES.add(new OnlineTime(time, isJoin));
        }

        public long totalTime() {
            long time = 0;
            if (!ONLINE_TIMES.isEmpty()) {
                OnlineTime prevTime = ONLINE_TIMES.get(0);
                for (int i = 1, size = ONLINE_TIMES.size(); i < size; i += 2) {
                    Duration duration = Duration.between(prevTime.time, ONLINE_TIMES.get(i).time);
                    time += duration.getSeconds();
                }
            }
            return time;
        }

        @Override
        public String toString() {
            return "OnlineTimes{" +
                    "ONLINE_TIMES=" + ONLINE_TIMES + ", " +
                    "TOTAL_TIMES=" + totalTime() + "s" +
                    '}';
        }

        record OnlineTime(LocalTime time, boolean isJoin) {
        }
    }
}
