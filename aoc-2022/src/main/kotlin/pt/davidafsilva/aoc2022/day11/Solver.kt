package pt.davidafsilva.aoc2022.day11

import net.objecthunter.exp4j.ExpressionBuilder
import pt.davidafsilva.aoc2022.scanInput
import java.util.LinkedList
import java.util.Queue

fun main() {
    val monkeys = loadMonkeys()

    for (round in 1..10_000) {
        for (monkey in monkeys) monkeyInspection(monkeys, monkey)
    }

    val monkeyBusinessLevel = monkeys.sortedByDescending(Monkey::itemsInspected)
        .take(2)
        .fold(1L) { acc, m -> m.itemsInspected * acc }
    println("1st/2nd part: $monkeyBusinessLevel")
}

private fun monkeyInspection(
    monkeys: List<Monkey>,
    monkey: Monkey,
) {
    while (monkey.items.isNotEmpty()) {
        // inspect item
        val item = monkey.items.poll()
        item.worryLevel = monkey.worryLevelFn(item.worryLevel)
        monkey.itemsInspected++

        // relief (part 1 only): divide by 3
        //item.worryLevel = floor(item.worryLevel / 3.0).toInt()

        // relief (part 2 only): wrap around the least common multiple
        item.worryLevel = item.worryLevel % monkeys
            .fold(1L) { acc, m -> m.conditionalThrow.divisor * acc }

        // throw item
        monkey.conditionalThrow.exec(item)
    }
}

private fun loadMonkeys(): List<Monkey> {
    val monkeys = mutableListOf<Monkey>()

    val scanner = scanInput(day = 11)
    while (scanner.hasNext()) {
        // Monkey <idx>:
        scanner.nextLine()

        // Starting items:
        val items = scanner.nextLine()
            .substring("  Starting items: ".length)
            .splitToSequence(",")
            .map { Item(worryLevel = it.trim().toLong()) }
            .toCollection(LinkedList())

        // Operation: new =
        val worryLevelFn = scanner.nextLine()
            .substring("  Operation: new = ".length)
            .let { expression ->
                { wl: Long -> computeWorryLevel(expression, wl) }
            }

        //  Test: divisible by 2
        val divisor = scanner.nextLine()
            .substring("  Test: divisible by ".length)
            .toInt()
        val conditionalThrow = ConditionalThrow(
            divisor = divisor,
            //    If true: throw to monkey 0
            trueBranch = computeThrowBranch(
                description = scanner.nextLine(),
                prefix = "    If true: throw to monkey ",
                monkeys = monkeys,
            ),
            //    If false: throw to monkey 7
            falseBranch = computeThrowBranch(
                description = scanner.nextLine(),
                prefix = "    If false: throw to monkey ",
                monkeys = monkeys,
            )
        )

        // empty line at the end
        if (scanner.hasNext()) scanner.nextLine()

        // add the monkey
        val monkey = Monkey(items, worryLevelFn, conditionalThrow)
        monkeys.add(monkey)
    }

    return monkeys
}

private fun computeWorryLevel(expression: String, currentWorry: Long): Long {
    val expr = ExpressionBuilder(expression)
        .variables("old")
        .build()
        .setVariable("old", currentWorry.toDouble())
    return expr.evaluate().toLong()
}

private fun computeThrowBranch(
    description: String,
    prefix: String,
    monkeys: List<Monkey>,
) = description.substring(prefix.length)
    .toInt()
    .let { targetMonkeyIdx ->
        { item: Item ->
            val target = monkeys[targetMonkeyIdx]
            target.items.offer(item)
            Unit
        }
    }

private class Item(
    var worryLevel: Long,
)

private class Monkey(
    val items: Queue<Item>,
    val worryLevelFn: (Long) -> Long,
    val conditionalThrow: ConditionalThrow,
    var itemsInspected: Long = 0,
)

private class ConditionalThrow(
    val divisor: Int,
    private val trueBranch: (Item) -> Unit,
    private val falseBranch: (Item) -> Unit,
) {
    fun exec(item: Item) = when {
        item.worryLevel.mod(divisor) == 0 -> trueBranch(item)
        else -> falseBranch(item)
    }
}
