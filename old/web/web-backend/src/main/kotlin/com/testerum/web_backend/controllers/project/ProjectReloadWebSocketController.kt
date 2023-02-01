package com.testerum.web_backend.controllers.project

import com.testerum.web_backend.services.project.ProjectFileSystemWatcher
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.nio.file.Path
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class ProjectReloadWebSocketController(projectFileSystemWatcher: ProjectFileSystemWatcher) : TextWebSocketHandler() {

    private val lock = ReentrantReadWriteLock()

    private val sessionsById = LinkedHashMap</*id:*/String, WebSocketSession>()

    init {
        projectFileSystemWatcher.registerReloadListener(this::notifyProjectReload)
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        lock.write {
            sessionsById[session.id] = session
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        lock.write {
            sessionsById.remove(session.id)
        }
    }

    private fun notifyProjectReload(projectRootDir: Path) {
        lock.read {
            for (session in sessionsById.values) {
                session.sendMessage(
                        TextMessage(projectRootDir.toString())
                )
            }
        }
    }

}
