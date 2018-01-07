package net.corda.testing.driver

import net.corda.core.internal.concurrent.transpose
import net.corda.core.utilities.getOrThrow
import net.corda.testing.ALICE_NAME
import net.corda.testing.BOB_NAME
import net.corda.testing.CHARLIE_NAME
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

//@RunWith(Parameterized::class)
class DriverPerformanceTest {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "in-process = {0}")
        fun data() = listOf(true, false)
    }

    @Parameterized.Parameter
    @JvmField
    var inProcess: Boolean = true

    @Test
    fun nothing() {
        driver(startNodesInProcess = inProcess, notarySpecs = emptyList()) { }
    }

    @Test
    fun notary() {
        driver(startNodesInProcess = inProcess) { }
    }

    @Test
    fun alice() {
        driver(startNodesInProcess = inProcess, notarySpecs = emptyList()) {
            startNode(providedName = ALICE_NAME).getOrThrow()
        }
    }

    @Test
    fun `in twos`() {
        driver(startNodesInProcess = inProcess, notarySpecs = emptyList()) {
            (1..10).map {
                listOf(
                        startNode(),
                        startNode()
                ).transpose().getOrThrow().map { it.stop() }
            }
        }
    }

    @Test
    fun `in threes`() {
        driver(startNodesInProcess = inProcess, notarySpecs = emptyList()) {
            (1..10).map {
                listOf(
                        startNode(),
                        startNode(),
                        startNode()
                ).transpose().getOrThrow().map { it.stop() }
            }
        }
    }

    @Test
    fun `alice and notary in parallel`() {
        driver(startNodesInProcess = inProcess) {
            listOf(
                    startNode(providedName = ALICE_NAME),
                    defaultNotaryNode
            ).transpose().getOrThrow()
        }
    }

    @Test
    fun `alice and notary in sequence`() {
        driver(startNodesInProcess = inProcess) {
            defaultNotaryNode.getOrThrow()
            startNode(providedName = ALICE_NAME).getOrThrow()
        }
    }

    @Test
    fun `alice, bob and notary in parallel`() {
        driver(startNodesInProcess = inProcess) {
            listOf(
                    startNode(providedName = ALICE_NAME),
                    startNode(providedName = BOB_NAME),
                    defaultNotaryNode
            ).transpose().getOrThrow()
        }
    }

    @Test
    fun `alice, bob and notary in sequence`() {
        driver(startNodesInProcess = inProcess) {
            defaultNotaryNode.getOrThrow()
            startNode(providedName = ALICE_NAME).getOrThrow()
            startNode(providedName = BOB_NAME).getOrThrow()
        }
    }

    @Test
    fun `alice, bob, charlie and notary in parallel`() {
        driver(startNodesInProcess = inProcess) {
            listOf(
                    startNode(providedName = ALICE_NAME),
                    startNode(providedName = BOB_NAME),
                    startNode(providedName = CHARLIE_NAME),
                    defaultNotaryNode
            ).transpose().getOrThrow()
        }
    }

    @Test
    fun `alice, bob, charlie and notary in sequence`() {
        driver(startNodesInProcess = inProcess) {
            defaultNotaryNode.getOrThrow()
            startNode(providedName = ALICE_NAME).getOrThrow()
            startNode(providedName = BOB_NAME).getOrThrow()
            startNode(providedName = CHARLIE_NAME).getOrThrow()
        }
    }
}