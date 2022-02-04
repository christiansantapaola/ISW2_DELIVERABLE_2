package it.uniroma2.santapaola.christian.ml;

public class ConfusionMatrix {
    private double truePositive;
    private double trueNegative;
    private double falsePositive;
    private double falseNegative;

    public ConfusionMatrix(double truePositive, double trueNegative, double falsePositive, double falseNegative) {
        this.truePositive = truePositive;
        this.trueNegative = trueNegative;
        this.falsePositive = falsePositive;
        this.falseNegative = falseNegative;
    }

    public ConfusionMatrix() {
        this.truePositive = 0;
        this.trueNegative = 0;
        this.falsePositive = 0;
        this.falseNegative = 0;

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

    public void add(ConfusionMatrix confusionMatrix) {
        if (confusionMatrix == null) {
            return;
        }
        this.truePositive = this.truePositive + confusionMatrix.getTruePositive();
        this.trueNegative = this.trueNegative + confusionMatrix.getTrueNegative();
        this.falsePositive = this.falsePositive + confusionMatrix.getFalsePositive();
        this.falseNegative = this.falseNegative + confusionMatrix.getFalseNegative();
    }

    public void div(double div) {
        if (div == 0) return;
        this.truePositive = this.truePositive / div ;
        this.trueNegative = this.trueNegative / div ;
        this.falsePositive = this.falsePositive / div ;
        this.falseNegative = this.falseNegative / div;

    }
}
