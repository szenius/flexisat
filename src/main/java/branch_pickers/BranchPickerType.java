package branch_pickers;

public enum BranchPickerType {
    RANDOM("random"),
    SEQ("seq"),
    TWO_CLAUSE("two_clause");

    private String type;

    BranchPickerType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
