package com.yang.customviewdemo.utils


/**
 * Create by Yang Yang on 2023/7/25
 */
class ScaleHelper(vararg scales: Float) {

    private var mScales: FloatArray = floatArrayOf()

    init {
        updateScales(scales)
    }

    /**
     * 更新平滑缩放比例，数组长度必须是偶数
     * 偶数索引表示要缩放的比例，奇数索引表示位置 (0~1)
     * 奇数索引必须要递增，即越往后的数值应越大
     * 例如：
     * [0.8, 0.5] 表示在50%处缩放到原来的80%
     * [0, 0, 1, 0.5, 0, 1]表示在起点处的比例是原来的0%，在50%处会恢复原样，到终点处会缩小到0%
     *
     * @param params 每个位置上的缩放比例
     */
    private fun updateScales(params: FloatArray) {
        val scales: FloatArray = if (params.isEmpty()) {
            floatArrayOf(1f, 0f, 1f, 1f)
        } else {
            params
        }

        //检查是否存在负数
        scales.forEach {
            if (it < 0) {
                throw IllegalArgumentException("Array value can not be negative!")
            }
        }

        if (!mScales.contentEquals(scales)) {
            if (scales.size < 2 || scales.size % 2 != 0) {
                throw IllegalArgumentException("Array length no match!")
            }
            mScales = scales
        }
    }

    /**
     * 获取指定位置的缩放比例
     * @param fraction 当前位置(0~1)
     */
    fun getScale(fraction: Float): Float {
        var minScale = 1f
        var maxScale = 1f
        var scalePosition = 0f
        var minFraction = 0f
        var maxFraction = 1f

        //顺序遍历，找到小于fraction的，最贴近的scale
        for (i in 1 until mScales.size step 2) {
            scalePosition = mScales[i]
            if (scalePosition <= fraction) {
                minScale = mScales[i - 1]
                minFraction = scalePosition
            } else {
                break
            }
        }

        //倒序遍历，找到大于fraction的，最贴近的scale
        for (i in mScales.size - 1 downTo 1 step 2 ) {
            scalePosition = mScales[i]
            if (scalePosition >= fraction) {
                maxScale = mScales[i - 1]
                maxFraction = mScales[i]
            } else {
                break
            }
        }

        //计算当前点fraction，在起始点minFraction与结束点maxFraction中的百分比
        val relativeFraction = solveTwoPointRelativePercent(minFraction, fraction, maxFraction)
        //最大缩放 - 最小缩放 = 要缩放的范围
        val distance = maxScale - minScale
        //缩放范围 * 当前位置 = 当前缩放比例
        val scale = distance * relativeFraction
        //加上基本的缩放比例
        val finalScale = minScale + scale
        //如果得出的数值不合法，则直接返回基本缩放比例
        return if (finalScale.isFinite()) finalScale else minScale

    }

    /**
     * 将基于总长度的百分比转换成基于某个片段的百分比 (解两点式直线方程)
     * 计算相对百分比
     *
     * @param startX   片段起始百分比
     * @param endX     片段结束百分比
     * @param currentX 总长度百分比
     * @return 该片段的百分比
     */
    private fun solveTwoPointRelativePercent(startX: Float, currentX: Float, endX: Float): Float {
        return (currentX - startX) / (endX - startX)
    }

}