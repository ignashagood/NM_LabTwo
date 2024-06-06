package org.example

import java.awt.BasicStroke
import java.awt.BorderLayout
import java.awt.Button
import java.awt.Color
import java.awt.Dialog
import java.awt.FlowLayout
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Label
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import org.example.FunctionOptimization.coordinateDescentWithPath
import org.example.FunctionOptimization.gradientDescentWithPath
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class FunctionPlotter(
    private val func: Function,
    private val a: Double,
    private val b: Double,
    private val c: Double,
    private val d: Double
) :
    JPanel() {
    private var path: List<DoubleArray?>

    init {
        path = ArrayList()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2d = g as Graphics2D

        // Отрисовка координатной сетки
        drawGrid(g2d)

        // Отрисовка контуров функции
        drawContours(g2d)

        // Отрисовка пути к минимуму
        if (!path.isEmpty()) {
            drawPath(g2d)
        }
    }

    private fun drawGrid(g2d: Graphics2D) {
        val width = width
        val height = height
        g2d.color = Color.LIGHT_GRAY

        run {
            var x = a
            while (x <= b) {
                val screenX = ((x - a) / (b - a) * width).toInt()
                g2d.drawLine(screenX, 0, screenX, height)
                x += (b - a) / 20
            }
        }

        run {
            var y = c
            while (y <= d) {
                val screenY = ((d - y) / (d - c) * height).toInt()
                g2d.drawLine(0, screenY, width, screenY)
                y += (d - c) / 20
            }
        }

        // Оси координат
        g2d.color = Color.BLACK
        g2d.stroke = BasicStroke(2f) // Установка толщины осей
        g2d.drawLine(0, height / 2, width, height / 2) // Ось X
        g2d.drawLine(width / 2, 0, width / 2, height) // Ось Y
    }

    private fun drawContours(g2d: Graphics2D) {

        // Задаем уровни контуров
        val levels = DoubleArray(20)
        for (i in levels.indices) {
            levels[i] = -1 + i * 0.1
        }

        for (level in levels) {
            g2d.color = Color.BLUE
            run {
                var x = a
                while (x <= b) {
                    run {
                        var y = c
                        while (y <= d) {
                            val z: Double = func(x, y)
                            if (abs(z - level) < 0.01) {
                                val screenX = ((x - a) / (b - a) * width).toInt()
                                val screenY = ((d - y) / (d - c) * height).toInt()
                                if (screenX >= 0 && screenX < width && screenY >= 0 && screenY < height) {
                                    g2d.fillRect(screenX, screenY, 1, 1)
                                }
                            }
                            y += 0.01
                        }
                    }
                    x += 0.01
                }
            }
        }
    }

    private fun drawPath(g2d: Graphics2D) {
        val width = width
        val height = height
        g2d.color = Color.RED
        g2d.stroke = BasicStroke(2f)
        for (i in 0 until path.size - 1) {
            val x1 = ((path[i]!![0] - a) / (b - a) * width).toInt()
            val y1 = ((d - path[i]!![1]) / (d - c) * height).toInt()
            val x2 = ((path[i + 1]!![0] - a) / (b - a) * width).toInt()
            val y2 = ((d - path[i + 1]!![1]) / (d - c) * height).toInt()
            g2d.drawLine(x1, y1, x2, y2)
        }
    }

    fun setPath(path: List<DoubleArray?>) {
        this.path = path
        repaint()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // Ввод функции
            val frame = JFrame("Function Plotter")
            val func: Function = { x, y -> sqrt(x + y) }
            val plotter = FunctionPlotter(func, -5.0, 5.0, -5.0, 5.0)
            val inputPanel = JPanel()
            val epsilonField = JTextField("0.01", 5)
            val x0Field = JTextField("", 5)
            val y0Field = JTextField("", 5)
            val descentButton = JButton("Координатный спуск")
            val gradientButton = JButton("Градиентный спуск")

            val dialog = Dialog(frame, true)
            dialog.setSize(400, 300)
            dialog.setLayout(FlowLayout())
            dialog.add(Label("Не удалось найти минимум функции"))
            val okButton = Button("OK").apply {
                addActionListener {
                    dialog.dispose()
                }
            }
            dialog.add(okButton)

            inputPanel.add(JLabel("ε:"))
            inputPanel.add(epsilonField)
            inputPanel.add(JLabel("x0:"))
            inputPanel.add(x0Field)
            inputPanel.add(JLabel("y0:"))
            inputPanel.add(y0Field)
            inputPanel.add(descentButton)
            inputPanel.add(gradientButton)
            descentButton.addActionListener {
                val epsilon = epsilonField.getText().toDouble()
                val x0 = x0Field.getText().toDouble()
                val y0 = y0Field.getText().toDouble()
                val path: MutableList<DoubleArray?> = ArrayList()
                val result = coordinateDescentWithPath(func, x0, y0, epsilon, path)
                plotter.setPath(path)
                if (result != null) {
                    println("Результат покоординатного спуска: (" + result[0] + ", " + result[1] + ")")
                } else {
                    dialog.isVisible = true
                    println("Не удалось найти точку минимума")
                }
            }
            gradientButton.addActionListener {
                val epsilon = epsilonField.getText().toDouble()
                val x0 = x0Field.getText().toDouble()
                val y0 = y0Field.getText().toDouble()
                val path: MutableList<DoubleArray?> = ArrayList()
                val result = gradientDescentWithPath(func, x0, y0, epsilon, path)
                plotter.setPath(path)
                if (result != null) {
                    println("Результат градиентного спуска: (" + result[0] + ", " + result[1] + ")")
                } else {
                    dialog.isVisible = true
                    println("Не удалось найти точку минимума")
                }
            }
            frame.layout = BorderLayout()
            frame.add(inputPanel, BorderLayout.NORTH)
            frame.add(plotter, BorderLayout.CENTER)
            frame.setSize(1300, 1200)
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
            frame.isVisible = true
        }
    }
}
