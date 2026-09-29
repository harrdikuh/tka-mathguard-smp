# Fix untuk GitHub Actions

Workflow sebelumnya gagal karena project tidak membawa Gradle Wrapper (`gradlew`).
Workflow baru tidak membutuhkan `gradlew`: GitHub Actions memasang Gradle 8.9 lalu menjalankan `gradle assembleDebug`.

Langkah:
1. Upload/replace seluruh isi project dengan versi ZIP ini.
2. Pastikan `.github/workflows/build-apk.yml` ikut ter-upload.
3. Buka tab Actions.
4. Jalankan `Build TKA MathGuard APK`.
5. Jika hijau/Success, buka run tersebut dan download Artifact `TKA-MathGuard-SMP-APK`.

Catatan: ini menghasilkan APK debug. Firebase masih belum aktif sampai project Firebase milikmu dihubungkan.
