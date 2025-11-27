# WarrantySys  
**Warranty Claim Management System using Java, JavaFX & MySQL**

WarrantySys adalah aplikasi desktop berbasis Java yang dirancang untuk membantu perusahaan atau toko dalam mengelola proses **klaim garansi**, data pelanggan, dan data produk secara terstruktur dan efisien.

Aplikasi ini dibangun menggunakan JavaFX untuk antarmuka, MySQL sebagai database, serta konsep OOP dan arsitektur MVC agar mudah dikembangkan.

---

## 📌 Fitur Utama

### Login & Role-Based Access
- Mendukung dua peran pengguna:
  - **Admin** – akses penuh ke seluruh modul dan dapat memproses status klaim.
  - **Staff** – hanya dapat menambah dan mengelola klaim tanpa memproses persetujuan.
- Validasi username dan password langsung dari database.

### Dashboard Interaktif
- Menampilkan statistik:
  - Total pelanggan  
  - Total produk  
  - Total klaim garansi  
- Menampilkan daftar klaim terbaru secara ringkas.
- Menampilkan nama pengguna berdasarkan session login.

### Manajemen Pelanggan
- Tambah, ubah, hapus data pelanggan.
- Menyimpan informasi nama, nomor telepon, email, dan alamat.
- Tampilan tabel interaktif menggunakan JavaFX TableView.

### Manajemen Produk
- CRUD produk.
- Menyimpan nama, model, dan masa garansi.

### Manajemen Klaim Garansi
- Input klaim baru dengan nomor klaim otomatis (UUID).
- Validasi tanggal pembelian dan tanggal klaim.
- Admin dapat mengubah status (DIAJUKAN, DIPROSES, DITOLAK, DISETUJUI, SELESAI).
- Staff hanya dapat mengajukan.

### Sistem Logout dengan Session
- Menghapus session user aktif.
- Mengarahkan kembali ke halaman login.

---

## 🧩 Teknologi yang Digunakan

| Teknologi | Keterangan |
|----------|------------|
| **Java 17+ / JDK 21+** | Bahasa utama aplikasi |
| **JavaFX** | Antarmuka pengguna |
| **Scene Builder** | Mendesain tampilan FXML |
| **MySQL** | Database relasional |
| **JDBC** | Koneksi Java–MySQL |
| **CSS JavaFX** | Styling tampilan |
| **OOP Concepts** | Struktur program |
| **MVC Architecture** | Pembagian peran sistem |

---

## 🧠 Penerapan OOP dalam WarrantySys

WarrantySys dibangun menggunakan konsep OOP secara penuh:

### **Encapsulation**
Atribut model disembunyikan (`private`) serta diakses melalui getter dan setter untuk menjaga keamanan data.

### **Inheritance**
Menggunakan struktur role (ADMIN & STAFF) melalui enum untuk membedakan akses dan perilaku pengguna.

### **Polymorphism**
Saat login, method yang sama menghasilkan tampilan dashboard yang berbeda berdasarkan role user.

### **Abstraction (DAO Pattern)**
Semua operasi database dipisahkan dalam class DAO, sehingga controller tetap bersih dari query SQL.

### **Composition**
`WarrantyClaim` memiliki (`has-a`) `Customer` dan `Product`.

### **Singleton Session**
Class `Session` menyimpan data login user secara global dan hanya satu instance aktif selama aplikasi berjalan.

### **I/O Stream**
Digunakan pada proses export data klaim ke file CSV.

### **Exception Handling**
Digunakan di seluruh module untuk menjaga aplikasi dari crash akibat kesalahan input atau koneksi database.

---

## 🗃️ Database Setup (MySQL)

1. Buat database:
```sql
CREATE DATABASE warranty_db;
```

2. Import file database:
```
warranty_db.sql
```

3. Sesuaikan koneksi pada:
```
dao/Database.java
```

```java
private static final String URL = "jdbc:mysql://localhost:3306/warranty_db";
private static final String USER = "root";
private static final String PASS = "";
```

---

## ▶️ Cara Menjalankan Aplikasi

### 1️⃣ Install JavaFX  
Download JavaFX di:  
https://gluonhq.com/products/javafx/

### 2️⃣ Tambahkan JavaFX ke NetBeans  
- Klik kanan project → Properties  
- Libraries → Add JAR/Folder  
- Tambahkan semua file `.jar` dari `javafx/lib`

### 3️⃣ Tambahkan VM Options  
```
--module-path "C:\javafx\lib" --add-modules javafx.controls,javafx.fxml
```

### 4️⃣ Jalankan aplikasi melalui:
```
app.WarrantyApp
```

---

## 👤 Akun Default

Admin:
```
username: admin
password: admin123
```

Staff:
```
username: staff
password: staff123
```

(Dapat diedit di database sesuai kebutuhan)

---

## 📝 Catatan Akhir

Aplikasi WarrantySys ini merupakan hasil pengembangan proyek akhir dari mata kuliah Pemrograman Berorientasi Objek. Selama proses pengembangannya, saya memperoleh banyak pengalaman berharga dalam menerapkan konsep OOP, mengintegrasikan JavaFX sebagai antarmuka grafis, serta menghubungkan aplikasi Java dengan database MySQL melalui JDBC. Project ini membantu saya memahami bagaimana membangun aplikasi desktop yang terstruktur dengan pendekatan MVC dan memanfaatkan konsep-konsep OOP seperti Encapsulation, Polymorphism, Abstraction, serta Exception Handling.


Selain itu, pengerjaan WarrantySys juga memberikan pemahaman yang lebih mendalam tentang manajemen data, pembuatan UI yang konsisten, serta penerapan role-based access antara Admin dan Staff. Ke depannya, aplikasi ini masih dapat dikembangkan lebih lanjut, baik dari sisi fitur maupun tampilan, namun versi saat ini telah memenuhi kebutuhan dasar dari sistem pengelolaan klaim garansi. Diharapkan project ini dapat bermanfaat sebagai referensi dan pembelajaran dalam pengembangan aplikasi berbasis Java.
