import org.apache.batik.swing.JSVGCanvas
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.geom.AffineTransform
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities

var transform: AffineTransform = AffineTransform.getRotateInstance(0.0)
var ballPanel = object : JPanel() {


    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val g2 = g as Graphics2D
        g2.addRenderingHints(RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON))

        g2.drawImage(ImageIO.read((javaClass.getResourceAsStream("Basketball.png"))), transform, null)
    }
}

val ballFrame = JFrame("Ball")

var canvas = JSVGCanvas().apply {
    preferredSize = Dimension(100, 100)
    background = Color(0, 0, 0, 0)
    loadSVGDocument(javaClass.getResource("/Basketball_plain.svg")?.toString())

    setBounds(0, 0, 100, 100)
    var t = AffineTransform.getRotateInstance(0.5 * Math.PI, 50.0, 50.0)
    setRenderingTransform(t)



}

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
        isAlwaysOnTop = true
        pack()

        val mouseHandler = MouseHandler(ballFrame, ballPanel)
        addMouseListener(mouseHandler)
        addMouseMotionListener(mouseHandler)
    }

    ballFrame.isVisible = true
}