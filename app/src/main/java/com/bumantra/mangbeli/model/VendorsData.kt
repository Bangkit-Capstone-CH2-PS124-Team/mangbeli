package com.bumantra.mangbeli.model

object VendorsData {
    private val dummyPhotoUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQBL9N1b6X0_qii4Hr6fLqNiTep23l1qXDDwA&usqp=CAU"
    val vendors = listOf(
        Vendor("1", "Vendor A", "John Doe", "5 km", dummyPhotoUrl),
        Vendor("2", "Vendor B", "Jane Doe", "10 km", dummyPhotoUrl),
        Vendor("3", "Vendor C", "Bob Smith", "2 km", dummyPhotoUrl),
        Vendor("4", "Vendor D", "Alice Johnson", "8 km", dummyPhotoUrl),
        Vendor("5", "Vendor E", "Charlie Brown", "15 km", dummyPhotoUrl),
        Vendor("6", "Vendor F", "David Miller", "3 km", dummyPhotoUrl),
        Vendor("7", "Vendor G", "Eva White", "12 km", dummyPhotoUrl),
        Vendor("8", "Vendor H", "Frank Green", "7 km", dummyPhotoUrl),
        Vendor("9", "Vendor I", "Grace Lee", "9 km", dummyPhotoUrl),
        Vendor("10", "Vendor J", "Henry Wilson", "4 km", dummyPhotoUrl)
    )
}