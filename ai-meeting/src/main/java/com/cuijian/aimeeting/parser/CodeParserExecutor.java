package com.cuijian.aimeeting.parser;


/**
 * 代码解析执行器
 *
 */
public class CodeParserExecutor {


    private static final MultiFileCodeParser multiFileCodeParser = new MultiFileCodeParser();

    /**
     * 执行代码解析
     *
     * @param codeContent     代码内容
     * @return 解析结果（HtmlCodeResult 或 MultiFileCodeResult）
     */
    public static Object executeParser(String codeContent) {
        return multiFileCodeParser.parseCode(codeContent);
    }
}
