package core.cache;

import org.json.JSONObject;

public class BankCache {

    private final JSONObject data;

    public BankCache(JSONObject data) {
        this.data = data.getJSONObject("bank_data");
    }

    public JSONObject getData() {
        return this.data;
    }

    public double getTotalOSRSAction(String action) {
        double total = 0;
        for (String key : this.data.keySet()) {
            if (this.data.getJSONObject(key).get("ledger").equals("07")) {
                if (this.data.getJSONObject(key).get("action").equals(action)) {
                    total += Double.parseDouble(String.valueOf(this.data.getJSONObject(key).get("amount")));
                }
            }
        }

        return total;
    }

    public double getTotalRS3Action(String action) {
        double total = 0;
        for (String key : this.data.keySet()) {
            if (this.data.getJSONObject(key).get("ledger").equals("rs3")) {
                if (this.data.getJSONObject(key).get("action").equals(action)) {
                    total += Double.parseDouble(String.valueOf(this.data.getJSONObject(key).get("amount")));
                }
            }
        }

        return total;
    }
}
