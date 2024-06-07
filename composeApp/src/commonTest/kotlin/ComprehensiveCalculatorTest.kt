import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ComprehensiveCalculatorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val calculator = ComprehensiveCalculator()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `add integers two plus two returns four`() {
        // Arrange
        val a = 2
        val b = 2
        val expected = 4

        // Act
        val actual = calculator.addIntegers(a, b)

        // Assert
        assertEquals(expected, actual, "Sum should be 4")
    }

    @Test
    fun `add floats two point five plus three point five returns six`() {
        // Arrange
        val a = 2.5f
        val b = 3.5f
        val expected = 6.0f

        // Act
        val actual = calculator.addFloats(a, b)

        // Assert
        assertEquals(expected, actual, 0.0001f, "Sum should be 6.0")
    }

    @Test
    fun `increment state once increments by one`() {
        // Arrange
        calculator.incrementState()
        val expected = 1

        // Act
        val actual = calculator.getState()

        // Assert
        assertEquals(expected, actual, "State should be incremented by 1")
    }

    @Test
    fun `fetch result async returns expected result`() = runBlocking {
        // Arrange
        val expected = 42

        // Act
        val actual = calculator.fetchResultAsync()

        // Assert
        assertEquals(expected, actual, "Result should be 42")
    }

    @Test
    fun `fetch result flow returns expected flow`() = runBlocking {
        // Arrange
        val expected = listOf(1, 2, 3)

        // Act
        val actual = calculator.fetchResultFlow().toList()

        // Assert
        assertContentEquals(expected, actual, "Flow should emit 1, 2, 3")
    }

    @Test
    fun `update state flow updates state flow value`() = testScope.runTest {
        // Arrange
        val newValue = 5
        val expected = 5

        // Act
        val deferredResult = async { calculator.updateStateFlow(newValue) }
        advanceTimeBy(500L)
        deferredResult.await()
        val actual = calculator.stateFlow.value

        // Assert
        assertEquals(expected, actual, "StateFlow value should be updated to 5")
    }

    @Test
    fun `update state flow with advanceTimeBy updates state flow value`() = runTest {
        // Arrange
        val newValue = 5
        val expected = 5

        // Act
        advanceTimeBy(500L)
        calculator.updateStateFlow(newValue)
        val actual = calculator.stateFlow.value

        // Assert
        assertEquals(expected, actual, "StateFlow value should be updated to 5")
    }
}