package net.xwdoor.basemodule.extension

/**
 * 给一个Int色值加上一个透明度。
 * (重载掉原来的透明度)
 *
 * @param alpha 透明度，取值: [0,1]
 */
fun Int.withAlpha(alpha: Float): Int {
    require(alpha in 0f..1.0f)
    return ((this shl 8) shr 8) + ((0xff * alpha).toInt() shl 24)
}