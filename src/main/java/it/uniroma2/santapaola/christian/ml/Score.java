package it.uniroma2.santapaola.christian.ml;

import java.util.List;

/**
 * La classe Score tiene traccia delle metriche di valutazione di un classificatore,
 * Tiene traccia della matrice di confusione di varie metriche di valutazioni, quali F1, Auc, etc..
 * Infine tiene traccia di dati riguardanti il training e il testing.
 */
public class Score {
    private ConfusionMatrix confusionMatrix;
    private MLMetrics metrics;
    private double trainingPercent;
    private double defectiveTraining;
    private double defectiveTesting;



    public Score(ConfusionMatrix confusionMatrix, MLMetrics metrics, double trainingPercent, double defectiveTraining, double defectiveTesting) {
        this.confusionMatrix = confusionMatrix;
        this.metrics = metrics;
        this.trainingPercent = trainingPercent;
        this.defectiveTraining = defectiveTraining;
        this.defectiveTesting = defectiveTesting;
    }

    public void add(Score score) {
        this.confusionMatrix.add(score.confusionMatrix);
        this.metrics.add(score.metrics);
        this.trainingPercent += score.trainingPercent;
        this.defectiveTraining += score.defectiveTraining;
        this.defectiveTesting += score.defectiveTesting;
    }

    public void divide(double div) {
        if (div == 0) return;
        this.confusionMatrix.div(div);
        this.metrics.div(div);
        this.trainingPercent = trainingPercent / div;
        this.defectiveTraining = defectiveTraining / div;
        this.defectiveTesting = defectiveTesting / div;

    }

    public double getTruePositive() {
        return confusionMatrix.getTruePositive();
    }

    public double getTrueNegative() {
        return confusionMatrix.getTrueNegative();
    }

    public double getFalsePositive() {
        return confusionMatrix.getFalsePositive();
    }

    public double getFalseNegative() {
        return confusionMatrix.getFalseNegative();
    }

    public double getAccuracy() {
        return metrics.getAccuracy();
    }

    public double getPrecision() {
        return metrics.getPrecision();
    }

    public double getRecall() {
        return metrics.getRecall();
    }

    public double getF1() {
        return metrics.getF1();
    }

    public double getKappa() {
        return metrics.getKappa();
    }

    public double getTrainingPercent() {
        return trainingPercent;
    }

    public double getDefectiveTraining() {
        return defectiveTraining;
    }

    public double getDefectiveTesting() {
        return defectiveTesting;
    }

    public double getAuc() {
        return metrics.getAuc();
    }

    public static Score mean(List<Score> scoreList) {
        Score mean = new Score(new ConfusionMatrix(), new MLMetrics(),0,0, 0);
        for (Score score : scoreList) {
            mean.add(score);
        }
        mean.divide(scoreList.size());
        return mean;
    }

    @Override
    public String toString() {
        return "Score{" +
                "truePositive=" + confusionMatrix.getTruePositive() +
                ", trueNegative=" + confusionMatrix.getTrueNegative() +
                ", falsePositive=" + confusionMatrix.getFalsePositive() +
                ", falseNegative=" + confusionMatrix.getFalseNegative() +
                ", accuracy=" + metrics.getAccuracy() +
                ", precision=" + metrics.getPrecision() +
                ", recall=" + metrics.getRecall() +
                ", f1=" + metrics.getF1() +
                ", kappa=" + metrics.getKappa() +
                ", auc=" + metrics.getAuc() +
                ", trainingPercent=" + trainingPercent +
                ", defectiveTraining=" + defectiveTraining +
                ", defectiveTesting=" + defectiveTesting +
                '}';
    }
}
