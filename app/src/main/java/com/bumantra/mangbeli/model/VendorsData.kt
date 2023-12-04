package com.bumantra.mangbeli.model

object VendorsData {
    private val dummyPhotoUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQBL9N1b6X0_qii4Hr6fLqNiTep23l1qXDDwA&usqp=CAU"
    val vendors = listOf(
        Vendor("1", "Bakso", "Mang Abdi", -6.200000, 106.816666, dummyPhotoUrl, listOf("Product1", "Product2")),
        Vendor("2", "Mie Ayam", "Mang Bobi", -6.197513, 106.812366,dummyPhotoUrl, listOf("Product3", "Product4")),
        Vendor("3", "Nasi Goreng", "Pak Udin", -6.200339, 106.821129,dummyPhotoUrl, listOf("Product5", "Product6")),
        Vendor("4", "Bubur Ayam", "Mang Polu", -6.2046825, 106.818465,dummyPhotoUrl, listOf("Product7", "Product8")),
        Vendor("5", "Es Doger", "Mas Aryo", -6.1968173, 106.822167, dummyPhotoUrl, listOf("Product9", "Product10")),
        Vendor("6", "Cendol", "Mang Didi", -6.1932751, 106.820608, dummyPhotoUrl, listOf("Product11", "Product12")),
        Vendor("7", "Bakso Pentol", "Pak Suryo", -6.1909212, 106.817351, dummyPhotoUrl, listOf("Product13", "Product14")),
        Vendor("8", "Mie Ayam", "Mas Tarjo", -6.1933398, 106.81656, dummyPhotoUrl, listOf("Product15", "Product16")),
        Vendor("9", "Bakso Ikan", "Mang Uus", -6.195447, 106.815751, dummyPhotoUrl, listOf("Product17", "Product18")),
        Vendor("10", "Rujakeun", "Mang Juned", -6.2003411, 106.824473, dummyPhotoUrl, listOf("Product19", "Product20"))
    )
}