package branch_pickers;

public enum BranchPickerType {
    RANDOM("random"),
    SEQ("seq"),
    TWO_CLAUSE("two_clause"),
    VSIDS("vsids"),
    CHAFF("chaff"),
    MINISAT("minisat");

    private String type;

    BranchPickerType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
