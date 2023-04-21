package org.example;

import org.example.util.ScannerHelper;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        String cursorPath = Main.class.getClassLoader().getResource("").getPath();
        Service service = new Service();
        service.setEmployeeDatabasePath(cursorPath + "./database/employee.csv");
        service.setRuleDatabasePath(cursorPath + "./database/rule.csv");

        while (true) {
            System.out.println("欢迎使用工资查询系统");
            System.out.printf("当前员工数量：%d%n", service.getEmployeeHandler().getShape().get(0));
            System.out.println("----------------");
            System.out.println("0——退出系统       ");
            System.out.println("1——查看员工       ");
            System.out.println("2——添加员工       ");
            System.out.println("3——删除员工       ");
            System.out.println("4——修改员工       ");
            System.out.println("5——显示所有员工工资       ");
            System.out.println("6——查询工资       ");

            System.out.println("请输入选项：");
            Scanner sc = ScannerHelper.getInstance();
            int choose = sc.nextInt();
            switch (choose) {
                case 0:
                    // 退出系统
                    return;
                case 1:
                    // 查看员工
                    service.lookupStaff();
                    break;
                case 2:
                    // 添加员工
                    service.addStaff();
                    break;
                case 3:
                    // 删除员工
                    service.deleteStaff();
                    break;
                case 4:
                    // 修改员工
                    service.updateStaff();
                    break;
                case 5:
                    // 显示所有员工工资
                    service.showSalary();
                    break;
                case 6:
                    // 查询工资
                    service.querySalary();
                    break;
                default:
                    System.out.println("菜单选项错误");
            }

            service.getEmployeeHandler().save();
        }
    }

}
