package it.uniroma2.santapaola.christian.ml;

import java.util.Arrays;
import java.util.List;

public class Score {
    private double truePositive;
    private double trueNegative;
    private double falsePositive;
    private double falseNegative;
    private double accuracy;
    private double precision;
    private double recall;
    private double f1;
    private double kappa;
    private double trainingPercent;
    private double defectiveTraining;
    private double defectiveTesting;
    private double auc;



    public Score(double truePositive, double trueNegative, double falsePositive, double falseNegative, double accuracy, double precision, double recall, double f1, double kappa, double trainingPercent, double defectiveTraining, double defectiveTesting, double auc) {
        this.truePositive = truePositive;
        this.trueNegative = trueNegative;
        this.falsePositive = falsePositive;
        this.falseNegative = falseNegative;
        this.accuracy = accuracy;
        this.precision = precision;
        this.recall = recall;
        this.f1 = f1;
        this.kappa = kappa;
        this.trainingPercent = trainingPercent;
        this.defectiveTraining = defectiveTraining;
        this.defectiveTesting = defectiveTesting;
        this.auc = auc;
    }

    public void add(Score score) {
        this.truePositive += score.truePositive;
        this.trueNegative += score.trueNegative;
        this.falsePositive += score.falsePositive;
        this.falseNegative += score.falseNegative;
        this.accuracy += score.accuracy;
        this.precision += score.precision;
        this.recall += score.recall;
        this.f1 += score.f1;
        this.kappa += score.kappa;
        this.trainingPercent += score.trainingPercent;
        this.defectiveTraining += score.defectiveTraining;
        this.defectiveTesting += score.defectiveTesting;
    }

    public void divide(double div) {
        this.truePositive = truePositive / div;
        this.trueNegative = trueNegative / div;
        this.falsePositive = falsePositive / div;
        this.falseNegative = falseNegative / div;
        this.accuracy = accuracy / div;
        this.precision = precision / div;
        this.recall = recall / div;
        this.f1 = f1 / div;
        this.kappa = kappa / div;
        this.trainingPercent = trainingPercent / div;
        this.defectiveTraining = defectiveTraining / div;
        this.defectiveTesting = defectiveTesting / div;

    }

    public double getTruePositive() {
        return truePositive;
    }

    public double getTrueNegative() {
        return trueNegative;
    }

    public double getFalsePositive() {
        return falsePositive;
    }

    public double getFalseNegative() {
        return falseNegative;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getPrecision() {
        return precision;
    }

    public double getRecall() {
        return recall;
    }

    public double getF1() {
        return f1;
    }

    public double getKappa() {
        return kappa;
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
        return auc;
    }

    public static Score mean(List<Score> scoreList) {
        Score mean = new Score(0, 0,0,0,0,0,0,0,0,0,0,0,0);
        for (Score score : scoreList) {
            mean.add(score);
        }
        mean.divide(scoreList.size());
        return mean;
    }

    @Override
    public String toString() {
        return "Score{" +
                "truePositive=" + truePositive +
                ", trueNegative=" + trueNegative +
                ", falsePositive=" + falsePositive +
                ", falseNegative=" + falseNegative +
                ", accuracy=" + accuracy +
                ", precision=" + precision +
                ", recall=" + recall +
                ", f1=" + f1 +
                ", kappa=" + kappa +
                ", trainingPercent=" + trainingPercent +
                ", defectiveTraining=" + defectiveTraining +
                ", defectiveTesting=" + defectiveTesting +
                '}';
    }
}
