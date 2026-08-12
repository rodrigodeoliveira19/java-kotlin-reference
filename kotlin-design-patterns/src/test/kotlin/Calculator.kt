import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class Calculator {
    fun sum(a: Int, b: Int) = a + b
}

class CalculatorTest{

    @Test
    fun testSum(){
        val calculator = Calculator()
        Assertions.assertThat(calculator.sum(1, 2))
            .isEqualTo(3)
    }
}