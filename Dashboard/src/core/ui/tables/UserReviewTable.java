package core.ui.tables;

import java.util.LinkedHashMap;

public class UserReviewTable implements TableData {

    private final LinkedHashMap<String, String> data;

    public UserReviewTable(LinkedHashMap<String, String> data) {
        this.data = data;
    }

    @Override
    public Object[][] getAll() {
        final Object[][] dataArray = new Object[this.data.size()][];
        int i = 0;
        for (String key : this.data.keySet()) {
            final String value = String.valueOf(this.data.get(key));
            dataArray[i] = new Object[]{key, value};
            i++;
        }

        return dataArray;
    }

    @Override
    public String[] getAllColumns() {
        return new String[]{" ", " "};
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
