package com.testerum.launcher

import com.testerum.launcher.config.ConfigManager
import com.testerum.launcher.runner.TesterumExecuter
import com.testerum.launcher.ui.MainFrame
import javax.swing.JOptionPane
import javax.swing.WindowConstants

fun main(args: Array<String>) {
//    UIManager.setLookAndFeel(
//            Plastic3DLookAndFeel()
//    )

    val testerumRunner = TesterumExecuter()

    val frame = MainFrame(ConfigManager, testerumRunner)
    testerumRunner.startTesterum()

    frame.defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
    frame.centerOnScreen()
    frame.addWindowListener(object : java.awt.event.WindowAdapter() {
        override fun windowClosing(windowEvent: java.awt.event.WindowEvent) {
            val confirmDialogResult = JOptionPane.showConfirmDialog(frame,
                    "Closing this window will stop Testerum and it won't be accessible from the browser anymore.\nDo you want to continue?", "Close Window?",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE
            )
            if (confirmDialogResult == JOptionPane.OK_OPTION) {
                testerumRunner.stopTesterum()
                System.exit(0)
            }
        }
    })

    frame.isVisible = true
}
