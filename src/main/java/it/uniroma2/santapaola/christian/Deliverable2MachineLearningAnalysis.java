package it.uniroma2.santapaola.christian;

import it.uniroma2.santapaola.christian.ml.*;
import it.uniroma2.santapaola.christian.utility.CSVWriter;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.RandomForest;
import weka.filters.Filter;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;
import weka.filters.unsupervised.attribute.Standardize;
import weka.filters.unsupervised.instance.Resample;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Deliverable2MachineLearningAnalysis {

    private static final Logger LOGGER = Logger.getLogger(Deliverable2MachineLearningAnalysis.class.getName());

    public static Pipeline[] generateFilter() {
        ArrayList<Pipeline> result = new ArrayList<>();
        Filter[] featureSelection = new Filter[]{null, Pipeline.attributeSelection()};
        Filter[] sampling = new Filter[]{new Resample(), new SpreadSubsample(), new SMOTE()};
        Classifier[] classifiers = new Classifier[]{new BayesNet(), new RandomForest(), new IBk()};
        for (Filter fs : featureSelection) {
            for (Filter sampl : sampling) {
                for (Classifier classifier : classifiers) {

                    Filter[] filters;
                    if (fs != null) {
                        filters = new Filter[]{new Standardize(), fs, sampl};
                    } else {
                        filters = new Filter[]{new Standardize(), sampl};
                    }
                    Pipeline pipeline = new Pipeline(filters, classifier);
                    result.add(pipeline);
                }
            }
        }
        return result.toArray(new Pipeline[]{});
    }

    /**
     *
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            LOGGER.log(Level.SEVERE, "USAGE: command <data.csv> <output.csv>");
            return;
        }
        String data = args[0];
        String output = args[1];
        Dataset dataset = new Dataset(data);
        ClassifierInfo[] classifierInfos = ClassifierInfo.getAll();
        String[] headers = new String[]{
                "dataset",
                "%training",
                "%Defective in Training",
                "%Defective in testing",
                "classifier",
                "balancing",
                "feature selection",
                "sensitivity",
                "TP", "FP", "TN", "FN", "precision", "recall", "f1", "auc", "kappa"
        };
        CSVWriter csvWriter = new CSVWriter(Paths.get(output).toAbsolutePath().toFile(), headers);
        csvWriter.writeFieldName();
        Arrays.stream(classifierInfos).parallel().forEach(clfInfo -> {
            try {
                Logger.getLogger("isw2").log(Level.INFO, clfInfo.getName());
                Score score = clfInfo.walkingForward(dataset);
                classifierInfoToCSV(score, dataset, clfInfo, csvWriter);
                Logger.getLogger("isw2").log(Level.INFO, String.format("%s %s", clfInfo.getName(), "DONE"));
            } catch (WekaError | IOException e) {
                Logger.getLogger("isw2").log(Level.SEVERE, e.getMessage());
            }
        });
    }

    public static void classifierInfoToCSV(Score score, Dataset dataset, ClassifierInfo classifierInfo, CSVWriter csvWriter) throws IOException {
        String[] row = new String[]{dataset.getData().relationName(),
                Double.toString(score.getTrainingPercent()),
                Double.toString(score.getDefectiveTraining()),
                Double.toString(score.getDefectiveTesting()),
                classifierInfo.getClassifier().toString(),
                classifierInfo.getSampling().toString(),
                classifierInfo.getFeatureSelection().toString(),
                classifierInfo.getSensitiveLearning().toString(),
                Double.toString(score.getTruePositive()),
                Double.toString(score.getFalsePositive()),
                Double.toString(score.getTrueNegative()),
                Double.toString(score.getFalseNegative()),
                Double.toString(score.getPrecision()),
                Double.toString(score.getRecall()),
                Double.toString(score.getF1()),
                Double.toString(score.getAuc()),
                Double.toString(score.getKappa())};
        csvWriter.writeLine(row);
        csvWriter.flush();
    }
}
