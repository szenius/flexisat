package conflict_analysers;

public class SingleUIPConflictAnalyser extends UIPConflictAnalyser {
    @Override
    int getUIPCriteria() {
        return 1;
    }
}
