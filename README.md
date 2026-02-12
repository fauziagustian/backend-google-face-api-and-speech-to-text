# AI Project: Face Recognition & Speech-to-Text Backend

Backend service berbasis Spring Boot yang mengintegrasikan layanan Google Cloud Platform (GCP) untuk fitur pengenalan wajah dan transkripsi suara ke teks.

## 🚀 Fitur Utama

### 1. Face Recognition (GCP Vision API)
- **Enrollment**: Pendaftaran user dengan foto wajah (Depan & Samping).
  - Validasi keberadaan wajah pada setiap foto.
  - Penyimpanan foto otomatis ke Google Cloud Storage (`users/{userId}/...`).
- **Validation**: Analisis kualitas foto wajah.
  - Deteksi wajah.
  - Cek tingkat keburaman (Blur Likelihood).
  - Cek posisi kepala (Head Pose/Tilt) untuk memastikan wajah menghadap ke depan.

### 2. Speech-to-Text (GCP Speech-to-Text API)
- Transkripsi file audio (format `.wav`).
- Mendukung Bahasa Indonesia (`id-ID`).
- Fitur *Automatic Punctuation* (tanda baca otomatis).
- Output: Teks transkrip dan nilai *confidence*.

## 🛠️ Teknologi

- **Java 17**
- **Spring Boot 3.x**
- **Maven**
- **Google Cloud Platform Services**:
  - Cloud Vision API
  - Cloud Speech-to-Text API
  - Cloud Storage

## ⚙️ Prasyarat & Instalasi

### 1. Persiapan Google Cloud
Pastikan Anda memiliki **Service Account Key (JSON)** dengan role berikut:
- `Storage Object Admin`
- `Cloud Vision API User`
- `Cloud Speech Client`

### 2. Konfigurasi Project
1. Clone repository ini.
2. Letakkan file JSON Service Account Anda di folder project (jangan di-commit ke Git).
3. Update `src/main/resources/application.yml`:
   ```yaml
   gcp:
     bucket-name: nama-bucket-anda # Ganti dengan nama bucket GCP Storage Anda
   ```

### 3. Setup Environment Variable
Set variable `GOOGLE_APPLICATION_CREDENTIALS` mengarah ke file JSON Anda.

**Windows (PowerShell):**
```powershell
$env:GOOGLE_APPLICATION_CREDENTIALS="C:\path\to\your-service-account.json"
```

**Linux/Mac:**
```bash
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/your-service-account.json"
```

## ▶️ Cara Menjalankan

Jalankan aplikasi menggunakan Maven Wrapper:

```bash
./mvnw spring-boot:run
```
Aplikasi akan berjalan di `http://localhost:8080`.

## ☁️ Deployment ke Google Cloud Platform (GCP)

### Opsi 1: Menggunakan Script Otomatis (Windows)
1. Pastikan **Google Cloud SDK** sudah terinstall dan Anda sudah login:
   ```powershell
   gcloud auth login
   gcloud config set project ai-project-487201
   ```
2. Jalankan script deployment:
   ```powershell
   .\deploy.ps1
   ```
   Script ini akan otomatis:
   - Mengaktifkan API yang dibutuhkan.
   - Build docker image ke Container Registry.
   - Deploy aplikasi ke **Cloud Run** (Region Jakarta).

### Opsi 2: Manual Deployment
Jika Anda ingin melakukan deployment secara manual:

1. **Build Container**
   ```bash
   gcloud builds submit --tag gcr.io/ai-project-487201/backend-api .
   ```

2. **Deploy ke Cloud Run**
   ```bash
   gcloud run deploy backend-api \
     --image gcr.io/ai-project-487201/backend-api \
     --platform managed \
     --region asia-southeast2 \
     --allow-unauthenticated \
     --memory 1Gi
   ```

## 📚 Dokumentasi API

### 1. Face Enrollment
Mendaftarkan wajah user baru.
- **Endpoint**: `POST /api/face/enroll`
- **Content-Type**: `multipart/form-data`
- **Body**:
  - `userId` (String): ID unik pengguna.
  - `frontImage` (File): Foto wajah tampak depan.
  - `sideImage` (File): Foto wajah tampak samping.

### 2. Face Validation
Memvalidasi kualitas foto wajah sebelum diproses lebih lanjut.
- **Endpoint**: `POST /api/face/validate`
- **Content-Type**: `multipart/form-data`
- **Body**:
  - `image` (File): Foto yang akan divalidasi.
- **Response**: Status deteksi wajah, level blur, dan sudut kemiringan kepala.

### 3. Speech to Text
Mengubah audio menjadi teks.
- **Endpoint**: `POST /api/speech/stt`
- **Content-Type**: `multipart/form-data`
- **Body**:
  - `file` (File): File audio format `.wav`.
- **Response**: Teks transkrip dan tingkat akurasi.

## 🐛 Troubleshooting

- **Audio tidak terdeteksi?**
  Pastikan file audio menggunakan format `.wav` LINEAR16. Cek log aplikasi untuk melihat debug "Audio first 20 bytes".
- **Error "Application Default Credentials"?**
  Pastikan environment variable `GOOGLE_APPLICATION_CREDENTIALS` sudah diset dengan benar sebelum menjalankan aplikasi.
