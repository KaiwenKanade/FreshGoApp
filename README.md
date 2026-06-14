# FreshGoApp - Digital Kitchen Inventory & Recipe Manager

Aplikasi manajemen inventaris dapur untuk mengelola stok bahan makanan dan menemukan inspirasi resep masakan secara praktis.

## Screenshot Aplikasi
| Preview | Login | HomeScreen |
| :---: | :---: | :---: |
| <img width="300" src="https://github.com/user-attachments/assets/f13d1628-b7fa-423e-9f08-28abf2f0fac1" /> | <img width="300" src="https://github.com/user-attachments/assets/4b3a0175-7ae7-458a-b67a-3dafd4e7167e" /> | <img width="300" src="https://github.com/user-attachments/assets/eaef8afc-23e7-4a74-973a-9e24d9f46e54" /> |

| AddItem | Recipe List | Recipe Detail |
| :---: | :---: | :---: |
| <img width="300" src="https://github.com/user-attachments/assets/de72b3c6-4baa-423d-9aae-f3031befb52b" /> | <img width="300" src="https://github.com/user-attachments/assets/051d92d9-a741-4373-8f44-14932fe4686b" /> | <img width="300" src="https://github.com/user-attachments/assets/351bfdd9-0461-4a23-8ae0-29aee7908f54" /> |

## Fitur Utama
* **Inventory Management:** CRUD (Browse, Read, Edit, Add, Delete) data bahan makanan.
* **Smart Recipe Finder:** Pencarian resep masakan berdasarkan bahan yang tersedia menggunakan API TheMealDB.
* **Expiry Tracker:** Notifikasi visual untuk bahan yang akan kadaluarsa.
* **Localization:** Mendukung Bahasa Indonesia dan Bahasa Inggris.

## Struktur Arsitektur
Aplikasi ini menerapkan **Clean Architecture** dengan pola **MVVM** untuk memastikan pemisahan *concern* yang jelas:

* **UI Layer:** Compose Screen & ViewModel (State management).
* **Domain Layer:** UseCase & Entity (Business Logic).
* **Data Layer:** Room Database & Retrofit API Implementation.

## Tech Stack
* **Language:** Kotlin
* **UI Framework:** Jetpack Compose
* **Architecture:** MVVM + Clean Architecture
* **Networking:** Retrofit (TheMealDB API)
* **Local Database:** Room Database
* **Asynchronous:** Coroutines & StateFlow

## Cara Instalasi & Menjalankan
1. Clone repository ini: `git clone https://github.com/KaiwenKanade/FreshGoApp.git`
2. Buka folder proyek di **Android Studio**.
3. Pastikan SDK Android API 36+ sudah terinstal.
4. Klik tombol **Run** untuk menjalankan aplikasi pada Emulator atau Perangkat Fisik.
5. Pastikan perangkat terkoneksi ke internet untuk menarik data resep.

## Informasi Tambahan
* **API:** [TheMealDB](https://www.themealdb.com/api.php)
* **Fitur Tambahan:** Implementasi Error Handling yang robust dan input validation untuk mencegah data korup pada Database lokal.
* **Smart Expiry Notification:** Aplikasi bisa mengecek barang yang akan kadaluarsa dalam waktu 2 hari. Android 13+ Ready: Sudah dilengkapi dengan sistem Runtime Permission yang secara elegan memunculkan pop-up meminta izin notifikasi kepada pengguna saat pertama kali masuk ke Home.
* **Custom Scheme Deep Linking (freshgo://):** Pengguna bisa membuka halaman spesifik di dalam aplikasi (seperti Detail Resep) langsung dari luar aplikasi. Mendukung pembukaan lewat Browser (HTML).

---
*KaiwenKanade A.K.A Kevin Maleakhi*
