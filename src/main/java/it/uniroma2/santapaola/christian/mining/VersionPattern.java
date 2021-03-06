package it.uniroma2.santapaola.christian.mining;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Questa classe si occupa di gestire il pattern per riconoscere una versione.
 * Il pattern è basato su regex.
 */
public class VersionPattern {
    private Pattern pattern;

    public VersionPattern(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public boolean matches(String tag) {
        return pattern.matcher(tag).matches();
    }

    public Optional<String> getName(String tag) {
        Matcher matcher = pattern.matcher(tag);
        if (!matcher.matches()) {
            return Optional.empty();
        }
        return Optional.of(matcher.group("name"));
    }
}
