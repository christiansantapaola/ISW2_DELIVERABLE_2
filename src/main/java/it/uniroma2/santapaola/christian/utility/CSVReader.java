package it.uniroma2.santapaola.christian.utility;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

public class CSVReader {
    private String filePath;
    private File file;
    private BufferedReader reader;
    private String[] attribute;
    private long pos;

    public CSVReader(String filePath) throws IOException {
        this.filePath = filePath;
        this.file = new File(filePath);
        this.reader = new BufferedReader(new FileReader(file));
        this.attribute = reader.readLine().split(",");
        this.pos = 0;
    }

    public CSVReader(String filePath, String[] attribute) throws IOException {
        this.filePath = filePath;
        this.file = new File(filePath);
        this.reader = new BufferedReader(new FileReader(file));
        this.attribute = attribute;
        this.pos = 0;
    }

    public class CSVRow {
        private HashMap<String, String> row;
        CSVRow(String[] values) {
            row = new HashMap<>();
            for (int i = 0; i < values.length; i++) {
                row.put(attribute[i], values[i]);
            }
        }

        public String getValue(String attribute) {
            return row.get(attribute);
        }
    }

    public CSVRow readRow() throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return null;
        }
        pos++;
        return new CSVRow(line.split(","));
    }

    public String[] readRowAsString() throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return null;
        }
        pos++;
        return line.split(",");
    }


    public long getPosition() throws IOException {
        return pos;

    }

}
