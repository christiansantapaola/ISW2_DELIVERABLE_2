package it.uniroma2.santapaola.christian.Proportion;

import it.uniroma2.santapaola.christian.JiraSubSystem.Release;
import it.uniroma2.santapaola.christian.Mining.Version;


public abstract class Proportion {
    protected Double proportion;

    public abstract void computeProportion(Version release);

    public Integer computeIV(Integer FV, Integer OV) {
        double IV = FV - (FV - OV) * proportion;
        return Math.round((float) IV);
    }

}