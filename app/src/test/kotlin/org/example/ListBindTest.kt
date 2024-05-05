package org.example

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertContentEquals

class ListBindTest {
    private val list1 = listOf(1, 2, 3)
    private val list2 = listOf("a", "b")

    @Test fun testListBind() = runBlocking {
        /*
         * Prints the following:
         *
         * > Before bind
         * >> After first bind. Value one is '1'
         * >>> After second bind. Value one is '1', value two is 'a'
         * >>> After second bind. Value one is '1', value two is 'b'
         * >> After first bind. Value one is '2'
         * >>> After second bind. Value one is '2', value two is 'a'
         * >>> After second bind. Value one is '2', value two is 'b'
         * >> After first bind. Value one is '3'
         * >>> After second bind. Value one is '3', value two is 'a'
         * >>> After second bind. Value one is '3', value two is 'b'
         */
        val result = list {
            println("> Before bind")
            val one = list1.bind()
            println(">> After first bind. Value one is '$one'")
            val two = list2.bind()
            println(">>> After second bind. Value one fis '$one', value two is '$two'")
            one to two
        }

        assertContentEquals(listOf(1 to "a", 1 to "b", 2 to "a", 2 to "b", 3 to "a", 3 to "b"), result)
    }

    @Test fun testShiftReset() = runBlocking {

        /*
         * Prints the following:
         *
         * > Before bind
         * >> After first bind. Value one is '1'
         * >>> After second bind. Value one is '1', value two is 'a'
         * >>> After second bind. Value one is '1', value two is 'b'
         * >> After first bind. Value one is '2'
         * >>> After second bind. Value one is '2', value two is 'a'
         * >>> After second bind. Value one is '2', value two is 'b'
         * >> After first bind. Value one is '3'
         * >>> After second bind. Value one is '3', value two is 'a'
         * >>> After second bind. Value one is '3', value two is 'b'
         */
        val result = reset<List<Pair<Int, String>>, List<Pair<Int, String>>> {

            println("> Before bind")
            val one = shift { cont1 -> list1.flatMap { cont1(it) } }
            println(">> After first bind. Value one is '$one'")

            reset {
                val two = shift { cont2 -> list2.map { cont2(it) } }
                println(">>> After second bind. Value one is '$one', value two is '$two'")
                one to two
            }
        }

        assertContentEquals(listOf(1 to "a", 1 to "b", 2 to "a", 2 to "b", 3 to "a", 3 to "b"), result)
    }
}
