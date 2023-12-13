package com.refengSGL.constant;

public interface ResultCode {
    /** 请求成功 */
    Integer SUCCESS = 200;

    /**
     * 请求失败
     */
    Integer ERROR = 0;

    Integer ACCEPTED = 202;

    /** 操作已经执行成功，但是没有返回数据 */
    Integer NO_CONTENT = 204;

    /**
     * 资源已经被移除
     */
    Integer MOVE_PERM = 301;

    Integer SEE_OTHER = 303;

    /**
     * 资源没有被修改
     */
    Integer NOT_MODIFIED = 304;

    Integer BAD_REQUEST = 400;

    Integer UNAUTHORIZED = 401;

    Integer FORBIDDEN = 403;

    Integer NOT_FOUND = 404;

    Integer BAD_METHOD = 405;

    Integer CONFLICT = 409;

    Integer UNSUPPORTED_TYPE = 415;

    Integer NOT_IMPLEMENTED = 501;
}
