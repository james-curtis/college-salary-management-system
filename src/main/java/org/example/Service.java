package org.example;

import org.example.util.ExcelOperator;
import org.example.util.SalaryFunction;
import org.example.util.ScannerHelper;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Service {

    private String employeeDatabasePath;
    private String ruleDatabasePath;

    private ExcelOperator employeeHandler;
    private ExcelOperator ruleHandler;


    public Service() {

    }

    public String getEmployeeDatabasePath() {
        return employeeDatabasePath;
    }

    public void setEmployeeDatabasePath(String employeeDatabasePath) throws IOException {
        this.employeeDatabasePath = employeeDatabasePath;
        this.employeeHandler = new ExcelOperator(employeeDatabasePath);
        this.employeeHandler.initReadFile();
    }

    public String getRuleDatabasePath() {
        return ruleDatabasePath;
    }

    public void setRuleDatabasePath(String ruleDatabasePath) throws IOException {
        this.ruleDatabasePath = ruleDatabasePath;
        this.ruleHandler = new ExcelOperator(ruleDatabasePath);
        this.ruleHandler.initReadFile();
    }

    public ExcelOperator getEmployeeHandler() {
        return employeeHandler;
    }

    public ExcelOperator getRuleHandler() {
        return ruleHandler;
    }


    public Double getSalary(Integer lastSemesterWorkload, String salaryRule) {
        org.example.util.SalaryCalculator salaryCalculator = new org.example.util.SalaryCalculator();
        try {
            SalaryFunction func = salaryCalculator.getFunc();
            return Double.valueOf(func.calc(String.valueOf(lastSemesterWorkload), salaryRule));
        } catch (ScriptException e) {
            System.err.println("计算薪资出错");
        }
        return Double.valueOf(0);
    }

    public List<List<String>> getSalaryList() {
        List<String> headers = this.employeeHandler.getHeaders();
        List<String> preserveHeaders = new ArrayList<>(
            Arrays.asList(
                "work_no",
                "name",
                "job",
                "last_semester_workload"
            ));
        List<String> newHeaders = new ArrayList<>(preserveHeaders);
        newHeaders.add("salary");

        List<List<String>> ctx = this.employeeHandler.getRawContents();
        List<List<String>> collect = ctx.stream().map(x -> {
            List<String> row = new ArrayList<>();
            for (String it : preserveHeaders) {
                row.add(x.get(headers.indexOf(it)));
            }

            // 添加薪资字段的值
            Double salary = getSalary(
                Integer.valueOf(x.get(headers.indexOf("last_semester_workload"))),
                x.get(headers.indexOf("salary_rule"))
            );
            row.add(salary.toString());
            return row;
        }).collect(Collectors.toList());
        collect.add(0, newHeaders);
        return collect;
    }

    /**
     * 显示所有员工工资
     */
    public void showSalary() {
        for (List<String> row : this.getSalaryList()) {
            System.out.println(row);
        }
    }

    /**
     * 查询工资
     */
    public void querySalary() {
        List<List<String>> salaryList = this.getSalaryList();
        List<List<String>> rawCtx = salaryList.subList(1, salaryList.size());
        Scanner sc = ScannerHelper.getInstance();
        System.out.println("请输入工号或姓名：");
        String workNoOrName = sc.next();
        List<List<String>> collect = rawCtx.stream().filter(
                x -> workNoOrName.equals(x.get(salaryList.get(0).indexOf("work_no"))) ||
                    workNoOrName.equals(x.get(salaryList.get(0).indexOf("name")))
            )
            .collect(Collectors.toList());
        System.out.println(salaryList.get(0)); // header
        for (List<String> row : collect) {
            System.out.println(row);
        }
    }


    /**
     * 查看员工
     */
    public void lookupStaff() {
        this.getEmployeeHandler().getContents().forEach(System.out::println);
    }

    /**
     * 添加员工
     */
    public boolean addStaff() {
        Scanner sc = ScannerHelper.getInstance();
        System.out.println("请输入工号：");
        String workNo = sc.next();
        if (this.getEmployeeHandler().find("work_no", workNo) != null) {
            System.out.println(">>>工号已存在，重新设置工号");
            return false;
        }
        Map<String, String> newEmployee = this.getScannerMap();
        if (newEmployee == null) {
            return false;
        }
        newEmployee.put("work_no", workNo);
        this.getEmployeeHandler().add(newEmployee);
        System.out.printf("<<<添加完成，当前员工数量：%d%n", this.getEmployeeHandler().getShape().get(0));
        return true;
    }

    /**
     * 删除员工
     */
    public boolean deleteStaff() {
        Scanner sc = ScannerHelper.getInstance();
        System.out.println("请输入工号：");
        String workNo = sc.next();
        if (this.getEmployeeHandler().find("work_no", workNo) == null) {
            System.out.println(">>>工号不存在，重新输入工号");
            return false;
        }
        this.getEmployeeHandler().delete("work_no", workNo);
        System.out.printf("<<<删除完成，当前员工数量：%d%n", this.getEmployeeHandler().getShape().get(0));
        return true;
    }

    /**
     * 修改员工
     *
     * @return
     */
    public boolean updateStaff() {
        Scanner sc = ScannerHelper.getInstance();
        System.out.println("请输入工号：");
        String workNo = sc.next();
        if (this.getEmployeeHandler().find("work_no", workNo) == null) {
            System.out.println(">>>工号不存在，重新输入工号");
            return false;
        }
        Map<String, String> info = this.getScannerMap();
        if (info == null) {
            return false;
        }
        String id = this.getEmployeeHandler().findMap("work_no", workNo).get("id");
        this.getEmployeeHandler().updateById(Integer.valueOf(id), info);
        return true;
    }

    /**
     * 获取姓名、职位名称、薪资计算规则
     * TODO: 动态计算输入参数
     *
     * @return
     */
    public Map<String, String> getScannerMap() {
        Scanner sc = ScannerHelper.getInstance();
        System.out.println("请输入姓名：");
        String name = sc.next();
        System.out.println("请输入职位名称：");
        String job = sc.next();
        System.out.println("请输入薪资计算规则：");
        String salaryRule = sc.next();
        if (this.getRuleHandler().find("rule_name", salaryRule) == null) {
            System.out.println(">>>薪资计算规则错误");
            return null;
        }
        System.out.println("请输入上学期工作量：");
        String lastSemesterWorkload = sc.next();

        Map<String, String> newEmployee = new HashMap<>();
        newEmployee.put("name", name);
        newEmployee.put("job", job);
        newEmployee.put("salary_rule", salaryRule);
        newEmployee.put("last_semester_workload", lastSemesterWorkload);
        return newEmployee;
    }

}
