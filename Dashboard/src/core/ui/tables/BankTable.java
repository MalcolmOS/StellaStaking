package core.ui.tables;

import core.cache.UserCache;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.*;

public class BankTable implements TableData {

    private final JSONObject data;

    private UserCache users;

    public BankTable(JSONObject data) {
        this.data = data;
    }

    public void setUserContext(UserCache cache) {
        this.users = cache;
    }

    @Override
    public Object[][] getAll() {
        final Object[][] dataArray = new Object[this.data.length()][];
        for (int i = 0; i < this.data.length(); i++) {
            final JSONObject transaction = this.data.getJSONObject(String.valueOf(i + 1));
            final Timestamp ts = new Timestamp(Long.parseLong(String.valueOf(transaction.get("timestamp"))) * 1_000);
            final Date date = new Date(ts.getTime());
            dataArray[i] = new Object[]{transaction.get("requesting_admin"),
                    this.users.getName(String.valueOf(transaction.get("requesting_admin"))),
                    transaction.get("user"), this.users.getName(String.valueOf(transaction.get("user"))),
                    String.valueOf(transaction.get("amount")), transaction.get("ledger"),
                    transaction.get("action"), String.valueOf(date)};
        }

        Collections.reverse(Arrays.asList(dataArray));
        return dataArray;
    }

    @Override
    public String[] getAllColumns() {
        return new String[]{"Requesting Admin", "Admin Name", "User", "User name", "Amount", "Ledger", "Action", "Date"};
    }
}
