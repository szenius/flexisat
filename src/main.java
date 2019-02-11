import parser
import algorithms.CDCL.cdcl

# note: form variable should be in DIMACS CNF format.
def main():
    form = parser.readFile
    form, v = parser.readParameters()
    algorithms.CDCL.cdcl.CDCL(form, v)

if __name__ == "__main__":
    main()
