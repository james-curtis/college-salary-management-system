package org.example.util;

public interface SalaryFunction {

    /**
     *
     * @param lastSemesterWorkload 上月工作负载
     * @param target 按照目标薪资规则计算
     * @return
     */
    String calc(String lastSemesterWorkload, String target);

}
