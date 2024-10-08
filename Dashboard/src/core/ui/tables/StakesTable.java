package core.ui.tables;

import core.cache.UserCache;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class StakesTable implements TableData {

    private final JSONObject data;

    private UserCache users;

    public StakesTable(JSONObject data) {
        this.data = data;
    }

    public void setUserContext(UserCache cache) {
        this.users = cache;
    }


    @Override
    public Object[][] getAll() {
        final Object[][] dataArray = new Object[this.data.length()][];
        for (int i = 0; i < this.data.length(); i++) {
            final JSONObject stake = this.data.getJSONObject(String.valueOf(i + 1));
            final Timestamp ts = new Timestamp(Long.parseLong(String.valueOf(stake.get("timestamp"))) * 1_000);
            final Date date = new Date(ts.getTime());
            dataArray[i] = new Object[]{String.valueOf(i + 1), stake.get("user"), String.valueOf(this.users.getName(String.valueOf(stake.get("user")))),
                    String.valueOf(stake.get("amount")), stake.get("ledger"), stake.get("result"), String.valueOf(date)};
        }

        Collections.reverse(Arrays.asList(dataArray));
        return dataArray;
    }

    @Override
    public String[] getAllColumns() {
        return new String[]{"ID", "User", "Name", "Amount", "Ledger", "Result", "Date"};
    }
}
