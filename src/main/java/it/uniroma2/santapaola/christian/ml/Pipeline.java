package it.uniroma2.santapaola.christian.ml;

import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.supervised.instance.*;
import weka.filters.supervised.attribute.*;
import weka.filters.unsupervised.attribute.Standardize;
import weka.classifiers.meta.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Pipeline {
    private String classifierName;
    private String sampling;
    private String featureSelection;
    private MultiFilter multiFilter;
    private FilteredClassifier classifier;

    public Pipeline(Filter[] filters, Classifier classifier) {
        classifierName = classifier.getClass().getTypeName();
        multiFilter = new MultiFilter();
        multiFilter.setFilters(filters);
        this.classifier = new FilteredClassifier();
        this.classifier.setFilter(multiFilter);
        this.classifier.setClassifier(classifier);
    }

    public Classifier getClassifier() {
        return classifier;
    }


    public void train(Instances trainingSet) throws Exception {
        classifier.buildClassifier(trainingSet);
    }

    public Evaluation score(Instances trainingSet, Instances testingSet) throws Exception {
        Evaluation evaluation = new Evaluation(trainingSet);
        evaluation.evaluateModel(classifier, testingSet);
        return evaluation;
    }

    public Evaluation trainAndScore(Instances trainingSet, Instances testingSet) throws Exception {
        this.train(trainingSet);
        return this.score(trainingSet, testingSet);
    }

    public static Instances filter(Filter filter, Instances data) throws Exception {
        filter.setInputFormat(data);
        Instances processed = Filter.useFilter(data, filter);
        processed.setClassIndex(data.classIndex());
        return processed;
    }

    public static Instances filter(Filter filter, Instances data, String[] options) throws Exception {
        filter.setOptions(options);
        return filter(filter, data);
    }

    public static AttributeSelection attributeSelection() {
        final AttributeSelection filter = new AttributeSelection();
        final CfsSubsetEval evaluator = new CfsSubsetEval();
        evaluator.setMissingSeparate(true);
        // Assign evaluator to filter
        filter.setEvaluator(evaluator);
        // Search strategy: best first (default values)
        final BestFirst search = new BestFirst();
        filter.setSearch(search);
        // Apply filter
        return filter;
    }


    public static CostMatrix createCostMatrix(double weightFalsePositive, double
            weightFalseNegative) {
        CostMatrix costMatrix = new CostMatrix(2);
        costMatrix.setCell(0, 0, 0.0);
        costMatrix.setCell(1, 0, weightFalsePositive);
        costMatrix.setCell(0, 1, weightFalseNegative);
        costMatrix.setCell(1, 1, 0.0);
        return costMatrix;
    }

    public static CostSensitiveClassifier createCostSensitiveClassifier(double weightFalsePositive, double
            weightFalseNegative) {
        CostSensitiveClassifier csc = new CostSensitiveClassifier();
        CostMatrix costMatrix = createCostMatrix(weightFalsePositive, weightFalseNegative);
        csc.setCostMatrix(costMatrix);
        return csc;
    }

 }
