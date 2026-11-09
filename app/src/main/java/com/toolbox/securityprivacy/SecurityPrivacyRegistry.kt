package com.toolbox.securityprivacy

import androidx.compose.runtime.Composable

object SecurityPrivacyRegistry {
    @Composable fun PasswordAudit() = PasswordAuditScreen()
    @Composable fun AesEncryption() = AesEncryptionScreen()
    @Composable fun ChecksumVerifier() = ChecksumVerifierScreen()
    @Composable fun Steganography() = SteganographyScreen()
    @Composable fun UuidValidator() = UuidValidatorScreen()
}
