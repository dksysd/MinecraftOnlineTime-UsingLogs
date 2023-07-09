import java.io.*;
import java.nio.file.Files;
import java.time.LocalTime;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        String inputPath = "C:\\Users\\이원희\\Desktop\\logs\\logs", outputPath = "C:\\Users\\이원희\\Desktop\\logs\\analysis";
        File outputDirectory = new File(outputPath);
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        File directory = new File(inputPath);
        mergeFiles(directory);

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                File save = new File(outputPath + "\\" + file.getName().substring(0, file.getName().indexOf(".")) + ".txt");
                FileOutputStream fos = new FileOutputStream(save);
                PrintStream ps = new PrintStream(fos);
                System.setOut(ps);

                Users users = analysis(readFile(file));
                System.out.println(users);
                String timestampPrint = users.printTimeStamp(users.getTimeStamp());
                System.out.println(timestampPrint);
            }
        }
    }

    private static void mergeFiles(File directory) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            int start = 0;
            for (int i = 1; i < files.length; i++) {
                String name = files[i].getName();
                if (name.charAt(name.lastIndexOf("-") + 1) == '1') {
                    if (start + 1 < i) {
                        mergeFiles(Arrays.copyOfRange(files, start, i));
                    }
                    start = i;
                }
            }
        }
    }

    private static void mergeFiles(File[] files) throws IOException {
        File outputFile = new File(files[0].getAbsolutePath());
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile, true));

        for (int i = 1; i < files.length; i++) {
            File file = files[i];

            System.out.println(file.getName());
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                bw.write(line + "\n");
            }
            bw.flush();
            br.close();
            Files.delete(file.toPath());
        }

        bw.close();
    }

    private static BufferedReader readFile(File file) throws FileNotFoundException {
        return new BufferedReader(new FileReader(file));
    }

    private static Users analysis(BufferedReader br) throws IOException {
        Users users = new Users();

        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("joined")) {
                LocalTime time = parseTime(line);
                if (time != null) {
                    users.addTime(line.substring(line.indexOf(": ") + 2, line.indexOf(" joined")), time, true);
                }
            } else if (line.contains("left")) {
                LocalTime time = parseTime(line);
                if (time != null) {
                    users.addTime(line.substring(line.indexOf(": ") + 2, line.indexOf(" left")), time, false);
                }
            }
        }


        return users;
    }

    private static LocalTime parseTime(String line) {
        try {
            return LocalTime.parse(line.substring(line.indexOf('[') + 1, line.indexOf(']')));
        } catch (StringIndexOutOfBoundsException e) {
            return null;
        }
    }
}
