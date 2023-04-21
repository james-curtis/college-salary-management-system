package org.example.util;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelOperator {

    private String path;
    private String charset = "UTF-8";

    //    protected List<List<String>> contents;
    protected List<List<String>> rawContents;
    protected List<String> headers;

    public ExcelOperator() {

    }

    public ExcelOperator(String path) {
        this.path = path;
    }

    public ExcelOperator(String path, String charset) {
        this.path = path;
        this.charset = charset;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTable(List<List<String>> contents) {
        this.headers = contents.get(0);
        this.rawContents = contents.subList(1, contents.size());
    }

    public void initReadFile() throws IOException {
        this.rawContents = new ArrayList<>();

        Reader fileReader = new InputStreamReader(new FileInputStream(this.path), Charset.forName(this.charset));
        Scanner scanner = new Scanner(fileReader);
        for (int i = 0; scanner.hasNextLine(); i++) {
            String line = scanner.nextLine();
            List<String> currentLineList = parseLine(line);
            if (i == 0) {
                // 表头
                this.headers = currentLineList;
            } else {
                this.rawContents.add(currentLineList);
            }
        }
        scanner.close();
    }

    protected static List<String> parseLine(String line) {
        return parseLine(line, ",");
    }

    protected static List<String> parseLine(String line, String regex) {
        String[] split = line.split(regex);
        List<String> result = new ArrayList<String>(split.length);
        Collections.addAll(result, split);
        return result;
    }


    public String getCell(Integer row, Integer column) {
        return rawContents.get(row).get(column);
    }

    public List<String> getRow(Integer row) {
        return rawContents.get(row);
    }

    public List<String> getColumns(Integer column) {
        return rawContents.stream().map(row -> row.get(column)).collect(Collectors.toList());
    }

    public List<List<String>> getContents() {
        List<List<String>> result = new ArrayList<>();
        result.add(this.headers);
        result.addAll(this.rawContents);
        return result;
    }

    public List<List<String>> getRawContents() {
        return this.rawContents;
    }

    public List<String> getHeaders() {
        return this.headers;
    }

    protected void write(String path) throws IOException {
        try (Writer writer = new FileWriter(path)) {

            // 写入表头
            writer.write(String.join(",", this.headers));

            // 写入内容
            for (List<String> item : this.rawContents) {
                writer.write("\n" + String.join(",", item));
            }
        }
    }

    /**
     * 查找一个元素
     *
     * @param field
     * @param value
     * @return
     */
    public List<String> find(String field, String value) {
        int idx = this.headers.indexOf(field);
        return this.rawContents.stream().filter(x -> x.get(idx).equals(value)).findFirst().orElse(null);
    }

    public Map<String, String> findMap(String field, String value) {
        int idx = this.headers.indexOf(field);
        List<String> result = this.rawContents.stream().filter(x -> x.get(idx).equals(value)).findFirst().orElse(null);
        if (result == null) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < result.size(); i++) {
            map.put(this.headers.get(i), result.get(i));
        }
        return map;
    }

    public Map<String, String> findMapById(Integer id) {
        return this.findMap("id", id.toString());
    }

    /**
     * 选择一个或多个元素
     *
     * @param field
     * @param values
     * @return
     */
    public List<List<String>> select(String field, List<String> values) {
        int idx = this.headers.indexOf(field);
        return this.rawContents.stream().filter(x -> values.contains(x.get(idx))).collect(Collectors.toList());
    }

    /**
     * 获取表的形状 返回：（行数量，列数量）
     *
     * @return
     */
    public List<Integer> getShape() {
        return Arrays.asList(this.rawContents.size(), this.rawContents.get(0).size());
    }

    /**
     * 增加一行
     *
     * @param row
     * @return
     */
    public ExcelOperator add(List<String> row) {
        this.rawContents.add(row);
        return this;
    }

    /**
     * 增加多行
     *
     * @param row
     * @return
     */
    public ExcelOperator add(String... row) {
        this.rawContents.add(Arrays.asList(row));
        return this;
    }

    public ExcelOperator add(Map<String, String> row) {
        List<String> newRow = new ArrayList<>();
        for (String key : this.headers) {
            if (row.containsKey(key)) {
                newRow.add(row.get(key));
            }
            // 没有指定id，则自动递增
            if ("id".equals(key) && !row.containsKey("id")) {
                newRow.add(String.valueOf(Integer.valueOf(this.getCell(this.getShape().get(0) - 1, 0)) + 1));
            }
        }
        this.add(newRow);
        return this;
    }

    public ExcelOperator update(String field, String value, String newValue) {
        int idx = this.headers.indexOf(field);
        this.rawContents.stream().filter(x -> x.get(idx).equals(value)).forEach(x -> x.set(idx, newValue));
        return this;
    }

    public ExcelOperator updateById(Integer id, Map<String, String> newValue) {
        this.rawContents.stream().filter(x -> x.get(0).equals(id.toString())).findFirst().ifPresent(x -> {
            for (int i = 0; i < this.headers.size(); i++) {
                String key = this.headers.get(i);
                if (newValue.containsKey(key)) {
                    x.set(i, newValue.get(key));
                }
            }
        });
        return this;
    }

    public void delete(String field, String value) {
        int idx = this.headers.indexOf(field);
        this.rawContents.removeIf(x -> x.get(idx).equals(value));
    }

    public void deleteById(Integer id) {
        this.rawContents.removeIf(x -> x.get(0).equals(id.toString()));
    }

    public void export(String path) {
        try {
            this.write(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            this.write(this.path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

