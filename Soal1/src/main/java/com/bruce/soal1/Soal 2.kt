package com.bruce.soal1
import kotlin.random.Random

class Wizard(var name: String) {
    var hp = 50
    var maxHp = 50
    var mana = 30
    var maxMana = 30
    var kills = 0
    var manaPotions = 5
    var healthPotions = 5
    var isStrong = false
    var lifesteal = 0

    fun evolveIfReady() {
        if (!isStrong && kills >= 5) {
            isStrong = true
            maxHp = (maxHp * 1.5).toInt()
            maxMana = (maxMana * 1.5).toInt()
            hp = maxHp
            mana = maxMana
            lifesteal = 1
            println("\n*** $name telah berevolusi menjadi Strong Wizard! ***")
            println("HP dan Mana meningkat 1.5x, dan kamu mendapatkan stat Lifesteal!\n")
        }
    }

    fun drinkManaPotion() {
        if (manaPotions <= 0) {
            println("Kamu tidak punya Mana Potion lagi.\n")
            return
        }
        manaPotions--
        mana = minOf(mana + 15, maxMana)
        println("$name meminum Mana Potion. Mana sekarang: $mana/$maxMana\n")
    }

    fun drinkHealthPotion() {
        if (healthPotions <= 0) {
            println("Kamu tidak punya Health Potion lagi.\n")
            return
        }
        healthPotions--
        hp = minOf(hp + 25, maxHp)
        println("$name meminum Health Potion. HP sekarang: $hp/$maxHp\n")
    }

    fun printStats() {
        println("\n------- $name's STATS -------")
        println("HP: $hp/$maxHp")
        println("Mana: $mana/$maxMana")
        println("Kills needed to evolve: ${minOf(kills, 5)}/5")
        println("Mana Potions held: $manaPotions")
        println("Health Potions held: $healthPotions")
        if (isStrong) println("Lifesteal: $lifesteal")
        println("------------------------------\n")
    }

    fun isAlive(): Boolean = hp > 0
}

class Enemy(val type: String) {
    val name = "${type}mon"
    var hp = 30
    val maxHp = 30
}

fun main() {
    while (true) {
        playGame()
    }
}

fun playGame() {
    println("What's your name?")
    var name = readLine()
    while (name.isNullOrBlank()) {
        println("Nama tidak boleh kosong. Masukkan nama:")
        name = readLine()
    }

    val wizard = Wizard(name)
    println("Good luck, ${wizard.name}! You're gonna need it!\n")

    mainMenu(wizard)
    println("${wizard.name} telah gugur... memulai ulang permainan!\n")
}

fun mainMenu(wizard: Wizard) {
    while (wizard.isAlive()) {
        println("What're you going to do?")
        println("1. View Stats")
        println("2. Enter battle")
        print("Pilihan: ")

        when (readLine()?.toIntOrNull()) {
            1 -> statsMenu(wizard)
            2 -> enterBattle(wizard)
            else -> println("Pilihan tidak valid.\n")
        }
    }
}

fun statsMenu(wizard: Wizard) {
    while (true) {
        wizard.printStats()
        println("a. Drink Mana Potion")
        println("b. Drink Health Potion")
        println("c. Rename self")
        println("d. Back")
        print("Pilihan: ")

        when (readLine()?.lowercase()) {
            "a" -> wizard.drinkManaPotion()
            "b" -> wizard.drinkHealthPotion()
            "c" -> {
                print("Nama baru: ")
                val newName = readLine()
                if (!newName.isNullOrBlank()) {
                    wizard.name = newName
                    println("Nama berhasil diubah menjadi $newName.\n")
                } else {
                    println("Nama tidak boleh kosong.\n")
                }
            }
            "d" -> return
            else -> println("Pilihan tidak valid.\n")
        }
    }
}

fun enterBattle(wizard: Wizard) {
    val types = listOf("Fire", "Water", "Grass")
    val enemy = Enemy(types[Random.nextInt(types.size)])
    println("Seekor ${enemy.name} muncul!\n")

    while (wizard.isAlive() && enemy.hp > 0) {
        printBattleStatus(wizard, enemy)
        println("a. Fire Attack")
        println("b. Water Attack")
        println("c. Grass Attack")
        println("d. Drink potion")
        println("e. Run")
        print("Pilihan: ")

        val action = readLine()?.lowercase()
        var turnUsed = true

        when (action) {
            "a" -> playerAttack(wizard, enemy, "Fire")
            "b" -> playerAttack(wizard, enemy, "Water")
            "c" -> playerAttack(wizard, enemy, "Grass")
            "d" -> battlePotionMenu(wizard)
            "e" -> {
                println("${wizard.name} melarikan diri dari pertarungan!\n")
                return
            }
            else -> {
                println("Pilihan tidak valid.\n")
                turnUsed = false
            }
        }

        if (!turnUsed) continue

        if (enemy.hp <= 0) {
            println("${enemy.name} berhasil dikalahkan!\n")
            wizard.kills++
            wizard.evolveIfReady()
            return
        }

        enemyAttack(wizard, enemy)
    }
}

fun battlePotionMenu(wizard: Wizard) {
    println("1. Mana Potion")
    println("2. Health Potion")
    print("Pilihan: ")
    when (readLine()?.toIntOrNull()) {
        1 -> wizard.drinkManaPotion()
        2 -> wizard.drinkHealthPotion()
        else -> println("Pilihan tidak valid, giliran tetap terpakai.\n")
    }
}

fun playerAttack(wizard: Wizard, enemy: Enemy, spellType: String) {
    if (wizard.mana < 10) {
        println("Mana tidak cukup untuk menyerang!\n")
        return
    }
    wizard.mana -= 10

    var damage = 10
    val isEffective = (spellType == "Fire" && enemy.type == "Grass") ||
            (spellType == "Water" && enemy.type == "Fire") ||
            (spellType == "Grass" && enemy.type == "Water")

    if (isEffective) damage *= 2
    if (wizard.isStrong) damage = (damage * 1.5).toInt()

    enemy.hp -= damage
    println("${wizard.name} menggunakan $spellType Attack! Damage: $damage${if (isEffective) " (Super Effective!)" else ""}")

    if (wizard.isStrong && enemy.hp > 0) {
        wizard.hp = minOf(wizard.hp + wizard.lifesteal, wizard.maxHp)
        println("${wizard.name} menyerap ${wizard.lifesteal} HP melalui Lifesteal!")
    }
    println()
}

fun enemyAttack(wizard: Wizard, enemy: Enemy) {
    if (enemy.hp <= 0) return
    wizard.hp -= 10
    println("${enemy.name} menyerang balik! ${wizard.name} kehilangan 10 HP.\n")
}

fun printBattleStatus(wizard: Wizard, enemy: Enemy) {
    println("--------- BATTLE ---------")
    println(wizard.name)
    println("HP: ${wizard.hp}/${wizard.maxHp}")
    println("Mana: ${wizard.mana}/${wizard.maxMana}")
    println("HP Potions: ${wizard.healthPotions}")
    println("MP Potions: ${wizard.manaPotions}")
    println()
    println(enemy.name)
    println("HP: ${enemy.hp}/${enemy.maxHp}")
    println("Type: ${enemy.type}")
    println("---------------------------")
}