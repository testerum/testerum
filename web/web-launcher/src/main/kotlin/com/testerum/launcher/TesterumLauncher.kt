package com.testerum.launcher

import com.testerum.launcher.runner.TesterumRunner
import com.testerum.launcher.ui.MainFrame
import javax.swing.JOptionPane

fun main(args: Array<String>) {
    val testerumRunner = TesterumRunner()
    testerumRunner.startTesterum()

    val frame = MainFrame()
    frame.centerOnScreen()
    frame.isVisible = true

    frame.addWindowListener(object : java.awt.event.WindowAdapter() {
        override fun windowClosing(windowEvent: java.awt.event.WindowEvent?) {
            if (JOptionPane.showConfirmDialog(frame,
                            "Are you sure you want to close this window?", "Close Window?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                testerumRunner.stopTesterum()
                System.exit(0)
            }
        }
    })
}

//TODO: set a better look&feel