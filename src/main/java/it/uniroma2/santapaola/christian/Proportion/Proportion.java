package it.uniroma2.santapaola.christian.Proportion;

import it.uniroma2.santapaola.christian.JiraSubSystem.Release;


public abstract class Proportion {
    protected Double proportion;

    public abstract void computeProportion();

    public abstract void computeProportion(Release release);

    public Integer computeIV(Integer FV, Integer OV) {
        double IV = FV - (FV - OV) * proportion;
        return Math.round((float) IV);
    }

}