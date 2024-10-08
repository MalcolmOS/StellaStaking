package core.exports;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

public class BankExport extends CSVExport {

    public BankExport(JSONObject data) {
        super(data);
    }

    @Override
    public void exportAll() throws IOException {

        final FileWriter writer = new FileWriter(this.createExportFile());
        writer.append("ID,REQUESTING_ADMIN,ADMIN_NAME,USER,USER_NAME,AMOUNT,ACTION,LEDGER,TIMESTAMP\n");
        final JSONObject users = super.data.getJSONObject("users");
        final JSONObject bankData = super.data.getJSONObject("bank_data");
        for (String key : bankData.keySet()) {

            final JSONObject transaction = bankData.getJSONObject(key);
            Timestamp ts = new Timestamp(Long.parseLong(String.valueOf(transaction.get("timestamp"))) * 1_000);
            Date date = new Date(ts.getTime());

            writer.append(key).append(",").append("'").append(String.valueOf(transaction.get("requesting_admin"))).append(",").
                    append(super.getHashTag(String.valueOf(users.getJSONObject(String.valueOf(transaction.get("requesting_admin"))).get("name")))).append(",").
                    append("'").append(String.valueOf(transaction.get("user"))).append(",").
                    append(super.getHashTag(String.valueOf(users.getJSONObject(String.valueOf(transaction.get("user"))).get("name")))).append(",").
                    append(String.valueOf(transaction.get("amount"))).append(",").append(String.valueOf(transaction.get("action"))).append(",").
                    append("'").append(String.valueOf(transaction.get("ledger"))).append(",").
                    append(String.valueOf(date)).append("\n");
        }

        writer.close();
    }

    @Override
    public File createExportFile() {
        File file = new File(System.getProperty("user.home") + "/Downloads/" + "BANKDATA.csv");
        if (file.exists()) {
            boolean result = file.delete();
        }
        return file;
    }
}
