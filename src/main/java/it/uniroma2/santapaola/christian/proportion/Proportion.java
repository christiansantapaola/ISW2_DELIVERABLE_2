package it.uniroma2.santapaola.christian.proportion;

import it.uniroma2.santapaola.christian.mining.Version;


public abstract class Proportion {
    protected Double p;

    public abstract void computeProportion(Version release);

    public Integer computeIV(Integer fv, Integer ov) {
        double iv = fv - (fv - ov) * p;
        return Math.round((float) iv);
    }
}