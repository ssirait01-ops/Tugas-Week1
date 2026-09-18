package com.bruce.soal1

import kotlin.system.exitProcess

class MenuItem(var name: String, var description: String, var price: Double)

class OrderItem(val menuItem: MenuItem, val quantity: Int) {
    val subtotal: Double
        get() = menuItem.price * quantity
}

class Order(val customerName: String, val items: MutableList<OrderItem>) {
    val total: Double
        get() {
            var sum = 0.0
            for (item in items) {
                sum += item.subtotal
            }
            return sum
        }
}

val menu = mutableListOf<MenuItem>()
val orders = mutableListOf<Order>()

fun main() {
    // seed sample data supaya menu tidak kosong saat pertama dijalankan
    menu.add(MenuItem("Nasi Goreng", "Indonesian fried rice", 25000.0))
    menu.add(MenuItem("Es Teh", "Iced tea", 5000.0))

    while (true) {
        printMenuOptions()
        val choice = readLine()?.toIntOrNull()

        if (choice == null) {
            println("Input tidak valid. Masukkan angka 1-7.\n")
            continue
        }

        when (choice) {
            1 -> makeOrder()
            2 -> viewOrders()
            3 -> viewMenu()
            4 -> addMenu()
            5 -> editMenu()
            6 -> deleteMenu()
            7 -> {
                println("Terima kasih, sampai jumpa!")
                exitProcess(0)
            }
            else -> println("Pilihan tidak tersedia. Masukkan angka 1-7.\n")
        }
    }
}

fun printMenuOptions() {
    println("========== ORDER SYSTEM ==========")
    println("1. Make order")
    println("2. View Orders")
    println("3. View Menu")
    println("4. Add Menu")
    println("5. Edit Menu")
    println("6. Delete Menu")
    println("7. Exit")
    print("Pilih menu: ")
}

fun viewMenu() {
    println("\n--------- MENU ---------")
    if (menu.isEmpty()) {
        println("Menu masih kosong.")
    } else {
        for (i in menu.indices) {
            val item = menu[i]
            println("${i + 1}. ${item.name} - ${item.description} - Rp${item.price}")
        }
    }
    println("-------------------------\n")
}

fun addMenu() {
    print("Nama makanan: ")
    val name = readLine()
    if (name.isNullOrBlank()) {
        println("Nama tidak boleh kosong.\n")
        return
    }

    print("Deskripsi: ")
    val description = readLine() ?: ""

    print("Harga: ")
    val price = readLine()?.toDoubleOrNull()
    if (price == null || price < 0) {
        println("Harga tidak valid.\n")
        return
    }

    menu.add(MenuItem(name, description, price))
    println("Menu '$name' berhasil ditambahkan.\n")
}

fun editMenu() {
    viewMenu()
    if (menu.isEmpty()) return

    print("Pilih nomor menu yang ingin diedit: ")
    val index = readLine()?.toIntOrNull()

    if (index == null || index < 1 || index > menu.size) {
        println("Nomor tidak valid.\n")
        return
    }

    val item = menu[index - 1]

    print("Nama baru (kosongkan jika tidak berubah) [${item.name}]: ")
    val newName = readLine()
    if (!newName.isNullOrBlank()) item.name = newName

    print("Deskripsi baru (kosongkan jika tidak berubah) [${item.description}]: ")
    val newDescription = readLine()
    if (!newDescription.isNullOrBlank()) item.description = newDescription

    print("Harga baru (kosongkan jika tidak berubah) [${item.price}]: ")
    val newPriceInput = readLine()
    if (!newPriceInput.isNullOrBlank()) {
        val newPrice = newPriceInput.toDoubleOrNull()
        if (newPrice != null && newPrice >= 0) {
            item.price = newPrice
        } else {
            println("Harga baru tidak valid, harga lama dipertahankan.")
        }
    }

    println("Menu berhasil diperbarui.\n")
}

fun deleteMenu() {
    viewMenu()
    if (menu.isEmpty()) return

    print("Pilih nomor menu yang ingin dihapus: ")
    val index = readLine()?.toIntOrNull()

    if (index == null || index < 1 || index > menu.size) {
        println("Nomor tidak valid.\n")
        return
    }

    val removed = menu.removeAt(index - 1)
    println("Menu '${removed.name}' berhasil dihapus.\n")
}

fun makeOrder() {
    if (menu.isEmpty()) {
        println("Menu masih kosong, tidak dapat membuat order.\n")
        return
    }

    print("Nama pelanggan: ")
    val customerName = readLine()
    if (customerName.isNullOrBlank()) {
        println("Nama pelanggan tidak boleh kosong.\n")
        return
    }

    val orderItems = mutableListOf<OrderItem>()

    while (true) {
        viewMenu()
        print("Pilih nomor menu (0 untuk selesai): ")
        val index = readLine()?.toIntOrNull()

        if (index == null) {
            println("Input tidak valid.\n")
            continue
        }
        if (index == 0) break
        if (index < 1 || index > menu.size) {
            println("Nomor menu tidak tersedia.\n")
            continue
        }

        print("Jumlah: ")
        val qty = readLine()?.toIntOrNull()
        if (qty == null || qty <= 0) {
            println("Jumlah tidak valid.\n")
            continue
        }

        orderItems.add(OrderItem(menu[index - 1], qty))
        println("${menu[index - 1].name} x$qty ditambahkan ke order.\n")
    }

    if (orderItems.isEmpty()) {
        println("Order dibatalkan karena tidak ada item.\n")
        return
    }

    val order = Order(customerName, orderItems)
    orders.add(order)
    printReceipt(order)
}

fun printReceipt(order: Order) {
    println("\n------- ${order.customerName}'s ORDER -------")
    for (i in order.items.indices) {
        val item = order.items[i]
        println("${i + 1}. ${item.menuItem.name} x${item.quantity}\tRp${item.subtotal}")
    }
    println("--------------------------------------")
    println("TOTAL\tRp${order.total}\n")
}

fun viewOrders() {
    if (orders.isEmpty()) {
        println("Belum ada order.\n")
        return
    }
    for (order in orders) {
        printReceipt(order)
    }
}