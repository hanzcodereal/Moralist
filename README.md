# Moralist

Aplikasi pemutar musik Android berbahasa Indonesia. Dikembangkan oleh **Hanz Dev Official** (hanzzcodee@gmail.com).

Versi aplikasi 3.0.0, platform Android, hanya build release. Server: https://moralist.up.railway.app

## Build APK release

Lewat GitHub: tab **Actions**, pilih **Build APK Release**, tekan **Run workflow**. Hasilnya APK di bagian Artifacts (atau di Releases jika opsi terbitkan dicentang). Folder `metroproto` dan `libopus` diambil otomatis oleh workflow.

Lokal:

```
./gradlew :androidApp:assembleRelease
```

APK ada di `androidApp/build/outputs/apk/release/`. Keystore bawaan: `moralist-release.jks` (alias `moralist`, password `moralist2026`). Ganti dengan milikmu lewat variabel KEYSTORE_PATH, KEYSTORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD.

Lisensi: Hanz Dev Official.
