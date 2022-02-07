package it.uniroma2.santapaola.christian.mining;

import it.uniroma2.santapaola.christian.git.*;
import it.uniroma2.santapaola.christian.git.exception.GitHandlerException;

import java.util.*;
import java.util.regex.Pattern;


/**
 * La classe ProjectState si occupa di tenere traccia di tutte le informazioni riguardanti un progetto software.
 * Si occupa di tenere traccia delle versioni del progetto, del suo git repository.
 */
public class ProjectState {
    private HashMap<String, ClassState> state;
    private Git git;
    private Timeline projectTimeline;
    private String projectName;
    private int version;
    private long numReleaseToProcess;
    private Version curr;
    private Optional<Version> next;
    private static final Pattern isJavaClass = Pattern.compile("^.*\\.java$");
    private static final CommitComparator cc = new CommitComparator();

    /**
     *
     * @param projectName: il nome del progetto.
     * @param git: Classe git legata al repository del progetto.
     * @param projectTimeline: Classe legata alla timeline jira del progetto, tiene traccia delle versioni e dei ticket.
     * @throws GitHandlerException
     * La classe mette il suo stato alla prima versione disponibile.
     * Setta il numero delle versioni da processare al 50% del numero di versioni totali.
     * infine per ogni classe del progetto presente alla versione corrente setta le informazioni richieste nel HashMap state
     * Mettendo come chiave il nome della classe e come valore le informazioni calcolate.
     * Le informazioni vengono calcolate usando il comando diff di git.
     */
    public ProjectState(String projectName, Git git, Timeline projectTimeline) throws GitHandlerException {
        state = new HashMap<>();
        this.projectName = projectName;
        this.git = git;
        this.projectTimeline = projectTimeline;
        this.version = 0;
        numReleaseToProcess = projectTimeline.getVersionTimeline().getNumVersion() / 2;
        curr = projectTimeline.getVersionTimeline().getFirst();
        next = projectTimeline.getVersionTimeline().getNext(curr);
        List<DiffStat> diffs = git.diff(curr.getTag().getId(), next.get().getTag().getId());
        for (DiffStat diff : diffs) {
            if (!isJavaClass(diff.getOldPath())) continue;
            updateClassState(diff, "", 0);
        }
        curr = next.get();
        next = projectTimeline.getVersionTimeline().getNext(curr);
    }

    /** isJavaClass controlla se una file è una classe java verificando il suo path con una regex.
     * */
    public static boolean isJavaClass(String path) {
        return isJavaClass.matcher(path).matches();
    }

    /**
     * ritorna le classi di cui si hanno informazioni salvate.
     */
    public Set<String> keySet() {
        return state.keySet();
    }

    /**
     * ritorna le informazioni di una classe dato il suo path.
     * @param path
     * @return
     */
    public ClassState getState(String path) {
        return state.get(path);
    }

