package Proportion;

import JiraSubSystem.Release;

public class ColdStartProportion extends Proportion {

    @Override
    public void computeProportion() {
        this.proportion = 5.;
    }

    @Override
    public void computeProportion(Release release) {
        computeProportion();
    }
}
