package it.uniroma2.santapaola.christian.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/** CSVWriter è una classe di utility il cui scopo è facilitare la scrittura di CSV file.
 */
public class CSVWriter {
    private File csvOutput;
    private int noField;
    private String[] fieldName;
    private BufferedWriter writer;

    /** CSVWriter()
     * @param csvOutput: file dove scrivere l'output.
     * @param field: array di stringhe contentente il nome delle colonne.
     * @throws IOException
     * Il parametro field determina il nome delle colonne è il loro numero.
     * */
    public CSVWriter(File csvOutput, String[] field) throws IOException {
        this.csvOutput = csvOutput;
        fieldName = field;
        noField = field.length;
        writer = new BufferedWriter(new FileWriter(this.csvOutput));
    }

    /**
     * @throws IOException
     * writeFieldName() scrive sul file la riga di headers.
     */
    public void writeFieldName() throws IOException {
        writeLine(fieldName);
    }

    /**
     * WriteLine() scrive una riga sul file.
     * @param values: lista contenente i valori da scrivere, la sua lunghezza deve essere identica a quella del vettore
     *              field dato nel costruttore.
     * @throws IOException
     */
    public synchronized void writeLine(String[] values) throws IOException{
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

    /**
     * flush() esegue un operazione di flush sul file, forzando la sincronizzazione delle scritture.
     * @throws IOException
     */
    public void flush() throws IOException {
        writer.flush();
    }


}
