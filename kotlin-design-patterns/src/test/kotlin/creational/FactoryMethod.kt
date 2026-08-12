package creational

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

// sealed - “Todas as implementações possíveis de Country estão definidas neste arquivo.”

sealed class Country {
    object Canada : Country()
}

object Spain : Country()
class Greece(val greeting: String) : Country()
data class USA(val someProperty: String) : Country()

class Currency(val code: String)

object CurrencyFactory {
    fun currencyForCountry(country: Country): Currency {
        return when (country) {
            is Spain -> Currency("EUA")
            is Greece -> Currency("EUR")
            is USA -> Currency("USD")
            is Country.Canada -> Currency("CAD")
        }
    }
}

class CurrencyFactoryTest {

    @Test
    fun currencyTest(){
        val geekCurrency = CurrencyFactory.currencyForCountry(Greece("")).code
        println(geekCurrency)

        val usaCurrency = CurrencyFactory.currencyForCountry(USA("")).code
        println(usaCurrency)

        Assertions.assertThat(geekCurrency).isEqualTo("EUR")
        Assertions.assertThat(usaCurrency).isEqualTo("USD")
    }
}