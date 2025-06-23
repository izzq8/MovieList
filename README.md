# myMovieList2

![Android](https://img.shields.io/badge/Platform-Android-brightgreen)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-orange)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue)

Aplikasi myMovieList2 adalah aplikasi daftar film berbasis Android yang memungkinkan pengguna untuk menjelajahi film-film populer, melihat detail film, informasi aktor, serta mengelola daftar tonton pribadi.

## Fitur

- 🎬 **Jelajahi Film**: Lihat film populer dari database TMDB
- 🔍 **Pencarian**: Cari film berdasarkan judul atau kata kunci
- 👨‍🎤 **Detail Aktor**: Lihat informasi lengkap tentang aktor favorit Anda
- 📋 **Daftar Tonton**: Simpan dan kelola daftar film yang ingin Anda tonton
- 👤 **Manajemen Profil**: Buat dan kelola akun pengguna Anda
- 🌙 **UI Modern**: Antarmuka pengguna yang menarik dengan Jetpack Compose dan Material 3
- 🔐 **Autentikasi**: Login dan registrasi dengan Firebase Authentication

## Teknologi

Aplikasi ini dibangun dengan teknologi modern untuk pengembangan Android:

- **Bahasa**: Kotlin
- **UI Framework**: Jetpack Compose
- **Arsitektur**: MVVM (Model-View-ViewModel)
- **Dependensi Injeksi**: Tidak ada (manual dependency provision)
- **Navigasi**: Navigation Compose
- **Jaringan**: Retrofit dengan OkHttp
- **Pemrosesan JSON**: Gson
- **Loading Gambar**: Coil
- **Backend & Autentikasi**: Firebase (Authentication, Firestore)
- **API Film**: TMDB (The Movie Database) API

## Prasyarat

Untuk menjalankan proyek ini, Anda memerlukan:

- Android Studio Hedgehog (2024.1.1) atau yang lebih baru
- JDK 11, 17 atau yang lebih baru
- Android SDK dengan level API minimum 31 (Android 12)
- TMDB API Key (disimpan di `apikeys.properties`)

## Instalasi

1. Clone repositori ini:
   ```
   git clone https://github.com/yourusername/myMovieList2.git
   ```

2. Buat file `apikeys.properties` di direktori root proyek dengan format berikut:
   ```
   TMDB_API_KEY=your_tmdb_api_key_here
   ```

3. Sync project dengan Gradle dan build aplikasi.

4. Jalankan aplikasi pada emulator atau perangkat fisik.

## Struktur Proyek

Proyek mengikuti struktur yang bersih dan terorganisir dengan baik:

```
com.faizabhinaya.mymovielist2
├── data
│   ├── model         # Model data aplikasi
│   ├── remote        # Konfigurasi API dan services
│   └── repository    # Repository untuk mengakses data
├── ui
│   ├── navigation    # Navigasi aplikasi dengan Compose
│   ├── screens       # Screen components
│   │   ├── actor     # Tampilan detail aktor
│   │   ├── auth      # Tampilan login & registrasi
│   │   ├── detail    # Tampilan detail film
│   │   ├── home      # Tampilan beranda
│   │   ├── main      # Container utama aplikasi
│   │   ├── profile   # Tampilan profil pengguna
│   │   ├── search    # Tampilan pencarian film
│   │   ├── splash    # Tampilan splash screen
│   │   └── watchlist # Tampilan daftar tonton
│   └── theme         # Tema dan styling aplikasi
└── utils             # Utilitas dan helper functions
```

## Screenshot

[Screenshot aplikasi akan ditambahkan di sini]

## Kontribusi

Kontribusi untuk meningkatkan aplikasi ini sangat disambut! Untuk berkontribusi:

1. Fork repositori
2. Buat branch fitur baru (`git checkout -b feature/amazing-feature`)
3. Commit perubahan Anda (`git commit -m 'Add some amazing feature'`)
4. Push ke branch (`git push origin feature/amazing-feature`)
5. Buka Pull Request

## Lisensi

Didistribusikan di bawah Lisensi MIT. Lihat `LICENSE` untuk informasi lebih lanjut.

## Kontak

Faiz Abhinaya - [@faizabhinaya](https://github.com/faizabhinaya)

Link Proyek: [https://github.com/faizabhinaya/myMovieList2](https://github.com/faizabhinaya/myMovieList2)
