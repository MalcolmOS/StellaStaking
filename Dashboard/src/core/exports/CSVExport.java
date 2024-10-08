package core.exports;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public abstract class CSVExport {

    protected JSONObject data;

    public CSVExport(JSONObject data) {
        this.data = data;
    }

    public abstract void exportAll() throws IOException;

    public abstract File createExportFile();

    protected String getHashTag(String string) {
        int index = string.lastIndexOf(" ");
        if (index == -1) {
            return string;
        }
        return string.substring(0, index) + "#"
                + string.substring(index + " ".length());
    }
}
