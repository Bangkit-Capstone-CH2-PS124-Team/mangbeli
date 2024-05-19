# MangBeli App

## Overview
MangBeli App is a mobile application designed to improve the experience of users who frequently purchase from street vendors. The app provides accurate arrival time estimates and real-time notifications for street vendors based on their location. This innovation aims to enhance users' productivity and quality of life by minimizing the waiting time and uncertainty associated with street vendors' arrival times.

## Features
- **Estimated Time of Arrival (ETA):** Provides accurate arrival time estimates based on the street vendor's current location.
- **Real-Time Location Tracking:** Utilizes location tracking technology to update the street vendor's position in real-time.
- **Proximity Notifications:** Sends notifications to users when a vendor is within a certain distance from their location.
- **Route Optimization for Vendors:** Helps street vendors optimize their routes for better productivity.
- **User-Friendly Interface:** Easy-to-use interface for seamless interaction.

## Getting Started
### Prerequisites
- Android Studio
- Google Maps API Key
- Firebase for real-time notifications and user authentication
- Backend API (available at [MangBeli API Repository](https://github.com/Bangkit-Capstone-CH2-PS124-Team/mangbeli-api))
- API Documentation (available at [MangBeli API Documentation](https://bangkit-capstone-ch2-ps124-team.github.io/mangbeli-api-doc/#/))

### Installation
1. Clone the repository:
    ```bash
    git clone https://github.com/Bangkit-Capstone-CH2-PS124-Team/mangbeli.git
    ```
2. Open the project in Android Studio.
3. Obtain a Google Maps API key from the [Google Cloud Console](https://console.cloud.google.com/) and enable the Directions API.
4. Add the API key to your `local.properties` file:
    ```properties
    MAPS_API_KEY=YOUR_API_KEY
    ```
5. Sync the project to ensure all dependencies are properly installed.
6. Configure the backend API by following the instructions in the [MangBeli API Repository](https://github.com/Bangkit-Capstone-CH2-PS124-Team/mangbeli-api).

### Usage
1. Run the application on your Android device or emulator.
2. Register or log in to the application.
3. Allow location permissions for the application to track your current location.
4. View nearby street vendors and their estimated arrival times.
5. Receive notifications when vendors are approaching your location.

## Architecture
The MangBeli App follows the MVVM (Model-View-ViewModel) architecture to ensure a clean separation of concerns and to facilitate easier testing and maintenance.

## API Integration
The application integrates with Google Maps Directions API to provide route and ETA information. It also uses a custom backend API for managing vendor data and user interactions. Ensure you have a valid API key and have enabled the necessary APIs in your Google Cloud Console.

For more details on the backend API, refer to the [MangBeli API Repository](https://github.com/Bangkit-Capstone-CH2-PS124-Team/mangbeli-api) and the [API Documentation](https://bangkit-capstone-ch2-ps124-team.github.io/mangbeli-api-doc/#/).

## Contributing
We welcome contributions from the community. If you wish to contribute, please follow these steps:
1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature-name`).
3. Make your changes.
4. Commit your changes (`git commit -m 'Add some feature'`).
5. Push to the branch (`git push origin feature/your-feature-name`).
6. Open a pull request.

### Download

[MangBeli_v1.0.0-alpha.apk](https://github.com/Bangkit-Capstone-CH2-PS124-Team/mangbeli/releases/tag/v1.0.0-alpha)

Thank you for using MangBeli App! We hope this app enhances your experience with street vendors and helps you save time.

## License
This project is licensed under the MIT License. See the [LICENSE](https://github.com/Bangkit-Capstone-CH2-PS124-Team/mangbeli#MIT-1-ov-file) file for details.

## Contact
For any inquiries or issues, please contact us at raafihilmi90@gmail.com.
