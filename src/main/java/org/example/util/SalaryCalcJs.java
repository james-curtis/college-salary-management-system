package org.example.util;

import java.io.IOException;
import java.util.stream.Collectors;

public class SalaryCalcJs {

    private ExcelOperator operator;

    public SalaryCalcJs() {
        String path = this.getClass().getClassLoader().getResource("").getPath();
        this.operator = new ExcelOperator(path + "./database/rule.csv");
        try {
            this.operator.initReadFile();
        } catch (IOException e) {
            System.err.println("解析csv文件失败");
        }
    }

    public String getSalaryCalcJs() {
        int ruleNameIdx = this.operator.getHeaders().indexOf("rule_name");
        int ruleDefineIdx = this.operator.getHeaders().indexOf("rule_define");
        String ruleDefine = this.operator.getRawContents().stream()
            .map(x -> x.get(ruleNameIdx) + " = function() {return (" + x.get(ruleDefineIdx) + ")}\n")
            .collect(Collectors.joining());
        return "function calc(last_semester_workload, target) {\n" + ruleDefine +
            "    return eval(target+'()');\n" +
            "}";
    }
}
