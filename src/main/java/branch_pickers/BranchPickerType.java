package branch_pickers;

public enum BranchPickerType {
    SEQUENTIAL("seq");

    private String type;

    BranchPickerType(String type) {
        this.type = type;
    }
}
