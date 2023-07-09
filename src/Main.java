import analysis.Analysis;
import analysis.UserList;
import gzip.Decompress;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        String usercachePath = args[0];
        String logsPath = args[1];
        String outputPath = args[2];

//        String serverPath = "";
//        usercachePath = serverPath + "\\usercache.json";
//        logsPath = serverPath + "\\logs";
//        outputPath = serverPath + "\\logs\\analysis";

//        Decompress.decompressGzipFile(logsPath);


        Analysis analysis = new Analysis(new UserList(usercachePath)) {
            @Override
            public String toString() {
                String usersString = usersToString();
                boolean[] timeStamp = getTimeStamp();
                String timeStampString = timeStampToString(timeStamp);
                String onlineTime = toHMS(onlineTimes(timeStamp));

                return usersString + "\n" +
                        timeStampString + "\n" +
                        "ONLINETIME=" + onlineTime;
            }
        };

        analysis.analysis(logsPath, outputPath);
    }

    private static String toHMS(long seconds) {
        long h = seconds / 3600;
        seconds %= 3600;
        long m = seconds / 60;
        seconds %= 60;
        return h + "h " + m + "m " + seconds + "s";
    }
}
