# TKA MathGuard SMP v3 — Cloud Sync Ready

Prototype khusus SMP kelas 7–9 dengan rancangan sinkronisasi cloud.

## Fitur
- 20 soal / 60 menit
- randomisasi soal dan opsi
- hasil benar/salah
- Dashboard Guru dan ranking
- input kode kelas
- abstraksi `CloudRepository` agar backend dapat diganti ke Firebase/Firestore
- dokumentasi struktur database dan security rules

## Penting
Versi ini **belum terhubung ke Firebase milik pengguna** karena `google-services.json` dan kredensial backend harus berasal dari project Firebase pemilik aplikasi. Tidak ada kredensial yang dibenamkan atau dipalsukan.

Ikuti `FIREBASE_SETUP.md` untuk menghubungkan Firestore secara nyata.
