package conflict_analysers;

public enum ConflictAnalyserType {
    BASIC("basic"),
    UIP("uip");

    private String type;

    ConflictAnalyserType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
