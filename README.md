# Kotlin Multiplatform Testing Guide

## Introduction

Welcome to the Kotlin Multiplatform Testing Guide! This repository provides a comprehensive guide on setting up and
writing tests for Kotlin Multiplatform projects. Whether you're just starting with unit tests or looking to dive into
advanced testing scenarios, this guide has you covered.

For a detailed video walkthrough of this repository, check out
my [YouTube video](https://www.youtube.com/watch?v=your-video-id).

---

## Basics

### Setting Up a Unit Test Environment

Setting up a unit test environment is the first step towards writing effective tests. Here’s a quick guide to get you
started:

1. **Adding Dependencies**:

- In a Kotlin Multiplatform project, you need to add the required dependencies in your `build.gradle.kts` file:

```kotlin
// ...

kotlin {
    // ...

    sourceSets {
        // ...
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
```

Let’s break down what `kotlin("test")` includes and the purpose of each component:

- **org.jetbrains.kotlin:kotlin-test**: Core Kotlin testing library with common test assertions and utilities.
    - **Example**: `assertEquals(expected, actual)` to verify that two values are equal.
- **org.jetbrains.kotlin:kotlin-test-junit**: Adds JUnit integration for Kotlin tests, providing compatibility with
  JUnit 4.
    - **Example**: Using `@Test` annotations from JUnit to mark your test functions.
- **org.jetbrains.kotlin:kotlin-test-common**: Provides common test annotations and utilities that are
  platform-independent.
    - **Example**: Using `@BeforeTest` to set up preconditions before each test.
- **org.jetbrains.kotlin:kotlin-test-annotations-common**: Provides common test annotations like `@Test`, `@BeforeTest`,
  and `@AfterTest`.
    - **Example**: Annotating a function with `@Test` to indicate it is a test case.

2. **Creating the Test Directory**:

- Create a directory structure for your tests. In your project, create the folder `composeApp/src/commonTest/kotlin`.

```sh
mkdir -p composeApp/src/commonTest/kotlin
```

### Writing Your First Tests

---

### Sample Class

Start with a sample class, `ComprehensiveCalculator`, containing methods that we will be testing.

```kotlin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

class ComprehensiveCalculator {

    private var internalState: Int = 0

    private val _stateFlow: MutableStateFlow<Int> = MutableStateFlow(0)
    val stateFlow: StateFlow<Int> = _stateFlow

    fun addIntegers(a: Int, b: Int): Int {
        return a + b
    }

    fun addFloats(a: Float, b: Float): Float {
        return a + b
    }

    fun incrementState() {
        internalState += 1
    }

    fun getState(): Int {
        return internalState
    }

    suspend fun fetchResultAsync(): Int {
        delay(1000L)  // Simulate a long-running operation
        return 42
    }

    fun fetchResultFlow(): Flow<Int> = flow {
        emit(1)
        emit(2)
        emit(3)
    }

    suspend fun updateStateFlow(newValue: Int) {
        delay(500L)  // Simulate a delay
        _stateFlow.value = newValue
    }
}
```

Since the `ComprehensiveCalculator` class has an internal state, create an instance of it as a `private val` in the test
class:

```kotlin
private val calculator = ComprehensiveCalculator()
```

If the class had internal states that needed to be reset before each test, use the `@BeforeTest` annotation to
initialize it.

Now, let's add tests for this class, one by one, explaining what each test does.

---

### Test 1: Verifying Addition with `assertEquals`

```kotlin
import kotlin.test.Test
import kotlin.test.assertEquals

class ComprehensiveCalculatorTest {

    private val calculator = ComprehensiveCalculator()

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
}
```

**Explanation**:

- Verifies that the `addIntegers` method returns the correct sum. Uses `assertEquals` to check if the sum of 2 and 2 is
  equal to 4.

### Test 2: Verifying Float Addition with `assertEquals`

```kotlin
import kotlin.test.Test
import kotlin.test.assertEquals

class ComprehensiveCalculatorTest {

    private val calculator = ComprehensiveCalculator()

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
}
```

**Explanation**:

- Verifies that the `addFloats` method returns the correct sum of two floats. Uses `assertEquals` with a tolerance
  parameter to account for potential floating-point precision issues.

### Test 3: Incrementing Internal State

```kotlin
import kotlin.test.Test
import kotlin.test.assertEquals

class ComprehensiveCalculatorTest {

    private val calculator = ComprehensiveCalculator()

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
}
```

**Explanation**:

- Verifies that the `incrementState` method correctly increments the internal state by 1. Uses `assertEquals` to check
  the state.

### Test 4: Fetching Asynchronous Result

```kotlin
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class ComprehensiveCalculatorTest {

    private val calculator = ComprehensiveCalculator()

    @Test
    fun `fetch result async returns expected result`() = runBlocking {
        // Arrange
        val expected = 42

        // Act
        val actual = calculator.fetchResultAsync()

        // Assert
        assertEquals(expected, actual, "Result should be 42")
    }
}
```

**Explanation**:

- Verifies the `fetchResultAsync` suspended function. Uses `runBlocking` to run the coroutine and `assertEquals` to
  check the result.

### Test 5: Fetching Result Flow

```kotlin
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertContentEquals

class ComprehensiveCalculatorTest {

    private val calculator = ComprehensiveCalculator()

    @Test
    fun `fetch result flow returns expected flow`() = runBlocking {
        // Arrange
        val expected = listOf(1, 2, 3)

        // Act
        val actual = calculator.fetchResultFlow().toList()

        // Assert
        assertContentEquals(expected, actual, "Flow should emit 1, 2, 3")
    }
}
```

**Explanation**:

- Verifies the `fetchResultFlow` method, which returns a Flow. Uses `runBlocking` to collect the flow into a list
  and `assertContentEquals` to compare the emitted values.

### Test 6: Updating State Flow

```kotlin
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runBlockingTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ComprehensiveCalculatorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val calculator = ComprehensiveCalculator()

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
```

**Explanation**:

- **Test Function:** `update state flow updates state flow value`
- **Usage:** Uses `runTest` within a custom `testScope`, starts the execution of `calculator.updateStateFlow(newValue)`
  asynchronously, then advances the time, and finally awaits the result.
- **When to Use:** Suitable for testing suspending functions or complex asynchronous operations.
- **Explanation

:** Verifies that the `updateStateFlow` method correctly updates the `StateFlow` value after a delay.
Uses `advanceTimeBy` to simulate the passage of time.

### Summary:

- **`update state flow updates state flow value`** is simpler and suitable for straightforward tests without complex
  asynchronous behavior.
- **`update state flow with advanceTimeBy updates state flow value`** is more complex and suitable for tests involving
  suspending functions or advanced asynchronous operations.

---

## Adding Test Coverage

To ensure that your code is well-tested, it's essential to measure test coverage. We will be
using [Kover](https://github.com/Kotlin/kotlinx-kover) for this purpose. Kover is a Kotlin Multiplatform code coverage
plugin.

### Setting Up Kover

1. **Add the Kover Plugin**:

- In your `build.gradle.kts` file, add the Kover plugin:

```kotlin
plugins {
    // ...

    id("org.jetbrains.kotlinx.kover") version "0.8.0"
}
```

2. **Configure Kover**:

- Add the following configuration to set up Kover reports and coverage rules:

```kotlin
kover {
    reports {
    filters {
        excludes {
        // Entry Points
        classes("MainKt") // Desktop
        classes("*.MainActivity") // Android

        // Generated Classes & Resources
        packages("*.generated.*")

        // Dependency Injection
        packages("*di*")

        // Compose Related
        classes("*ComposableSingletons*")
        annotatedBy("androidx.compose.runtime.Composable")
        }
    }

    verify {
        rule {
        minBound(80)
        }
    }
    }
}
```

### Explanation

- **Filters**: Excludes certain classes and packages from coverage reports to focus on the most relevant parts of your
  codebase.
- **Entry Points**: Exclude main entry point classes for different platforms.
- **Generated Classes & Resources**: Exclude generated code and resources.
- **Dependency Injection**: Exclude DI-related packages.
- **Compose Related**: Exclude Compose-related classes and annotations.
- **Verify**: Sets a minimum coverage threshold of 80%. This ensures that at least 80% of your code is covered by tests.

By adding and configuring Kover, you can effectively measure and enforce test coverage in your Kotlin Multiplatform
project.