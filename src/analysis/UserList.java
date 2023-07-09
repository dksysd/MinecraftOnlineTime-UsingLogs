package analysis;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserList {
    private final Set<String> USER_NAMES = new HashSet<>();

    public UserList(String usercachePath) throws IOException, ParseException {
        List<String> jsonStrings = Files.readAllLines(Path.of(usercachePath));
        String jsonString = jsonStrings.stream().collect(Collectors.joining(System.lineSeparator()));

        JSONArray jsonArray = (JSONArray) new JSONParser().parse(jsonString);

        if (!jsonArray.isEmpty()) {
            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;

                String name = (String) jsonObject.get("name");
                USER_NAMES.add(name);
            }
        }
    }

    public boolean contains(String name) {
        return USER_NAMES.contains(name);
    }

    public List<String> getNames() {
        return USER_NAMES.stream().toList();
    }
}
