# PasarCepat (Tugas Besar)

**PasarCepat** adalah sebuah aplikasi e-commerce berbasis Android yang dikembangkan sebagai proyek Tugas Besar. Aplikasi ini memungkinkan pengguna untuk menjelajahi produk, membeli barang, mengelola pesanan, membaca berita terkini, dan banyak lagi. Aplikasi ini mengintegrasikan Firebase untuk autentikasi pengguna dan manajemen data real-time, serta menggunakan arsitektur modern Android dengan Kotlin.

## 📱 Fitur Utama

### 1. Autentikasi Pengguna
*   **Login & Register**: Sistem pendaftaran dan login aman menggunakan **Firebase Authentication**.
*   **Profil Pengguna**: Pengguna dapat melihat dan mengedit profil mereka.
*   **Forgot Password**: Fitur pemulihan kata sandi jika pengguna lupa kredensial mereka.

### 2. Katalog Produk & Shopping
*   **Dashboard Interaktif**: Menampilkan produk unggulan (Featured, Best Sellers, New Arrivals, dll).
*   **Detail Produk**: Informasi lengkap mengenai produk termasuk gambar, deskripsi, harga, dan penilaian.
*   **Keranjang Belanja (Cart)**: Menambahkan produk ke keranjang dan mengelola item sebelum checkout.
*   **Wishlist**: Menyimpan produk favorit untuk dilihat nanti.
*   **Pencarian**: Fitur pencarian untuk menemukan produk dengan cepat.

### 3. Manajemen Pesanan (Order Management)
*   **Checkout**: Proses pembelian produk yang terintegrasi.
*   **Riwayat Pesanan**: Melihat status pesanan yang sedang berlangsung atau sudah selesai.

### 4. Fitur Tambahan
*   **Berita (News)**: Bagian khusus untuk membaca berita terkini terkait e-commerce atau update platform.
*   **Ulasan Produk (Reviews)**: Pengguna dapat melihat ulasan dan rating produk.
*   **Seller Dashboard**: Antarmuka untuk penjual (jika diaktifkan) untuk mengelola toko mereka.

## 🛠️ Teknologi yang Digunakan

Proyek ini dibangun menggunakan teknologi dan library berikut:

*   **Bahasa Pemrograman**: [Kotlin](https://kotlinlang.org/)
*   **Minimum SDK**: API 24 (Android 7.0 Nougat)
*   **UI Components**: XML Layouts, Material Design Components
*   **Backend & Database**:
    *   [Firebase Realtime Database](https://firebase.google.com/docs/database) (Penyimpanan data produk, user, order)
    *   [Firebase Authentication](https://firebase.google.com/docs/auth) (Manajemen user)
*   **Networking**:
    *   [Retrofit](https://square.github.io/retrofit/) & Gson (Komunikasi API)
*   **Image Loading**:
    *   [Glide](https://github.com/bumptech/glide) (Memuat dan caching gambar secara efisien)

## 📂 Struktur Proyek

*   `app/src/main/java/com/example/pasarcepat/`
    *   `presentation/`: Berisi Activity dan UI logic (MainActivity, LoginActivity, dll).
    *   `model/`: Data class untuk User, Product, News, Review, dll.
    *   `adapter/`: RecyclerView Adapters untuk menampilkan list data.
*   `app/src/main/res/layout/`: File layout XML untuk antarmuka pengguna.

## 🚀 Cara Menjalankan Aplikasi

1.  **Clone Repository**
    ```bash
    git clone https://github.com/username-anda/PasarCepat.git
    ```
2.  **Buka di Android Studio**
    *   Buka Android Studio.
    *   Pilih "Open an Existing Project".
    *   Arahkan ke folder proyek `PasarCepat`.
3.  **Sinkronisasi Gradle**
    *   Tunggu hingga Android Studio selesai mengunduh dependencies dan melakukan indexing.
4.  **Konfigurasi Firebase**
    *   Pastikan file `google-services.json` sudah ada di folder `app/`. (Catatan: File ini biasanya tidak di-upload ke public repo demi keamanan, pastikan Anda memilikinya dari konsol Firebase).
5.  **Run**
    *   Sambungkan perangkat Android atau gunakan Emulator.
    *   Klik tombol **Run** (▶️) di toolbar.

## 📝 Catatan Tambahan

*   Pastikan koneksi internet aktif karena aplikasi membutuhkan akses ke Firebase.
*   Aplikasi ini dioptimalkan untuk orientasi Portrait.

---
**Dibuat oleh:** Dava Ihza Bagus Setyawan
**Untuk:** Tugas Besar Pengembangan Aplikasi Bergerak/Sistem Informasi
