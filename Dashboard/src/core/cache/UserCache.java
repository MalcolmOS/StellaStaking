package core.cache;

import org.json.JSONObject;

import java.util.LinkedHashMap;

public class UserCache {

    private final JSONObject data;

    public UserCache(JSONObject data) {
        this.data = data.getJSONObject("users");
    }

    public JSONObject getData() {
        return this.data;
    }

    public String getName(String id) {
        return this.getHashTag(String.valueOf(this.data.getJSONObject(id).get("name")));
    }

    public double getOSRSBalance(String id) {
        return Double.parseDouble(String.valueOf(this.data.getJSONObject(id).get("07")));
    }

    public double getRS3Balance(String id) {
        return Double.parseDouble(String.valueOf(this.data.getJSONObject(id).get("rs3")));
    }

    public int getTotalUsers() {
        return this.data.keySet().size();
    }

    public double getTotalBalanceByLedger(String ledger) {
        double total = 0;
        for (String user : this.data.keySet()) {
            total += Double.parseDouble(String.valueOf(this.data.getJSONObject(user).get(ledger)));
        }
        return total;
    }

    public double getAverageBalanceByLedger(String ledger) {
        double total = 0;
        for (String user : this.data.keySet()) {
            total += Double.parseDouble(String.valueOf(this.data.getJSONObject(user).get(ledger)));
        }
        return total / this.getTotalUsers();
    }

    public LinkedHashMap<String, JSONObject> getUserByName(String name) {
        final LinkedHashMap<String, JSONObject> map = new LinkedHashMap<>();
        for (String key : this.data.keySet()) {
            if (this.getHashTag(String.valueOf(this.data.getJSONObject(key).get("name"))).equals(name)) {
                map.put(key, this.data.getJSONObject(key));
            }
        }

        return map;
    }

    public LinkedHashMap<String, JSONObject> getUserByID(String id) {
        final LinkedHashMap<String, JSONObject> map = new LinkedHashMap<>();
        if (this.data.keySet().contains(id)) {
            map.put(id, this.data.getJSONObject(id));
        }
        return map;
    }

    private String getHashTag(String string) {
        int index = string.lastIndexOf(" ");
        if (index == -1) {
            return string;
        }
        return string.substring(0, index) + "#"
                + string.substring(index + " ".length());
    }
}
