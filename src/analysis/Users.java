package analysis;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Users {
    private final HashMap<String, ArrayList<TimeRecord>> USERS = new HashMap<>();

    public void addRecord(String name, LocalTime start, LocalTime end) {
        if (start == null) {
            start = LocalTime.MIN;
        }
        if (end == null) {
            end = LocalTime.MAX;
        }

        TimeRecord timeRecord = new TimeRecord(start, end);
        if (USERS.containsKey(name)) {
            USERS.get(name).add(timeRecord);
        } else {
            ArrayList<TimeRecord> arrayList = new ArrayList<>();
            arrayList.add(timeRecord);
            USERS.put(name, arrayList);
        }
    }

    public Set<Map.Entry<String, ArrayList<TimeRecord>>> entrySet() {
        return USERS.entrySet();
    }
}
