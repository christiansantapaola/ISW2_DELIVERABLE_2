package it.uniroma2.santapaola.christian.ml;

import it.uniroma2.santapaola.christian.utility.Pair;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

/**
 * Questa classe implementa la tecnica di validazione Walking Forward.
 * Richiede una istanza di una classe Pipeline, la quale si occupa di gestire il training ed il testing dei classificatori.
 * Un istanza della classe Dataset.
 */
public class WalkingForward {
    private Pipeline pipeline;
    private Dataset dataset;
    private String attribute;
    private int maxVersion;

    public WalkingForward(Pipeline pipeline, Dataset dataset) {
        this.pipeline = pipeline;
        this.dataset = dataset;
        this.attribute = "Version";
        maxVersion = (int) getMax(dataset.getData(), attribute);

    }

    private static double getMax(Instances data, String attribute) {
        Attribute attr = data.attribute(attribute);
        double max = data.firstInstance().value(attr);
        for (Instance row: data) {
            if (max < row.value(attr)) {
                max = row.value(attr);
            }
        }
        return max;
    }

    /**
     * get() ritorna una coppia Training Set, Testing set tali che
     * Il training set comprende tutti i dati fino lala
     * @param version, intero che indica l'indice della versione.
     * @return Una coppia Traininig Set, Testing set.
     */
    public Pair<Instances, Instances> get(int version) {
        Instances tr = new Instances(dataset.getData(), 0);
        Instances ts = new Instances(dataset.getData(), 0);
        for (Instance instance: dataset.getData()) {
            if ((int) instance.value(dataset.getData().attribute(attribute)) < version) {
                tr.add(instance);
            } else if ((int) instance.value(dataset.getData().attribute(attribute)) == version) {
                ts.add(instance);
            }
        }
        return new Pair<>(tr, ts);
    }

    private double computeAccuracy(Evaluation evaluation) {
        return ((evaluation.correct() + evaluation.incorrect()) != 0) ? evaluation.correct() / (evaluation.correct() + evaluation.incorrect()) : 0;
    }

    private double computeIncorrect(Evaluation evaluation) {
        return ((evaluation.correct() + evaluation.incorrect()) != 0) ? evaluation.incorrect() / (evaluation.correct() + evaluation.incorrect()) : 0;
    }

    /**
     * evaluate() esegue l'algoritmo Walking forward su tutte le versioni.
     * @return Una lista contentente Il risultato di ogni iterazione.
     * @throws WekaError
     */
    public List<Score> evaluate() throws WekaError {
        int classIndex = dataset.getPositiveClassIndex();
        List<Score> scoreList = new ArrayList<>();
        for (int version = 0; version <= maxVersion; version++) {
            Pair<Instances, Instances> sets = get(version);
            if (sets.getFirst().numInstances() == 0) continue;
            pipeline.train(sets.getFirst());
            Evaluation evaluation = pipeline.score(sets.getFirst(), sets.getSecond());
            Evaluation trEvaluation = pipeline.score(sets.getFirst(), sets.getFirst());
            double accuracy = computeAccuracy(evaluation);
            ConfusionMatrix confusionMatrix = new ConfusionMatrix(evaluation.numTruePositives(classIndex),
                    evaluation.numTrueNegatives(classIndex),
                    evaluation.numFalsePositives(classIndex),
                    evaluation.numFalseNegatives(classIndex));
            MLMetrics metrics = new MLMetrics(accuracy,
                    evaluation.precision(classIndex),
                    evaluation.recall(classIndex),
                    evaluation.fMeasure(classIndex),
                    evaluation.kappa(),
                    evaluation.areaUnderPRC(classIndex));
            Score score = new Score(confusionMatrix,
                    metrics,
                    (double) sets.getFirst().numInstances() / dataset.getData().numInstances(),
                    computeIncorrect(trEvaluation),
                    computeIncorrect(evaluation));
            scoreList.add(score);
        }
        return scoreList;
    }

    /**
     * evaluateMean() esegue l'algoritmo di Walking forward per tutte le versioni è ritorna la media dei risultati.
     * @return La classe Score contenente la media dei risultati del metodo evaluate().
     * @throws WekaError
     */
    public Score evaluateMean() throws WekaError {
        List<Score> scores = this.evaluate();
        return Score.mean(scores);
    }
}
