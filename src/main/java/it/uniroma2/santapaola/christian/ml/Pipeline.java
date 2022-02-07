package it.uniroma2.santapaola.christian.ml;

import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.supervised.attribute.AttributeSelection;


/**
 * Pipeline è una classe che si occupa di gestire il processo di classificazione.
 * Gestisce un classificatore più le varie tecniche di preparazione del dataset.
 */
public class Pipeline {
    private MultiFilter multiFilter;
    private FilteredClassifier classifier;

    public Pipeline(Filter[] filters, Classifier classifier) {
        multiFilter = new MultiFilter();
        multiFilter.setFilters(filters);
        this.classifier = new FilteredClassifier();
        this.classifier.setFilter(multiFilter);
        this.classifier.setClassifier(classifier);
    }

    public Classifier getClassifier() {
        return classifier;
    }


    public void train(Instances trainingSet) throws WekaError {
        try {
            classifier.buildClassifier(trainingSet);
        } catch (Exception e) {
            throw new WekaError(e.getMessage());
        }
    }

    public Evaluation score(Instances trainingSet, Instances testingSet) throws WekaError {
        try {
            Evaluation evaluation = new Evaluation(trainingSet);
            evaluation.evaluateModel(classifier, testingSet);
            return evaluation;
        } catch (Exception e) {
            throw new WekaError(e.getMessage());
        }
    }

    public Evaluation trainAndScore(Instances trainingSet, Instances testingSet) throws WekaError {
        this.train(trainingSet);
        return this.score(trainingSet, testingSet);
    }

    public static Instances filter(Filter filter, Instances data) throws WekaError {
        try {
            filter.setInputFormat(data);
            Instances processed = Filter.useFilter(data, filter);
            processed.setClassIndex(data.classIndex());
            return processed;
        } catch (Exception e) {
            throw new WekaError(e.getMessage());
        }
    }

    public static Instances filter(Filter filter, Instances data, String[] options) throws WekaError {
        try {
            filter.setOptions(options);
            return filter(filter, data);
        } catch (Exception e) {
            throw new WekaError(e.getMessage());
        }
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
