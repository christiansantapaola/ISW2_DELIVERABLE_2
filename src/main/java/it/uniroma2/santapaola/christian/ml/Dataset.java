package it.uniroma2.santapaola.christian.ml;

import it.uniroma2.santapaola.christian.utility.Pair;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;


public class Dataset {
    private Instances data;
    private Attribute version;
    private int maxVersion;

    public Dataset(String dataCSV) throws Exception {
        this.data = ConverterUtils.DataSource.read(dataCSV);
        this.data.setClassIndex(data.attribute("Buggy").index());
        this.version = data.attribute("Version");
        this.maxVersion = (int) data.firstInstance().value(version);
        for (Instance instance: data) {
            if (this.maxVersion < (int) instance.value(version)) {
                this.maxVersion = (int) instance.value(version);
            }
        }
    }

    public Instances getData() {
        return data;
    }

    public Attribute getVersion() {
        return version;
    }

    public int getMaxVersion() {
        return maxVersion;
    }

    public Pair<Instances, Instances> tempSplit(int version) {
        Instances trainingSet = new Instances(data, 0);
        Instances testingSet = new Instances(data, 0);
        for (Instance row : data) {
            if ((int) row.value(version) < version) {
                trainingSet.add(row);
            } else if ((int) row.value(version) == version) {
                testingSet.add(row);
            }
        }
        return new Pair<>(trainingSet, testingSet);
    }

    public int getPositiveClassIndex() {
        return data.classAttribute().indexOfValue("YES");
    }


}
