package com.nohi.common.exception.file;

import com.nohi.common.exception.base.BaseException;

/**
 * 文件信息异常类
 *
 * @author nohi
 */
public class FileException extends BaseException {
    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args) {
        super("file", code, args, null);
    }

}
