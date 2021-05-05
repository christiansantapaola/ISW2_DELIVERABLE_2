package it.uniroma2.santapaola.christian.GitSubSystem;

import it.uniroma2.santapaola.christian.GitSubSystem.Exception.GitHandlerException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface Git {

    List<Commit> log() throws GitHandlerException;
    List<Commit> log(Optional<String> tagA, Optional<String> tagB) throws GitHandlerException;
    List<Commit> log(String path) throws GitHandlerException;
    List<Commit> grep(String pattern) throws GitHandlerException;
    List<DiffStat> diff(Commit c1, Commit c2) throws GitHandlerException;
    List<DiffStat> diff(String c1, String c2) throws GitHandlerException;
    List<Tag> getAllTags() throws GitHandlerException;
    Optional<Commit> show(String tag) throws GitHandlerException;
    Set<String> getSnapshot(Commit commit) throws GitHandlerException;
    Set<String> getSnapshot(Commit commit, String pattern) throws GitHandlerException;
    Set<String> getChangedFiles(Commit commit) throws GitHandlerException;
    Set<String> getChangedFiles(Commit commit, String pattern) throws GitHandlerException;
    long getNoChangedFiles(Commit commit) throws GitHandlerException;

    }
