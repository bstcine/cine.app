package com.bstcine.h5.data

/**
 * API 请求统一返回数据格式模型
 * Created by itwangxiang on 2018/12/28.
 */

class ResModel<T> {

    var code: String? = null
    var code_desc: String? = null
    var except_case: String? = null
    var except_case_desc: String? = null
    var result: T? = null
}
