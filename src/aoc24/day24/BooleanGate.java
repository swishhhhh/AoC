package aoc24.day24;

public class BooleanGate {
    public enum Operator {
        AND, OR, XOR
    }

    public static Operator fromSymbol(String symbol) {
        return switch (symbol) {
            case "AND" -> Operator.AND;
            case "OR" -> Operator.OR;
            case "XOR" -> Operator.XOR;
            default -> throw new RuntimeException("Unexpected symbol: " + symbol);
        };
    }

    private String resultVarName;
    private boolean resultValue;
    private boolean resultResolved = false;
    private String operandVariable1;
    private boolean operandValue1;
    private boolean op1Resolved = false;
    private String operandVariable2;
    private boolean operandValue2;
    private boolean op2Resolved = false;
    private final Operator operator;

    public BooleanGate(String resultVarName, String operandVariable1, String operandVariable2, Operator operator) {
        this.resultVarName = resultVarName;
        this.operandVariable1 = operandVariable1;
        this.operandVariable2 = operandVariable2;
        this.operator = operator;
    }

    public String getResultVarName() {
        return resultVarName;
    }

    public String getOperandVariable1() {
        return operandVariable1;
    }

    public String getOperandVariable2() {
        return operandVariable2;
    }

    public boolean isOperandValue1() {
        return operandValue1;
    }

    public boolean isOperandValue2() {
        return operandValue2;
    }

    public void setOperandVariable1(String operandVariable1) {
        this.operandVariable1 = operandVariable1;
    }

    public void setOperandVariable2(String operandVariable2) {
        this.operandVariable2 = operandVariable2;
    }

    public void setResultVarName(String resultVarName) {
        this.resultVarName = resultVarName;
    }

    public Operator getOperator() {
        return operator;
    }

    public boolean isOperand1Resolved() {
        return op1Resolved;
    }
    public boolean isOperand2Resolved() {
        return op2Resolved;
    }

    public void resolveOperandValue1(boolean operandValue1) {
        this.operandValue1 = operandValue1;
        this.op1Resolved = true;
    }

    public void resolveOperandValue2(boolean operandValue2) {
        this.operandValue2 = operandValue2;
        this.op2Resolved = true;
    }

    public boolean solve() {
        if (!this.isOperand1Resolved() || !this.isOperand2Resolved()) {
            throw new IllegalStateException("Can't solve boolean gate with either operand unresolved");
        }

        boolean result;
        switch (this.operator) {
            case AND -> result = this.operandValue1 && this.operandValue2;
            case OR -> result = this.operandValue1 || this.operandValue2;
            case XOR -> result = this.operandValue1 ^ this.operandValue2;
            default -> throw new IllegalStateException("Unexpected value: " + this.operator);
        }
        this.resultResolved = true;
        return result;
    }

    public BooleanGate copy() {
        BooleanGate copy = new BooleanGate(this.resultVarName, this.operandVariable1, this.operandVariable2, this.operator);
        copy.resolveOperandValue1(this.operandValue1);
        copy.resolveOperandValue2(this.operandValue2);
        copy.resultResolved = this.resultResolved;
        copy.resultValue = this.resultValue;
        return copy;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s -> %s", getOperandVariable1(), getOperator(), getOperandVariable2(),
                getResultVarName());
    }
}