    /**
     * Aggiorna le informazioni del progetto a quelle della prossima versione, se presente.
     * next() per aggiornare lo stato del progetto esegue le seguenti operazioni.
     * 1. esegue un reset dello stato precedente.
     * 2. aumenta l'indice di versione di 1
     * 3. prende tutti i commit avvenuti tra le versione corrente e la prossima versione.
     * per ogni coppia di commit consecutivi viene calcolata la loro diff.
     * 4. con le informazioni ottenute dalla diff si aggiorna appropriatamente le informazioni riguardanti la classe.
     * 5. si aggiornano i dati riguardanti la buggyness della classe.
     * 6. vengono aggiornate le informazioni riguardanti il LOC
     * 7. infine si cerca la prossima versione, se la versione prossimo non è effettivamente la prossima
     * (ad esempio abbiamo 1.3, 2, 1.3.1, 2.1) allora queste versioni cattive vengono skippate.
     * @return boolean
     * @throws GitHandlerException
     */
    public boolean next() throws GitHandlerException {
        if (!next.isPresent()) return false;
        if (next.get() == projectTimeline.getVersionTimeline().getLast()) return false;
        resetState();
        version++;
        List<Commit> commits = git.log(Optional.of(curr.getTag().getId()), Optional.of(next.get().getTag().getId()));
        commits.sort(cc);
        for (int i = 0; i < commits.size() - 1; i++) {
            List<DiffStat> diffs = git.diff(commits.get(i), commits.get(i+1));
            for (DiffStat diff : diffs) {
                if (!isJavaClass(diff.getOldPath())) continue;
                updateClassState(diff, commits.get(i+1).getAuthor(), diffs.stream().filter(df -> isJavaClass(df.getOldPath())).count());
            }
        }
        Set<String> buggySet = projectTimeline.getBuggyClass(next.get());
        for (Map.Entry<String, ClassState> entry : state.entrySet()) {
            boolean buggy = buggySet.contains(entry.getKey());
            long noFix = projectTimeline.getNoBugFixed(entry.getKey(), curr, next.get());
            entry.getValue().setBuggy(buggy);
            entry.getValue().setNoFix(noFix);
        }
        updateLoc(commits.get(commits.size() - 1));
        clean();
        curr = next.get();
        next = projectTimeline.getVersionTimeline().getNext(curr);
        // skip bad version.
        while (next.isPresent()) {
            if (next.get().getRelease().getName().compareTo(curr.getRelease().getName()) < 0) {
                next = projectTimeline.getVersionTimeline().getNext(next.get());
            } else {
                break;
            }
        }
        return true;
    }

    /**
     *
     * @param diffStat: informazioni sul cambiamento di un file.
     * @param author: chi ha effettuato il cambiamento
     * @param noChangedFile: numero di file cambiati in questa diff.
     * Questa classe aggiorna lo stato di una classe con le informazioni recuparete da un operazione di diff.
     */
    private void updateClassState(DiffStat diffStat, String author, long noChangedFile) {
        ClassState classState = state.get(diffStat.getOldPath());
        if (classState == null) {
            classState = new ClassState(projectName, diffStat.getNewPath());
            state.put(diffStat.getNewPath(), classState);
        }
        if (diffStat.getDiffType() == DiffType.COPY || diffStat.getDiffType() == DiffType.RENAME) {
            state.remove(diffStat.getOldPath());
            state.put(diffStat.getNewPath(), classState);
            return;
        }
        classState.updateLoc(diffStat.getLocAdded(), diffStat.getLocDeleted());
        classState.updateChgFileSet(noChangedFile);
        classState.addAuthor(author);
        classState.updateRevision();
    }

    /**
     * Resetta lo stato di ogni entry all'interno di classState.
     */
    public void resetState() {
        for (Map.Entry<String, ClassState> entry : state.entrySet()) {
            entry.getValue().reset();
        }
    }

    /**
     * Dato un commit, calcola il loc dei file presenti nel progetto a quel commit.
     * L'aggiornamento viene eseguito facendo una diff tra il commit dato ed il default empty tree di git.
     * @param commit
     * @throws GitHandlerException
     */
    public void updateLoc(Commit commit) throws GitHandlerException {
        List<DiffStat> diffs = git.diff(GitConstants.EMPTY_TREE_ID, commit.getName());
        for (DiffStat diff : diffs) {
            ClassState classState = state.get(diff.getNewPath());
            if (classState == null) {
                continue;
            }
            classState.setLoc(diff.getAddedLoc());
        }
    }

    /**
     * clean() pulisce le classi che sono state cancellate nella versione corrente dallo stato.
     */
    void clean() {
        List<String> toDelete = new ArrayList<>();
        for (Map.Entry<String, ClassState> entry : state.entrySet()) {
            if (entry.getValue().getLoc() <= 0) {
                toDelete.add(entry.getKey());
            }
        }
        for (String key : toDelete) {
            state.remove(key);
        }

    }

    public int getVersion() {
        return version;
    }

    public long getNumReleaseToProcess() {
        return numReleaseToProcess;
    }
}
