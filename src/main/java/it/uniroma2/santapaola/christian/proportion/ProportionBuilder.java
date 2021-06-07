package it.uniroma2.santapaola.christian.proportion;

import it.uniroma2.santapaola.christian.mining.Bug;

import java.util.List;

public class ProportionBuilder {

    public enum ProportionType {
        SIMPLE,
        INCREMENT,
    }

    public static ProportionType getSimpleProportion() {
        return ProportionType.SIMPLE;
    }

    public static ProportionType getIncrementProportion() {
        return ProportionType.INCREMENT;
    }

    public static Proportion build(ProportionType type, List<Bug> bugs) {
        switch (type) {
            case SIMPLE:
                return new SimpleProportion();
            case INCREMENT:
                return new IncrementProportion(bugs);
            default:
                throw new IllegalStateException();
        }
    }
}
