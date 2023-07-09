package analysis;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.time.LocalTime;
import java.util.*;

public abstract class Analysis {
    public static final String JOIN = "joined", LEFT = "left";
    private final UserList USER_LIST;
    private Users users;

    public Analysis(UserList userList) {
        USER_LIST = userList;
        users = new Users() {
            @Override
            public void addRecord(String name, LocalTime start, LocalTime end) {
                if (USER_LIST.contains(name)) {
                    super.addRecord(name, start, end);
                }
            }
        };
    }

    public void analysis(String logPath, String outputPath) throws IOException {
        File logFile = new File(logPath);
        if (logFile.isDirectory()) {
            File[] logs = logFile.listFiles();
            if (logs == null) {
                throw new FileNotFoundException("No logs found.");
            }

            mergeLogs(logs);

            for (File log : logFile.listFiles()) {
                analysis(log, outputPath);
            }
        } else {
            analysis(logFile, outputPath);
        }
    }

    private void analysis(File log, String outputPath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(log));

        String line;
        // 누가 언제 접속 했는지 기록
        HashMap<String, LocalTime> temp = new HashMap<>();

        // 주요 처리
        while ((line = br.readLine()) != null) {
            if (line.contains(JOIN)) {
                temp.put(parseName(line), parseTime(line));
            } else if (line.contains(LEFT)) {
                String name = parseName(line);
                users.addRecord(name, temp.remove(name), parseTime(line));
            }
        }

        // 나머지 처리 (날짜가 바뀌기 전에 나가지 않은 경우)
        if (!temp.isEmpty()) {
            for (Map.Entry<String, LocalTime> entry : temp.entrySet()) {
                users.addRecord(entry.getKey(), entry.getValue(), null);
            }
        }
        save(outputPath, log.getName());
    }

    private void save(String outputPath, String fileName) throws IOException {
        File outputDirectory = new File(outputPath);
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        if (!outputDirectory.isDirectory()) {
            throw new NotDirectoryException(outputPath + " is not directory.");
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath + "\\" + fileName + ".txt"));
        bw.write(toString());
        bw.close();

        users = new Users();
    }

    private void mergeLogs(File[] logs) throws IOException {
        for (int i = 1, length = logs.length, from = 0; i < length; i++) {
            String name = logs[i].getName();
            if (name.charAt(name.lastIndexOf("-") + 1) == '1') {
                if (from + 1 < i) {
                    mergeFilesToOne(Arrays.copyOfRange(logs, from, i));
                }
                from = i;
            }
        }
    }

    private void mergeFilesToOne(File[] files) throws IOException {
        File outputFile = new File(files[0].getAbsolutePath());
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile, true));

        for (int i = 1; i < files.length; i++) {
            File file = files[i];

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                bw.write(line + "\n");
            }
            bw.flush();
            br.close();

            Files.delete(file.toPath());
            System.out.println(file.getName() + " deleted.");
        }

        bw.close();
        System.out.println("Successfully merge to " + outputFile.getName() + ".");
    }

    private String parseName(String line) {
        int index = line.indexOf(": ") + 2;
        String name = line.substring(index, line.indexOf(' ', index));
        // 혹시 있을 칭호 처리
        index = name.indexOf(']');
        if (index > 0) {
            name = name.substring(index + 1);
        }
        return name;
    }

    private LocalTime parseTime(String line) {
        try {
            return LocalTime.parse(line.substring(line.indexOf('[') + 1, line.indexOf(']')));
        } catch (StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    public boolean[] getTimeStamp() {
        boolean[] timeStamp = new boolean[86_401]; // 3600 * 24 + 1

        for (Map.Entry<String, ArrayList<TimeRecord>> entry : users.entrySet()) {
            for (TimeRecord timeRecord : entry.getValue()) {
                for (int index = timeRecord.start().toSecondOfDay(), end = timeRecord.end().toSecondOfDay(); index <= end; index++) {
                    timeStamp[index] = true;
                }
            }
        }

        return timeStamp;
    }

    public String timeStampToString(boolean[] timeStamp) {
        StringBuilder sb = new StringBuilder();
        sb.append("TIMESTAMP=\n0\t");
        for (int i = 60, h = 1; i < timeStamp.length; i += 60) {
            sb.append(timeStamp[i] ? "■" : "□");
            if (i % 3600 == 0 && h < 24) {
                sb.append('\n').append(h++).append("\t");
            }
        }
        return sb.toString();
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

    public String usersToString() {
        StringBuilder sb = new StringBuilder();

        sb.append("USERS={");
        Set<Map.Entry<String, ArrayList<TimeRecord>>> entrySet = users.entrySet();

        if (entrySet.size() > 0) {
            for (Map.Entry<String, ArrayList<TimeRecord>> entry : entrySet) {
                sb.append("\n\t").append(entry.getKey()).append(" : ").append(entry.getValue().toString()).append(",");
            }
            sb.deleteCharAt(sb.length() - 1).append('\n');
        }
        sb.append('}');

        return sb.toString();
    }

    public abstract String toString();
}
