package util;

import org.example.util.SalaryCalcJs;
import org.example.util.SalaryFunction;
import org.junit.jupiter.api.Test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class SalaryCalculator {

    ScriptEngine engine;

    public SalaryCalculator() {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        this.engine = scriptEngineManager.getEngineByName("JavaScript");
    }

    @Test
    public void test() throws ScriptException {
        String code = "function test(input){"
            + "return input + 1e2"
            + "}";
        engine.eval(code);
        Invocable invocable = (Invocable) engine;
        JsFunction func = invocable.getInterface(JsFunction.class);
        System.out.println(func.test(10));
    }

    @Test
    public void getJsCode() {
        SalaryCalcJs salaryCalcJs = new SalaryCalcJs();
        String js = salaryCalcJs.getSalaryCalcJs();
        System.out.println();
    }

    @Test
    public void getJsFunc() {
        org.example.util.SalaryCalculator salaryCalculator = new org.example.util.SalaryCalculator();
        try {
            SalaryFunction func = salaryCalculator.getFunc();
            System.out.println(func.calc(String.valueOf(100), "administrative_part_time_teacher"));
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }

    }
}
