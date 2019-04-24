package conflict_analysers;

public enum ConflictAnalyserType {
    NO_UIP("no_uip"),
    SINGLE_UIP("single_uip"),
    DIRECT("direct"),
    ROOTS("roots");

    private String type;

    ConflictAnalyserType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
