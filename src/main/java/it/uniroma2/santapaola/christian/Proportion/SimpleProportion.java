package it.uniroma2.santapaola.christian.Proportion;

import it.uniroma2.santapaola.christian.JiraSubSystem.Release;

public class SimpleProportion extends Proportion {
    @Override
    public void computeProportion() {
        this.proportion = 1.0;
    }

    @Override
    public void computeProportion(Release release) {
        computeProportion();
    }
}
