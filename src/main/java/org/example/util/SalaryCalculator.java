package org.example.util;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;

public class SalaryCalculator {

    private ScriptEngine engine;
    private SalaryCalcJs salaryCalcJs;

    public SalaryCalculator() {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        this.engine = scriptEngineManager.getEngineByName("JavaScript");
        this.salaryCalcJs = new SalaryCalcJs();
    }

    public void setSalaryCalcJs(SalaryCalcJs salaryCalcJs) {
        this.salaryCalcJs = salaryCalcJs;
    }

    public SalaryCalcJs getSalaryCalcJs() {
        return salaryCalcJs;
    }

    public SalaryFunction getFunc() throws ScriptException {
        engine.eval(this.salaryCalcJs.getSalaryCalcJs());
        Invocable invocable = (Invocable) engine;
        return invocable.getInterface(SalaryFunction.class);
    }
}
