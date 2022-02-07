package it.uniroma2.santapaola.christian.proportion;

import it.uniroma2.santapaola.christian.mining.Version;


/**
 * Questa classe implementa il SimpleProportion, ovvero il caso in cui si assume IV=OV.
 */
public class SimpleProportion extends Proportion {

    @Override
    public void computeProportion(Version version) {
        p = 1.;
    }
}
