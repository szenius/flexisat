public class CDCL {
    public CDCL() {
        
    }
}

// CONFLICT = 1

// def conflictAnalysis(form, v):
//     return 1

// def pickBranchingVariable(form, v):
//     return 1, 1


// def allVariablesAssigned(form, v):
//     return True
// def unitPropagation(form, v):
//     return 1


// def backTrack(form, v, beta):
//     return (1,2,3)


// def CDCL(form, v):
//     if (unitPropagation(form, v) == CONFLICT):
//         return False
//     dl = 0
//     while (not allVariablesAssigned(form, v)):
//         var, v = pickBranchingVariable(form, v)
//         dl = dl + 1
//         # v = v union {x,v}
//         if (unitPropagation(form, v) == CONFLICT):
//             beta = conflictAnalysis(form, v)
//             if (beta < 0):
//                 return False
//             else:
//                 form, v, beta = backTrack(form, v, beta)
//                 dl = beta

