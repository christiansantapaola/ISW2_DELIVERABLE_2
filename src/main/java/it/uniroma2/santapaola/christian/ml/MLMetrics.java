package it.uniroma2.santapaola.christian.ml;

public class MLMetrics {
    private double accuracy;
    private double precision;
    private double recall;
    private double f1;
    private double kappa;
    private double auc;


    public MLMetrics(double accuracy, double precision, double recall, double f1, double kappa, double auc) {
        this.accuracy = accuracy;
        this.precision = precision;
        this.recall = recall;
        this.f1 = f1;
        this.kappa = kappa;
        this.auc = auc;
    }

    public MLMetrics() {
        this.accuracy = 0;
        this.precision = 0;
        this.recall = 0;
        this.f1 = 0;
        this.kappa = 0;
        this.auc = 0;
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

    public double getAuc() {
        return auc;
    }

    public void add(MLMetrics metrics) {
        this.accuracy += metrics.accuracy;
        this.precision += metrics.precision;
        this.recall += metrics.recall;
        this.f1 += metrics.f1;
        this.kappa += metrics.kappa;
        this.auc += metrics.auc;
    }

    public void div(double div) {
        this.accuracy = accuracy / div;
        this.precision = precision / div;
        this.recall = recall / div;
        this.f1 = f1 / div;
        this.kappa = kappa / div;
        this.auc = auc / div;
    }
}
