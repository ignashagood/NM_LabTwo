package org.example

import kotlin.math.abs
import kotlin.math.sqrt

typealias Function = (Double, Double) -> Double

object FunctionOptimization {

    fun coordinateDescentWithPath(
        func: Function,
        x0: Double,
        y0: Double,
        epsilon: Double,
        path: MutableList<DoubleArray?>
    ): DoubleArray? {
        var x = x0
        var y = y0
        var delta = epsilon + 1
        path.add(doubleArrayOf(x, y))
        var count = 0
        while (delta > epsilon && count < 10000000) {
            val prevX = x
            val prevY = y
            x = goldenSectionSearch(y, x, func, true)
            y = goldenSectionSearch(x, y, func, false)
            path.add(doubleArrayOf(x, y))
            delta = sqrt((x - prevX) * (x - prevX) + (y - prevY) * (y - prevY))
            count++
        }
        if (count > 9999999) {
            path.clear()
            return null
        }
        return doubleArrayOf(x, y)
    }

    fun gradientDescentWithPath(
        func: Function,
        x0: Double,
        y0: Double,
        epsilon: Double,
        path: MutableList<DoubleArray?>
    ): DoubleArray? {
        var x = x0
        var y = y0
        var alpha = 1.0
        var delta = epsilon + 1
        path.add(doubleArrayOf(x, y))
        var count = 0
        while (delta > epsilon && count < 100) {
            val grad = gradient(func, x, y)

            // золотое сечение для поиска оптимального альфа
            alpha = goldenSectionSearchForAlpha(func, x, y, grad)
            val newX = x - alpha * grad[0]
            val newY = y - alpha * grad[1]

            // добавление точки после обновления только одной координаты
            //path.add(new double[]{newX, y}); // ставилась лишняя точка на графике
            path.add(doubleArrayOf(newX, newY))
            delta = sqrt((newX - x) * (newX - x) + (newY - y) * (newY - y))
            x = newX
            y = newY
            count++
        }
        if (count > 99) {
            path.clear()
            return null
        }
        return doubleArrayOf(x, y)
    }

    fun gradient(func: Function, x: Double, y: Double): DoubleArray {
        val h = 1e-5
        val dfdx = (func.invoke(x + h, y) - func.invoke(x, y)) / h
        val dfdy = (func.invoke(x, y + h) - func.invoke(x, y)) / h
        return doubleArrayOf(dfdx, dfdy)
    }

    fun goldenSectionSearch(fixed: Double, `var`: Double, func: Function, isX: Boolean): Double {
        var a = `var` - 1
        var b = `var` + 1
        val gr = (sqrt(5.0) + 1) / 2
        var c = b - (b - a) / gr
        var d = a + (b - a) / gr
        while (abs(c - d) > 1e-5) {
            if (isX) {
                if (func.invoke(c, fixed) < func.invoke(d, fixed)) {
                    b = d
                } else {
                    a = c
                }
            } else {
                if (func.invoke(fixed, c) < func.invoke(fixed, d)) {
                    b = d
                } else {
                    a = c
                }
            }
            c = b - (b - a) / gr
            d = a + (b - a) / gr
        }
        return (a + b) / 2
    }

    fun goldenSectionSearchForAlpha(func: Function, x: Double, y: Double, grad: DoubleArray): Double {
        var a = 0.0
        var b = 1.0
        val gr = (sqrt(5.0) + 1) / 2
        var c = b - (b - a) / gr
        var d = a + (b - a) / gr
        var count = 0
        while (abs(c - d) > 1e-5) {
            val fc = func.invoke(x - c * grad[0], y - c * grad[1])
            val fd = func.invoke(x - d * grad[0], y - d * grad[1])
            if (fc < fd) {
                b = d
            } else {
                a = c
            }
            c = b - (b - a) / gr
            d = a + (b - a) / gr
            count++
        }
        count.plus(1)
        return (a + b) / 2
    }
}
