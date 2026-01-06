import com.example.otomata.PlateValidator
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class PlateValidatorValidPlatesTest(
    private val plate: String
) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{1}: {0}")
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf("34 A 1234", "1 Harf - 4 Rakam"),
                arrayOf("01 A 12345", "1 Harf - 5 Rakam"),
                arrayOf("06 AB 123", "2 Harf - 3 Rakam"),
                arrayOf("54 SU 2025", "2 Harf - 4 Rakam"),
                arrayOf("35 ABC 12", "3 Harf - 2 Rakam"),
                arrayOf("34 CAN 123", "3 Harf - 3 Rakam"),
                arrayOf("34 abc 123", "Küçük harf test"),
                arrayOf("06 AB 1234", "2 Harf - 4 Rakam"),
                arrayOf("81 Z 9999", "Max il kodu")
            )
        }
    }

    private lateinit var validator: PlateValidator

    @Before
    fun setup() {
        validator = PlateValidator()
    }

    @Test
    fun testGecerliPlaka() {
        assertTrue("$plate gecerli olmaliydi", validator.validate(plate))
    }
}

@RunWith(Parameterized::class)
class PlateValidatorInvalidPlatesTest(
    private val plate: String,
    private val description: String
) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{1}: {0}")
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf("00 A 1234", "Gecersiz il kodu (00)"),
                arrayOf("82 A 1234", "Gecersiz il kodu (82)"),
                arrayOf("34 QW 123", "Yasakli harf (Q)"),
                arrayOf("34 W 123", "Yasakli harf (W)"),
                arrayOf("34 X 123", "Yasakli harf (X)"),
                arrayOf("34 AA 1", "Eksik rakam (1 rakam)"),
                arrayOf("34 AAA 1234", "Fazla harf (4 harf)"),
                arrayOf("34A1234", "Yanlis format (Bosluksuz)"),
                arrayOf("34 A-1234", "Gecersiz karakter (-)"),
                arrayOf("34 A 12", "Eksik rakam (2 rakam)"),
                arrayOf("34 AB 12", "2 Harf - 2 Rakam (min 3 gerekli)"),
                arrayOf("34 A 123456", "Fazla rakam (6 rakam)"),
                arrayOf("34 Ç 1234", "Yasakli harf (Ç)"),
                arrayOf("34 Ş 1234", "Yasakli harf (Ş)"),
                arrayOf("34 İ 1234", "Yasakli harf (İ)"),
                arrayOf("34 Ö 1234", "Yasakli harf (Ö)"),
                arrayOf("34 Ü 1234", "Yasakli harf (Ü)"),
                arrayOf("34 Ğ 1234", "Yasakli harf (Ğ)")
            )
        }
    }

    private lateinit var validator: PlateValidator

    @Before
    fun setup() {
        validator = PlateValidator()
    }

    @Test
    fun testGecersizPlaka() {
        assertFalse("$plate gecersiz olmaliydi", validator.validate(plate))
    }
}

// Özel durumlar için ayrı test sınıfı
class PlateValidatorSpecialCasesTest {

    private lateinit var validator: PlateValidator

    @Before
    fun setup() {
        validator = PlateValidator()
    }

    @Test
    fun testEmptyString() {
        assertFalse("Bos string gecersiz olmali", validator.validate(""))
    }

    @Test
    fun testOnlySpaces() {
        assertFalse("Sadece bosluk gecersiz olmali", validator.validate("   "))
    }

    @Test
    fun testIlKodu01ile09Arasi() {
        // 01-09 arası geçerli il kodları
        assertTrue(validator.validate("01 A 1234"))
        assertTrue(validator.validate("09 B 5678"))
    }

    @Test
    fun testIlKodu10ile79Arasi() {
        // 10-79 arası geçerli il kodları
        assertTrue(validator.validate("10 C 1234"))
        assertTrue(validator.validate("79 D 5678"))
    }

    @Test
    fun testIlKodu80ve81() {
        // 80 ve 81 geçerli il kodları
        assertTrue(validator.validate("80 E 1234"))
        assertTrue(validator.validate("81 F 5678"))
    }
}