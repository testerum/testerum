package com.testerum.launcher

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel
import com.testerum.launcher.config.ConfigManager
import com.testerum.launcher.runner.TesterumExecuter
import com.testerum.launcher.ui.MainFrame
import javax.swing.JOptionPane
import javax.swing.UIManager
import javax.swing.WindowConstants

fun main(args: Array<String>) {
    UIManager.setLookAndFeel(
            Plastic3DLookAndFeel()
    )

    val testerumRunner = TesterumExecuter()
    testerumRunner.startTesterum()

    val frame = MainFrame(ConfigManager, testerumRunner)

    frame.defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
    frame.centerOnScreen()
    frame.addWindowListener(object : java.awt.event.WindowAdapter() {
        override fun windowClosing(windowEvent: java.awt.event.WindowEvent) {
            val confirmDialogResult = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to close this window?", "Close Window?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            )
            if (confirmDialogResult == JOptionPane.YES_OPTION) {
                testerumRunner.stopTesterum()
                System.exit(0)
            }
        }
    })

    frame.isVisible = true
}
