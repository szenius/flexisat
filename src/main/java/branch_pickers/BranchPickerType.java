package branch_pickers;

public enum BranchPickerType {
    RANDOM("random"),
    SEQ("seq");

    private String type;

    BranchPickerType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
