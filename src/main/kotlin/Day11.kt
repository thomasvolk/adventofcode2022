package net.t53k

import java.math.BigInteger

object Day11 {
    interface Operand {
        fun value(context: BigInteger): BigInteger

        companion object {
            fun parse(input: String): Operand {
                return if(input == "old") Old() else Constant(input.toBigInteger())
            }
        }
    }
    class Old: Operand {
        override fun value(context: BigInteger): BigInteger {
            return context
        }
    }
    class Constant(private val value: BigInteger): Operand {
        override fun value(context: BigInteger): BigInteger {
            return value
        }
    }

    enum class Operator {
        PLUS {
            override fun execute(left: BigInteger, right: BigInteger): BigInteger {
                return left + right
            }
        },
        MULTIPLY {
            override fun execute(left: BigInteger, right: BigInteger): BigInteger {
                return left * right
            }
        };
        abstract fun execute(left: BigInteger, right: BigInteger): BigInteger

        companion object {
            fun parse(input: String): Operator {
                return if(input == "+") PLUS else MULTIPLY
            }
        }
    }

    class Operation(private val left: Operand, private val operator: Operator, private val right: Operand) {
        fun execute(context: BigInteger): BigInteger {
            return operator.execute(left.value(context), right.value(context))
        }

        companion object {
            private val partsRe     = "([0-9a-z]+) ([*+]) ([0-9a-z]+)".toRegex()
            fun parse(input: String): Operation {
                partsRe.find(input)?.let { p ->
                    val left = Operand.parse(p.groupValues[1])
                    val right = Operand.parse(p.groupValues[3])
                    return Operation(left, Operator.parse(p.groupValues[2]), right)
                }
                throw IllegalArgumentException("inconsistent operation: $input")
            }
        }
    }

    class KeepAwayGame(private val monkeys: List<Monkey>) {
        private val monkeyMap = monkeys.associateBy { it.id }
        private val maxWorryLevel = monkeys.map { it.testDivisibleBy }.reduce{ a, b -> a * b } * BigInteger.TWO
        fun throwItem(item: BigInteger, to: Int) {
            val compressedItem = if(item > maxWorryLevel) {
                maxWorryLevel + (item % maxWorryLevel)
            }
            else {
                item
            }
            monkeyMap[to]?.receiveItem(compressedItem)
        }

        fun round() {
            monkeys.forEach { it.round(this) }
        }
    }
    class Monkey(
        val id: Int,
        inItems: List<BigInteger>,
        private val operation: Operation,
        val testDivisibleBy: BigInteger,
        private val targetMonkeyIfTestTrue: Int,
        private val targetMonkeyIfTestFalse: Int,
        private val damageLevelDivisor: BigInteger
    ) {
        private val items = inItems.toMutableList()
        private var itemsInspected: BigInteger = BigInteger.ZERO
        fun round(game: KeepAwayGame) {
            items.forEach {currentLevel ->
                val newWorryLevel = operation.execute(currentLevel) / damageLevelDivisor
                val divisible = (newWorryLevel % testDivisibleBy) == BigInteger.ZERO
                if(divisible) {
                    game.throwItem(newWorryLevel, targetMonkeyIfTestTrue)
                }
                else {
                    game.throwItem(newWorryLevel, targetMonkeyIfTestFalse)
                }
            }
            itemsInspected += items.count().toBigInteger()
            items.clear()
        }

        fun itemsInspected() = itemsInspected

        fun receiveItem(item: BigInteger) {
            items.add(item)
        }

        companion object {
            private val monkeyStartRe   = "Monkey ([0-9]+):".toRegex()
            private val startingItemsRe = "\\s\\sStarting items: ([0-9, ]+)".toRegex()
            private val operationRe     = "\\s\\sOperation: new = ([0-9a-z *+]+)".toRegex()
            private val testRe          = "\\s\\sTest: divisible by ([0-9]+)".toRegex()
            private val ifTrueRe        = "\\s\\s\\s\\sIf true: throw to monkey ([0-9]+)".toRegex()
            private val ifFalseRe       = "\\s\\s\\s\\sIf false: throw to monkey ([0-9]+)".toRegex()
            private fun create(text: String, damageLevelDivisor: BigInteger): Monkey {
                val lines = text.trim().split("\n")
                var line = 0
                monkeyStartRe.find(lines[line])?.let { start ->
                    line++
                    startingItemsRe.find(lines[line])?.let { items ->
                        line++
                        operationRe.find((lines[line]))?.let { operation ->
                            line++
                            testRe.find(lines[line])?.let {test ->
                                line++
                                ifTrueRe.find(lines[line])?.let { ifTrue ->
                                    line++
                                    ifFalseRe.find(lines[line])?.let { ifFalse ->
                                        return Monkey(
                                            start.groupValues[1].toInt(),
                                            items.groupValues[1]
                                                .split(",")
                                                .map { it.trim() }
                                                .map { it.toBigInteger() },
                                            Operation.parse(operation.groupValues[1]),
                                            test.groupValues[1].toBigInteger(),
                                            ifTrue.groupValues[1].toInt(),
                                            ifFalse.groupValues[1].toInt(),
                                            damageLevelDivisor
                                       )
                                    }
                                    throwError(line, lines)
                                }
                                throwError(line, lines)
                            }
                            throwError(line, lines)
                        }
                        throwError(line, lines)
                    }
                    throwError(line, lines)
                }
                throw IllegalArgumentException("inconsistent dataset: $text")
            }

            private fun throwError(line: Int, lines: List<String>) {
                throw IllegalArgumentException("error parsing line $line: '${lines[line]}'")
            }

            fun parse(input: String, damageLevelDivisor: BigInteger): List<Monkey> {
                return input.split("\n\n").map { create(it, damageLevelDivisor) }
            }
        }
    }
}