package com.bumantra.mangbeli.model

object VendorsData {
    private val dummyPhotoUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQBL9N1b6X0_qii4Hr6fLqNiTep23l1qXDDwA&usqp=CAU"
    val vendors = listOf(
        Vendor("1", "Vendor A", "John Doe", "5 km", dummyPhotoUrl, listOf("Product1", "Product2")),
        Vendor("2", "Vendor B", "Jane Doe", "10 km", dummyPhotoUrl, listOf("Product3", "Product4")),
        Vendor("3", "Vendor C", "Bob Smith", "2 km", dummyPhotoUrl, listOf("Product5", "Product6")),
        Vendor("4", "Vendor D", "Alice Johnson", "8 km", dummyPhotoUrl, listOf("Product7", "Product8")),
        Vendor("5", "Vendor E", "Charlie Brown", "15 km", dummyPhotoUrl, listOf("Product9", "Product10")),
        Vendor("6", "Vendor F", "David Miller", "3 km", dummyPhotoUrl, listOf("Product11", "Product12")),
        Vendor("7", "Vendor G", "Eva White", "12 km", dummyPhotoUrl, listOf("Product13", "Product14")),
        Vendor("8", "Vendor H", "Frank Green", "7 km", dummyPhotoUrl, listOf("Product15", "Product16")),
        Vendor("9", "Vendor I", "Grace Lee", "9 km", dummyPhotoUrl, listOf("Product17", "Product18")),
        Vendor("10", "Vendor J", "Henry Wilson", "4 km", dummyPhotoUrl, listOf("Product19", "Product20"))
    )
}