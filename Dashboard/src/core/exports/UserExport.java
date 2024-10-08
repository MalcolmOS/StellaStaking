package core.exports;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class UserExport extends CSVExport {

    public UserExport(JSONObject data) {
        super(data);
    }

    @Override
    public void exportAll() throws IOException {

        final FileWriter writer = new FileWriter(this.createExportFile());
        writer.append("ID,NAME,OSRS_BALANCE,RS3_BALANCE\n");
        final JSONObject users = super.data.getJSONObject("users");

        for (String key : users.keySet()) {

            final JSONObject user = users.getJSONObject(key);

            writer.append("'").append(key).append(",").append(super.getHashTag(String.valueOf(user.get("name")))).append(",").
                    append(String.valueOf(user.get("07"))).append(",").append(String.valueOf(user.get("rs3"))).append("\n");
        }

        writer.close();
    }

    @Override
    public File createExportFile() {
        File file = new File(System.getProperty("user.home") + "/Downloads/" + "STAKINGUSERS.csv");
        if (file.exists()) {
            boolean result = file.delete();
        }
        return file;
    }
}
