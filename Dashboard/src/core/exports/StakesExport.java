package core.exports;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

public class StakesExport extends CSVExport {

    public StakesExport(JSONObject data) {
        super(data);
    }

    @Override
    public void exportAll() throws IOException {

        final FileWriter writer = new FileWriter(this.createExportFile());
        writer.append("ID,USER,NAME,AMOUNT,LEDGER,RESULT,TIMESTAMP\n");
        final JSONObject users = super.data.getJSONObject("users");
        final JSONObject stakes = super.data.getJSONObject("stakes");
        for (String key : stakes.keySet()) {

            final JSONObject stake = stakes.getJSONObject(key);
            Timestamp ts = new Timestamp(Long.parseLong(String.valueOf(stake.get("timestamp"))) * 1_000);
            Date date = new Date(ts.getTime());

            writer.append(key).append(",").append("'").append(String.valueOf(stake.get("user"))).append(",").
                    append(super.getHashTag(String.valueOf(users.getJSONObject(String.valueOf(stake.get("user"))).get("name")))).append(",").
                    append(String.valueOf(stake.get("amount"))).append(",").append("'").append(String.valueOf(stake.get("ledger"))).
                    append(",").append(String.valueOf(stake.get("result"))).append(",").append(String.valueOf(date)).append("\n");
        }

        writer.close();
    }

    @Override
    public File createExportFile() {
        File file = new File(System.getProperty("user.home") + "/Downloads/" + "STAKES.csv");
        if (file.exists()) {
            boolean result = file.delete();
        }
        return file;
    }
}
