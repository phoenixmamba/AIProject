package com.cuijian.aimeeting.saver;

import com.cuijian.aimeeting.ai.model.MultiFileCodeResult;

import java.io.File;

/**
 * 代码文件保存执行器
 * 根据代码生成类型执行相应的保存逻辑
 *
 */
public class CodeFileSaverExecutor {


    private static final MultiFileCodeFileSaverTemplate multiFileCodeFileSaver = new MultiFileCodeFileSaverTemplate();

    /**
     * 执行代码保存
     *
     * @param codeResult  代码结果对象
     * @param appId 应用 ID
     * @return 保存的目录
     */
    public static File executeSaver(Object codeResult, String appId) {
        return multiFileCodeFileSaver.saveCode((MultiFileCodeResult) codeResult, appId);
    }
}
