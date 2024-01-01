package datastructs;

import java.util.HashMap;
import java.util.Map;

public class Equation {
    public enum Operator {
        ADD("+"), SUBTRACT("-"), MULTIPLY("*"), DIVIDE("/");
        private final String symbol;

        Operator(String sign) {
            this.symbol = sign;
        }
        public String getSymbol() {
            return this.symbol;
        }
    }

    private static final Map<String, Operator> OPERATORS = new HashMap<>();
    static { //static initializer
        for (Operator op: Operator.values()) {
            OPERATORS.put(op.getSymbol(), op);
        }
    }

    private final String equationVarName;
    private long eqResultValue;
    private boolean resultResolved = false;
    private final String operandVariable1;
    private long operandValue1;
    private boolean op1Resolved = false;
    private final String operandVariable2;
    private long operandValue2;
    private boolean op2Resolved = false;
    private final Operator operator;

    public static Operator createOperatorFromSymbol(String symbol) {
        return OPERATORS.get(symbol);
    }

    public Equation(String eqVarName, String operandVariable1, String operandVariable2, Operator operator) {
        this.equationVarName = eqVarName;
        this.operandVariable1 = operandVariable1;
        this.operandVariable2 = operandVariable2;
        this.operator = operator;
    }

    public String getEquationVarName() {
        return equationVarName;
    }
    public String getOperandVariable1() {
        return operandVariable1;
    }
    public long getOperandValue1() {
        return operandValue1;
    }
    public String getOperandVariable2() {
        return operandVariable2;
    }
    public long getOperandValue2() {
        return operandValue2;
    }
    public boolean isResultValueResolved() {
        return resultResolved;
    }
    public boolean isOperand1Resolved() {
        return op1Resolved;
    }
    public boolean isOperand2Resolved() {
        return op2Resolved;
    }

    public void resolveOperandValue1(long operandValue1) {
        this.operandValue1 = operandValue1;
        this.op1Resolved = true;
    }
    public void resolveOperandValue2(long operandValue2) {
        this.operandValue2 = operandValue2;
        this.op2Resolved = true;
    }
    public void resolveValue(long value) {
        this.eqResultValue = value;
        this.resultResolved = true;
    }
    public long solve() {
        if (this.isOperand1Resolved() && this.isOperand2Resolved()) {
            return solveFor2Operands();
        } else if ((this.isOperand1Resolved() || this.isOperand2Resolved())
                && this.isResultValueResolved()) {
            return solveFor1Operand();
        }

        throw new RuntimeException("Unable to solve this equation: " + this);
    }

    private long solveFor2Operands() {
        long result = 0L;
        switch (this.operator) {
            case ADD -> result = this.operandValue1 + this.operandValue2;
            case SUBTRACT -> result = this.operandValue1 - this.operandValue2;
            case MULTIPLY -> result = this.operandValue1 * this.operandValue2;
            case DIVIDE -> result = this.operandValue1 / this.operandValue2;
        }
        this.resultResolved = true;
        return result;
    }

    private long solveFor1Operand() {
        assert this.op1Resolved || this.op2Resolved;

        switch (this.operator) {
            case ADD -> {
                if (!this.op1Resolved) {
                    this.resolveOperandValue1(this.eqResultValue - this.operandValue2);
                } else if (!this.op2Resolved) {
                    this.resolveOperandValue2(this.eqResultValue - this.operandValue1);
                }
            }
            case SUBTRACT -> {
                if (!this.op1Resolved) {
                    this.resolveOperandValue1(this.operandValue2 + this.eqResultValue);
                } else if (!this.op2Resolved) {
                    this.resolveOperandValue2(this.operandValue1 - this.eqResultValue);
                }
            }
            case MULTIPLY -> {
                if (!this.op1Resolved) {
                    this.resolveOperandValue1(this.eqResultValue / this.operandValue2);
                } else if (!this.op2Resolved) {
                    this.resolveOperandValue2(this.eqResultValue / this.operandValue1);
                }
            }
            case DIVIDE -> {
                if (!this.op1Resolved) {
                    this.resolveOperandValue1(this.operandValue2 * this.eqResultValue);
                } else if (!this.op2Resolved) {
                    this.resolveOperandValue2(this.operandValue1 /this.eqResultValue);
                }
            }
        }
        return this.eqResultValue;
    }

    @Override
    public String toString() {
        String val1 = op1Resolved ? "(" + operandValue1 + ")" : "";
        String val2 = op2Resolved ? "(" + operandValue2 + ")" : "";
        String resultVal = resultResolved ? "(" + eqResultValue + ")" : "";
        return String.format("%s%s = %s%s %s %s%s",
                equationVarName, resultVal, operandVariable1, val1, operator.getSymbol(), operandVariable2, val2);
    }
}
