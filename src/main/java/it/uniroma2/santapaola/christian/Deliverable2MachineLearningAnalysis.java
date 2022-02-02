package it.uniroma2.santapaola.christian;

import it.uniroma2.santapaola.christian.ml.ClassifierInfo;
import it.uniroma2.santapaola.christian.ml.Dataset;
import it.uniroma2.santapaola.christian.ml.Pipeline;
import it.uniroma2.santapaola.christian.ml.Score;
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

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Deliverable2MachineLearningAnalysis {

    private static final Logger LOGGER = Logger.getLogger(Deliverable2MachineLearningAnalysis.class.getName());

    private String[] headers = new String[]{"dataset",
            "%training",
            "%Defective in Training",
            "%Defective in testing",
            "classifier",
            "balancing",
            "feature selection",
            "sensitivity",
            "TP", "FP", "TN", "FN", "precision", "recall", "auc", "kappa"};

    private CSVWriter csvWriter;

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
                "TP", "FP", "TN", "FN", "precision", "recall", "auc", "kappa"
        };

//        CSVWriter csvWriter = new CSVWriter(new File(output), headers);
//        csvWriter.writeFieldName();
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        for (ClassifierInfo clfInfo : classifierInfos) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        CSVWriter csvWriter = new CSVWriter(Paths.get(output, clfInfo.getName() + ".csv").toAbsolutePath().toFile(), headers);
                        csvWriter.writeFieldName();
                        classifierInfoToCSV(dataset, clfInfo, csvWriter);
                    } catch (Exception e) {
                        throw new RuntimeException();
                    }
                }
            };
            threadPoolExecutor.submit(runnable);
        }
    }

    public static void classifierInfoToCSV(Dataset dataset, ClassifierInfo classifierInfo, CSVWriter csvWriter) throws Exception {
        List<Score> scores = classifierInfo.walkingForward(dataset);
        scores.add(Score.mean(scores));
        for (Score score : scores) {
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
                    Double.toString(score.getAuc()),
                    Double.toString(score.getKappa())};
            csvWriter.writeLine(row);
        }
        csvWriter.flush();
    }
}
