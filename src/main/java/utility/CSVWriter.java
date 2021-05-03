package utility;

import java.io.*;

public class CSVWriter {
    private File csvOutput;
    private int noField;
    private String[] fieldName;
    private BufferedWriter writer;

    public CSVWriter(File csvOutput, String[] field) throws IOException {
        this.csvOutput = csvOutput;
        fieldName = field;
        noField = field.length;
        writer = new BufferedWriter(new FileWriter(this.csvOutput));
    }

    public void writeFieldName() throws IOException {
        writeLine(fieldName);
    }

    public void writeLine(String[] values) throws IOException{
        if (values.length != noField)
            throw new IllegalArgumentException("ERROR: values len is " + Integer.toString(values.length) + " while the number of column is " + noField);
        for (int i = 0; i < values.length; i++) {
            writer.append(values[i]);
            if (i == values.length - 1) {
                writer.append('\n');
            } else {
                writer.append(',');
            }
        }
    }

    public void flush() throws IOException {
        writer.flush();
    }


}
