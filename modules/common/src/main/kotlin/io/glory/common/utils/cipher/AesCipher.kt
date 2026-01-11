package io.glory.common.utils.cipher

import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.security.GeneralSecurityException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AesCipher {

    const val AES_ECB_PKCS_5_PADDING = "AES/ECB/PKCS5Padding"
    const val AES_CBC_PKCS_5_PADDING = "AES/CBC/PKCS5Padding"

    private const val AES = "AES"
    private val DEFAULT_CHARSET: Charset = UTF_8

    @JvmStatic
    @Throws(GeneralSecurityException::class)
    fun encrypt(
        input: String,
        key: String,
        iv: String? = null,
        mode: String = AES_CBC_PKCS_5_PADDING,
    ): String {
        val secretKey = SecretKeySpec(key.toByteArray(DEFAULT_CHARSET), AES)
        val ivParameterSpec = getIvParameterSpec(iv)
        return encrypt(input, secretKey, ivParameterSpec, mode)
    }

    @JvmStatic
    @Throws(GeneralSecurityException::class)
    fun encrypt(
        input: String,
        key: SecretKey,
        iv: IvParameterSpec?,
        mode: String = AES_CBC_PKCS_5_PADDING,
    ): String {
        val cipher = Cipher.getInstance(mode)
        if (mode == AES_ECB_PKCS_5_PADDING) {
            cipher.init(Cipher.ENCRYPT_MODE, key)
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, key, iv)
        }
        val cipherText = cipher.doFinal(input.toByteArray(DEFAULT_CHARSET))
        return Base64.getEncoder().encodeToString(cipherText)
    }

    @JvmStatic
    @Throws(GeneralSecurityException::class)
    fun decrypt(
        cipherText: String,
        key: String,
        iv: String? = null,
        mode: String = AES_CBC_PKCS_5_PADDING,
    ): String {
        val secretKey = SecretKeySpec(key.toByteArray(DEFAULT_CHARSET), AES)
        val ivParameterSpec = getIvParameterSpec(iv)
        return decrypt(cipherText, secretKey, ivParameterSpec, mode)
    }

    @JvmStatic
    @Throws(GeneralSecurityException::class)
    fun decrypt(
        cipherText: String,
        key: SecretKey,
        iv: IvParameterSpec?,
        mode: String = AES_CBC_PKCS_5_PADDING,
    ): String {
        val cipher = Cipher.getInstance(mode)
        if (mode == AES_ECB_PKCS_5_PADDING) {
            cipher.init(Cipher.DECRYPT_MODE, key)
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key, iv)
        }
        val plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText))
        return String(plainText, DEFAULT_CHARSET)
    }

    @JvmStatic
    @Throws(NoSuchAlgorithmException::class)
    fun getSecretKey(key: String, digestAlgorithm: String): SecretKeySpec {
        return SecretKeySpec(getMessageDigest(digestAlgorithm).digest(key.toByteArray(UTF_8)), AES)
    }

    @JvmStatic
    @Throws(NoSuchAlgorithmException::class)
    fun getInitialVector(iv: String, digestAlgorithm: String): IvParameterSpec {
        return IvParameterSpec(getMessageDigest(digestAlgorithm).digest(iv.toByteArray(DEFAULT_CHARSET)))
    }

    @JvmStatic
    @Throws(UnsupportedEncodingException::class)
    fun customDecoder(value: String): String {
        val encode = URLEncoder.encode(value, DEFAULT_CHARSET.name())
        return URLDecoder.decode(encode, DEFAULT_CHARSET.name())
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun getMessageDigest(digestAlgorithm: String): MessageDigest {
        return MessageDigest.getInstance(digestAlgorithm)
    }

    private fun getIvParameterSpec(iv: String?): IvParameterSpec {
        return if (iv.isNullOrEmpty()) {
            IvParameterSpec(ByteArray(16)) // 16-byte array filled with zeros
        } else {
            IvParameterSpec(iv.toByteArray(DEFAULT_CHARSET))
        }
    }

}