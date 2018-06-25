package com.testerum.scanner.step_lib_scanner.model

import java.time.Instant

data class ScannerLibraryFile(val name: String,
                              val sizeInBytes: Long,
                              val modifiedTimestamp: Instant)
