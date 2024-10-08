package core.ui.tables;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashMap;

public class UserTable implements TableData {

    private JSONObject data;
    private LinkedHashMap<?, ?> map = new LinkedHashMap<>();

    public UserTable(JSONObject data) {
        this.data = data;
    }

    public UserTable(LinkedHashMap<?, ?> map) {
        this.map = map;
    }

    @Override
    public Object[][] getAll() {

        if (this.data != null) {
            return this.getDataByJSON();
        } else if (this.map != null) {
            return this.getDataByMap();
        }
        return null;
    }

    @Override
    public String[] getAllColumns() {
        return new String[]{"", "Unique ID", "Name", "07 Balance", "RS3 Balance"};
    }

    private Object[][] getDataByJSON() {
        final Object[][] dataArray = new Object[this.data.length()][];
        int i = 0;
        for (Iterator<String> it = this.data.keys(); it.hasNext(); ) {
            final String userID = it.next();
            final JSONObject user = this.data.getJSONObject(userID);
            dataArray[i] = new Object[]{Boolean.FALSE, userID, this.getHashTag(String.valueOf(user.get("name"))),
                    String.valueOf(user.get("07")), String.valueOf(user.get("rs3"))};
            i++;
        }

        return dataArray;
    }

    private Object[][] getDataByMap() {
        final Object[][] dataArray = new Object[this.map.size()][];
        int i = 0;
        for (Object key : this.map.keySet()) {
            final JSONObject user = (JSONObject) this.map.get(String.valueOf(key));
            dataArray[i] = new Object[]{Boolean.FALSE, key, this.getHashTag(String.valueOf(user.get("name"))),
                    String.valueOf(user.get("07")), String.valueOf(user.get("rs3"))};
            i++;
        }

        return dataArray;
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
