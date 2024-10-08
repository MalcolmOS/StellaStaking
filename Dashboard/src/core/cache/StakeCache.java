package core.cache;

import org.json.JSONObject;

public class StakeCache {

    private final JSONObject data;

    public StakeCache(JSONObject data) {
        this.data = data.getJSONObject("stakes");
    }

    public JSONObject getData() {
        return this.data;
    }

    public double getTotalOSRSStakes() {
        return this.getTotalOSRSByResult("WIN") + this.getTotalOSRSByResult("LOSE");
    }

    public double getTotalRS3Stakes() {
        return this.getTotalRS3ByResult("WIN") + this.getTotalRS3ByResult("LOSE");
    }

    public double getTotalOSRSByResult(String result) {
        double amount = 0;
        for (String key : this.data.keySet()) {
            if (this.data.getJSONObject(key).get("ledger").equals("07")) {
                if (this.data.getJSONObject(key).get("result").equals(result)) {
                    amount += Double.parseDouble(String.valueOf(this.data.getJSONObject(key).get("amount")));
                }
            }
        }
        return amount;
    }

    public double getTotalRS3ByResult(String result) {
        double amount = 0;
        for (String key : this.data.keySet()) {
            if (this.data.getJSONObject(key).get("ledger").equals("rs3")) {
                if (this.data.getJSONObject(key).get("result").equals(result)) {
                    amount += Double.parseDouble(String.valueOf(this.data.getJSONObject(key).get("amount")));
                }
            }
        }
        return amount;
    }

    public double getTotalOSRSForUser(String user) {
        return this.getTotalOSRSForUserByResult(user, "WIN") + this.getTotalOSRSForUserByResult(user, "LOSE");
    }

    public double getTotalOSRSForUserByResult(String user, String result) {
        double amount = 0;
        for (String key : this.data.keySet()) {
            if (this.data.getJSONObject(key).get("ledger").equals("07")) {
                if (this.data.getJSONObject(key).get("user").equals(user)) {
                    if (this.data.getJSONObject(key).get("result").equals(result)) {
                        amount += Double.parseDouble(String.valueOf(this.data.getJSONObject(key).get("amount")));
                    }
                }
            }
        }
        return amount;
    }

    public double getTotalRS3ForUser(String user) {
        return this.getTotalRS3ForUserByResult(user, "WIN") + this.getTotalRS3ForUserByResult(user, "LOSE");
    }

    public double getTotalRS3ForUserByResult(String user, String result) {
        double amount = 0;
        for (String key : this.data.keySet()) {
            if (this.data.getJSONObject(key).get("ledger").equals("rs3")) {
                if (this.data.getJSONObject(key).get("user").equals(user)) {
                    if (this.data.getJSONObject(key).get("result").equals(result)) {
                        amount += Double.parseDouble(String.valueOf(this.data.getJSONObject(key).get("amount")));
                    }
                }
            }
        }
        return amount;
    }

    public double getWeeklyOSRSForUser(String user) {
        double amount = 0;
        for (String key : this.data.keySet()) {
            if (this.data.getJSONObject(key).get("ledger").equals("07")) {
                if (this.data.getJSONObject(key).get("user").equals(user)) {
                    if (this.data.getJSONObject(key).get("result").equals("WIN") || this.data.getJSONObject(key).get("result").equals("LOSE")) {
                        if (Long.parseLong(String.valueOf(this.data.getJSONObject(key).get("timestamp"))) * 1_000 > System.currentTimeMillis() - 604_800_000) {
                            amount += Double.parseDouble(String.valueOf(this.data.getJSONObject(key).get("amount")));
                        }
                    }
                }
            }
        }

        return amount;
    }

    public double getWeeklyRS3ForUser(String user) {
        double amount = 0;
        for (String key : this.data.keySet()) {
            if (this.data.getJSONObject(key).get("ledger").equals("rs3")) {
                if (this.data.getJSONObject(key).get("user").equals(user)) {
                    if (this.data.getJSONObject(key).get("result").equals("WIN") || this.data.getJSONObject(key).get("result").equals("LOSE")) {
                        if (Long.parseLong(String.valueOf(this.data.getJSONObject(key).get("timestamp"))) * 1_000 > System.currentTimeMillis() - 604_800_000) {
                            amount += Double.parseDouble(String.valueOf(this.data.getJSONObject(key).get("amount")));
                        }
                    }
                }
            }
        }

        return amount;
    }
}
