package com.chenc.fuploads.pojo

/** http 响应基类 */
class BaseResponse<T> private constructor(
    var code: Int, 
    var message: String, 
    var data: T?) {
        
    companion object {
        inline fun <T> build(block: Builder<T>.() -> Unit) = Builder<T>().apply(
            block
        ).build()
    }

    class Builder<T> {
        var code: Int = 200
        var message: String = "OK"
        var data: T? = null

        fun build() = BaseResponse<T>(code, message, data)
    }
}
