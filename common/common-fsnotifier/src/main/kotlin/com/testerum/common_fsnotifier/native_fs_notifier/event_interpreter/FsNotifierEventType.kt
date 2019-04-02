package com.testerum.common_fsnotifier.native_fs_notifier.event_interpreter

enum class FsNotifierEventType {
    GIVEUP,
    RESET,
    UNWATCHEABLE,
    REMAP,
    MESSAGE,
    CREATE,
    DELETE,
    STATS,
    CHANGE,
    DIRTY,
    RECDIRTY
}
