import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.JFrame
import javax.swing.JPanel

var ballPanel = object : JPanel() {
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val g2 = g as Graphics2D
        g2.addRenderingHints(RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON))

        g.color = Color.BLUE
        g.fillOval(0, 0, 100, 100)
    }
}

val ballFrame = JFrame("Ball")

fun main() {
    ballPanel.apply {
        preferredSize = Dimension(100, 100)
        background = Color(0xff, 0xff, 0xff, 0)
        layout = null
    }
    ballFrame.apply {
        add(ballPanel)
        isUndecorated = true
        background = Color(0xff, 0xff, 0xff, 0)
        pack()
        val mouseHandler = MouseHandler(ballFrame)
        addMouseListener(mouseHandler)
        addMouseMotionListener(mouseHandler)
    }

    ballFrame.isVisible = true
}