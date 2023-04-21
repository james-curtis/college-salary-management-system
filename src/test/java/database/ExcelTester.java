package database;

import org.example.util.ExcelOperator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单元测试
 * TODO: passed on 2023年4月20日 21:35:43
 */
public class ExcelTester {

    @Test
    public void test() {
        ExcelTester excelTester = new ExcelTester();
        excelTester.testCsvReader();
    }

    public void testCsvReader() {
        String currentPath = this.getClass().getClassLoader().getResource("").getPath();
        ExcelOperator excelOperator = new ExcelOperator();
        excelOperator.setPath(currentPath + "./database/rule.csv");
        try {
            excelOperator.initReadFile();
            List<List<String>> contents = excelOperator.getContents();

            // TODO: 测试获取形状
            System.out.println("------------测试获取形状");
            System.out.println(excelOperator.getShape());

            // TODO: 查询
            System.out.println("------------查询");
            System.out.println(excelOperator.find("rule_name", "teacher_part_time_experimenter_basic_salary"));
            System.out.println(excelOperator.findMap("rule_name", "teacher_part_time_experimenter_basic_salary"));

            // TODO: 增加
            System.out.println("------------增加");
            Map<String, String> newRow = new HashMap<>();
            int newId = excelOperator.getShape().get(0) + 1;
            newRow.put("id", String.valueOf(newId));
            newRow.put("rule_name", "test_newRow");
            newRow.put("rule_remark", "测试增加");
            newRow.put("rule_define", "test_newRow");
            excelOperator.add(newRow);
            System.out.println(excelOperator.getShape());
            System.out.println(excelOperator.findMapById(newId));

            // TODO: 修改
            System.out.println("------------修改");
            Map<String, String> updateRow = new HashMap<>();
            updateRow.put("rule_remark", "描述");
            updateRow.put("rule_define", "规则");
            excelOperator.updateById(newId, updateRow);
            System.out.println(excelOperator.findMapById(newId));

            // TODO: 删除
            System.out.println("------------删除");
            excelOperator.deleteById(newId);
            excelOperator.updateById(newId, updateRow);

            // TODO: 存档
            System.out.println("------------存档");
            excelOperator.export(currentPath + "./export.csv");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
