package io.glory.common.utils.cipher

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeEmpty
import java.security.GeneralSecurityException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class AesCipherTest : FunSpec({

    val validKey16 = "1234567890123456" // 16 bytes for AES-128
    val validKey32 = "12345678901234567890123456789012" // 32 bytes for AES-256
    val validIv16 = "abcdefghijklmnop" // 16 bytes IV
    val plainText = "Hello, World!"

    context("encrypt and decrypt with CBC mode") {
        test("should encrypt and decrypt with 16-byte key and IV") {
            // given
            val key = validKey16
            val iv = validIv16

            // when
            val encrypted = AesCipher.encrypt(plainText, key, iv)
            val decrypted = AesCipher.decrypt(encrypted, key, iv)

            // then
            encrypted.shouldNotBeEmpty()
            encrypted shouldNotBe plainText
            decrypted shouldBe plainText
        }

        test("should encrypt and decrypt with null IV using zero-filled IV") {
            // given
            val key = validKey16

            // when
            val encrypted = AesCipher.encrypt(plainText, key, null)
            val decrypted = AesCipher.decrypt(encrypted, key, null)

            // then
            encrypted.shouldNotBeEmpty()
            decrypted shouldBe plainText
        }

        test("should encrypt and decrypt with default parameters") {
            // given
            val key = validKey16

            // when
            val encrypted = AesCipher.encrypt(plainText, key)
            val decrypted = AesCipher.decrypt(encrypted, key)

            // then
            decrypted shouldBe plainText
        }

        test("should produce different ciphertext with different IVs") {
            // given
            val key = validKey16
            val iv1 = "aaaaaaaaaaaaaaaa"
            val iv2 = "bbbbbbbbbbbbbbbb"

            // when
            val encrypted1 = AesCipher.encrypt(plainText, key, iv1)
            val encrypted2 = AesCipher.encrypt(plainText, key, iv2)

            // then
            encrypted1 shouldNotBe encrypted2
        }

        test("should produce different ciphertext with different keys") {
            // given
            val key1 = "1111111111111111"
            val key2 = "2222222222222222"
            val iv = validIv16

            // when
            val encrypted1 = AesCipher.encrypt(plainText, key1, iv)
            val encrypted2 = AesCipher.encrypt(plainText, key2, iv)

            // then
            encrypted1 shouldNotBe encrypted2
        }
    }

    context("encrypt and decrypt with ECB mode") {
        test("should encrypt and decrypt with ECB mode") {
            // given
            val key = validKey16
            val mode = AesCipher.AES_ECB_PKCS_5_PADDING

            // when
            val encrypted = AesCipher.encrypt(plainText, key, null, mode)
            val decrypted = AesCipher.decrypt(encrypted, key, null, mode)

            // then
            encrypted.shouldNotBeEmpty()
            decrypted shouldBe plainText
        }

        test("should produce same ciphertext for same plaintext in ECB mode") {
            // given
            val key = validKey16
            val mode = AesCipher.AES_ECB_PKCS_5_PADDING

            // when
            val encrypted1 = AesCipher.encrypt(plainText, key, null, mode)
            val encrypted2 = AesCipher.encrypt(plainText, key, null, mode)

            // then
            encrypted1 shouldBe encrypted2
        }
    }

    context("encrypt and decrypt with SecretKey and IvParameterSpec") {
        test("should encrypt and decrypt with SecretKey and IvParameterSpec") {
            // given
            val secretKey = SecretKeySpec(validKey16.toByteArray(Charsets.UTF_8), "AES")
            val ivSpec = IvParameterSpec(validIv16.toByteArray(Charsets.UTF_8))

            // when
            val encrypted = AesCipher.encrypt(plainText, secretKey, ivSpec)
            val decrypted = AesCipher.decrypt(encrypted, secretKey, ivSpec)

            // then
            decrypted shouldBe plainText
        }

        test("should encrypt and decrypt with SecretKey in ECB mode") {
            // given
            val secretKey = SecretKeySpec(validKey16.toByteArray(Charsets.UTF_8), "AES")
            val mode = AesCipher.AES_ECB_PKCS_5_PADDING

            // when
            val encrypted = AesCipher.encrypt(plainText, secretKey, null, mode)
            val decrypted = AesCipher.decrypt(encrypted, secretKey, null, mode)

            // then
            decrypted shouldBe plainText
        }
    }

    context("getSecretKey") {
        test("should generate SecretKeySpec with MD5 digest") {
            // given
            val key = "mySecretKey"
            val digestAlgorithm = "MD5"

            // when
            val secretKey = AesCipher.getSecretKey(key, digestAlgorithm)

            // then
            secretKey shouldNotBe null
            secretKey.algorithm shouldBe "AES"
            secretKey.encoded.size shouldBe 16 // MD5 produces 128-bit hash
        }

        test("should generate SecretKeySpec with SHA-256 digest") {
            // given
            val key = "mySecretKey"
            val digestAlgorithm = "SHA-256"

            // when
            val secretKey = AesCipher.getSecretKey(key, digestAlgorithm)

            // then
            secretKey shouldNotBe null
            secretKey.algorithm shouldBe "AES"
            secretKey.encoded.size shouldBe 32 // SHA-256 produces 256-bit hash
        }

        test("should produce consistent key for same input") {
            // given
            val key = "mySecretKey"
            val digestAlgorithm = "SHA-256"

            // when
            val secretKey1 = AesCipher.getSecretKey(key, digestAlgorithm)
            val secretKey2 = AesCipher.getSecretKey(key, digestAlgorithm)

            // then
            secretKey1.encoded shouldBe secretKey2.encoded
        }
    }

    context("getInitialVector") {
        test("should generate IvParameterSpec with MD5 digest") {
            // given
            val iv = "myInitialVector"
            val digestAlgorithm = "MD5"

            // when
            val ivSpec = AesCipher.getInitialVector(iv, digestAlgorithm)

            // then
            ivSpec shouldNotBe null
            ivSpec.iv.size shouldBe 16
        }

        test("should produce consistent IV for same input") {
            // given
            val iv = "myInitialVector"
            val digestAlgorithm = "MD5"

            // when
            val ivSpec1 = AesCipher.getInitialVector(iv, digestAlgorithm)
            val ivSpec2 = AesCipher.getInitialVector(iv, digestAlgorithm)

            // then
            ivSpec1.iv shouldBe ivSpec2.iv
        }
    }

    context("customDecoder") {
        test("should encode and decode special characters") {
            // given
            val value = "Hello World!"

            // when
            val result = AesCipher.customDecoder(value)

            // then
            result shouldBe value
        }

        test("should handle URL-unsafe characters") {
            // given
            val value = "test&param=value"

            // when
            val result = AesCipher.customDecoder(value)

            // then
            result shouldBe value
        }

        test("should handle Korean characters") {
            // given
            val value = "안녕하세요"

            // when
            val result = AesCipher.customDecoder(value)

            // then
            result shouldBe value
        }
    }

    context("encryption with hashed key and IV") {
        test("should encrypt and decrypt using getSecretKey and getInitialVector") {
            // given
            val keySource = "mySecretKeySource"
            val ivSource = "myIvSource"
            val digestAlgorithm = "MD5"

            val secretKey = AesCipher.getSecretKey(keySource, digestAlgorithm)
            val ivSpec = AesCipher.getInitialVector(ivSource, digestAlgorithm)

            // when
            val encrypted = AesCipher.encrypt(plainText, secretKey, ivSpec)
            val decrypted = AesCipher.decrypt(encrypted, secretKey, ivSpec)

            // then
            decrypted shouldBe plainText
        }
    }

    context("error handling") {
        test("should throw exception for invalid key length in raw mode") {
            // given
            val invalidKey = "short"
            val iv = validIv16

            // when & then
            shouldThrow<GeneralSecurityException> {
                AesCipher.encrypt(plainText, invalidKey, iv)
            }
        }

        test("should throw exception for invalid cipher text during decryption") {
            // given
            val key = validKey16
            val invalidCipherText = "invalidBase64=="

            // when & then
            shouldThrow<Exception> {
                AesCipher.decrypt(invalidCipherText, key)
            }
        }
    }

    context("constants") {
        test("should have correct constant values") {
            AesCipher.AES_ECB_PKCS_5_PADDING shouldBe "AES/ECB/PKCS5Padding"
            AesCipher.AES_CBC_PKCS_5_PADDING shouldBe "AES/CBC/PKCS5Padding"
        }
    }

    context("various input types") {
        test("should handle empty string") {
            // given
            val key = validKey16
            val emptyText = ""

            // when
            val encrypted = AesCipher.encrypt(emptyText, key)
            val decrypted = AesCipher.decrypt(encrypted, key)

            // then
            decrypted shouldBe emptyText
        }

        test("should handle long text") {
            // given
            val key = validKey16
            val longText = "A".repeat(10000)

            // when
            val encrypted = AesCipher.encrypt(longText, key)
            val decrypted = AesCipher.decrypt(encrypted, key)

            // then
            decrypted shouldBe longText
        }

        test("should handle special characters") {
            // given
            val key = validKey16
            val specialText = "!@#$%^&*()_+-={}[]|\\:\";<>?,./~`"

            // when
            val encrypted = AesCipher.encrypt(specialText, key)
            val decrypted = AesCipher.decrypt(encrypted, key)

            // then
            decrypted shouldBe specialText
        }

        test("should handle unicode characters") {
            // given
            val key = validKey16
            val unicodeText = "한글 테스트 日本語 中文 🎉"

            // when
            val encrypted = AesCipher.encrypt(unicodeText, key)
            val decrypted = AesCipher.decrypt(encrypted, key)

            // then
            decrypted shouldBe unicodeText
        }
    }
})
