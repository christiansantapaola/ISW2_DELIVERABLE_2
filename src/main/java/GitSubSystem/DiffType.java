package GitSubSystem;

import org.eclipse.jgit.diff.DiffEntry;

import java.security.InvalidParameterException;

public enum DiffType {
    ADD,
    MODIFY,
    DELETE,
    COPY,
    RENAME,
    ;
}