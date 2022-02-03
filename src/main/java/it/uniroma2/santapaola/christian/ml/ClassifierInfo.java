package it.uniroma2.santapaola.christian.ml;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.trees.RandomForest;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;
import weka.filters.unsupervised.instance.Resample;

import java.util.ArrayList;
import java.util.List;

public class ClassifierInfo {
    private ClassifierType classifier;
    private Sampling sampling;
    private Learning learning;
    private FeatureSelection featureSelection;
    private Pipeline pipeline;
    private static String errorMessage = "[ERROR] '%s' is not implemented";

    public enum FeatureSelection {
        NOTHING,
        BEST_FIRST,
    }

    public AttributeSelection getFeatureSelection(FeatureSelection featureSelection) {
        if (featureSelection == FeatureSelection.NOTHING) {
            return null;
        } else {
            return Pipeline.attributeSelection();
        }
    }

    public enum Sampling {
        OVERSAMPLING,
        UNDERSAMPLING,
        SMOTE
    }


    public Filter getSampling(Sampling sampling) {
        switch (sampling) {
            case OVERSAMPLING:
                return new Resample();
            case UNDERSAMPLING:
                return new SpreadSubsample();
            case SMOTE:
                return new SMOTE();
            default:
                throw new IllegalStateException(String.format(errorMessage, sampling.toString()));
        }
    }

    public enum Learning {
        NOTHING,
        SENSITIVE_LEARNING,
        THRESHOLD
    }

    public CostSensitiveClassifier sensitiveThreshold(Learning learning) {
        switch (learning) {
            case NOTHING:
                return null;
            case SENSITIVE_LEARNING: {
                double cpf = 1.;
                double cnp = 10.;
                return Pipeline.createCostSensitiveClassifier(cpf, cnp);
            }
            case THRESHOLD: {
                /* Threshold is CFP / (CFP + CNP). here is 0.5*/
                double cfp = 1.;
                double cnp = 1.;
                return Pipeline.createCostSensitiveClassifier(cfp, cnp);
            }
            default:
                throw new IllegalStateException(String.format(errorMessage, learning.toString()));
        }
    }

    public enum ClassifierType {
        NAIVEBAYES,
        RANDOMFOREST,
        IBK,
    }

    public Classifier getClassifier(ClassifierType classifierType) {
        switch (classifierType) {
            case NAIVEBAYES:
                return new NaiveBayes();
            case RANDOMFOREST:
                return new RandomForest();
            case IBK:
                return new IBk();
            default:
                throw new IllegalStateException(String.format(errorMessage, sampling.toString()));
        }
    }

    public ClassifierInfo(FeatureSelection featureSelection, Sampling sampling, Learning learning, ClassifierType classifierType) {
        ArrayList<Filter> filters = new ArrayList<>();
        AttributeSelection attributeSelection = getFeatureSelection(featureSelection);
        if (attributeSelection != null) {
            filters.add(attributeSelection);
        }
        filters.add(getSampling(sampling));
        this.featureSelection = featureSelection;
        this.sampling = sampling;
        this.learning = learning;
        this.classifier = classifierType;
        Classifier clf = getClassifier(classifierType);
        CostSensitiveClassifier sensitiveClassifier = sensitiveThreshold(learning);
        if (sensitiveClassifier != null) {
            sensitiveClassifier.setClassifier(clf);
            pipeline = new Pipeline(filters.toArray(new Filter[0]), sensitiveClassifier);
        } else {
            pipeline = new Pipeline(filters.toArray(new Filter[0]), clf);
        }
    }

    public List<Score> walkingForward(Dataset dataset) throws WekaError {
        WalkingForward walkingForward = new WalkingForward(pipeline, dataset);
        return walkingForward.evaluate();
    }

    public ClassifierType getClassifier() {
        return classifier;
    }

    public Sampling getSampling() {
        return sampling;
    }

    public ClassifierInfo.Learning getSensitiveLearning() {
        return learning;
    }

    public FeatureSelection getFeatureSelection() {
        return featureSelection;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public static ClassifierInfo[] getAll() {
        ArrayList<ClassifierInfo> result = new ArrayList<>();
        for (Sampling sampling : Sampling.values()) {
            for (FeatureSelection featureSelection : FeatureSelection.values()) {
                for (Learning learning : Learning.values()) {
                    for (ClassifierType classifierType : ClassifierType.values()) {
                        ClassifierInfo classifierInfo = new ClassifierInfo(featureSelection, sampling, learning, classifierType);
                        result.add(classifierInfo);
                    }
                }
            }
        }
        return result.toArray(new ClassifierInfo[0]);
    }

    public String getName() {
        String sep = "_";
        return featureSelection.name() + sep + sampling.name() + sep + learning.name() + sep + classifier.name();
    }
}
