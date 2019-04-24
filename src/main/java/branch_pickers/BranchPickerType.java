package branch_pickers;

public enum BranchPickerType {
    RANDOM("random"),
    SEQ("seq"),
    TWO_CLAUSE("two_clause"),
    VSIDS("vsids");

    private String type;

    BranchPickerType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
