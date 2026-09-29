# Cloud Sync Setup — TKA MathGuard SMP v3

Versi ini menyiapkan arsitektur sinkronisasi cloud menggunakan Firebase Firestore.

## Yang perlu dibuat di Firebase Console
1. Buat Firebase project.
2. Tambahkan Android app dengan package:
   `com.example.tkamathguard`
3. Download `google-services.json` dan letakkan di:
   `app/google-services.json`
4. Aktifkan Firestore Database.
5. Tambahkan Firebase Authentication (Email/Password) untuk akun guru.

## Struktur Firestore yang disarankan

`classes/{classId}`
- name
- teacherUid
- createdAt

`classes/{classId}/students/{studentId}`
- name
- joinedAt

`classes/{classId}/results/{resultId}`
- studentId
- studentName
- correct
- wrong
- score
- integrity
- submittedAt

## Security
Jangan menggunakan rules yang mengizinkan semua orang membaca/menulis database pada aplikasi produksi.
Guru hanya boleh membaca kelas yang dimilikinya; siswa hanya boleh mengirim hasil untuk sesi/kelas yang sah.

## Catatan
Project ini tidak berisi `google-services.json` karena file tersebut spesifik untuk project Firebase milik pemilik aplikasi. Setelah file itu ditambahkan dan plugin Firebase dikonfigurasi, repository ini dapat disambungkan ke Firestore.
