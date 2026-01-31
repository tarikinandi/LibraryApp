# LibraryApp - Yayınevi Yönetim Sistemi Prototipi

Bu proje, Spring Boot ve Java 21 kullanılarak geliştirilmiş, ölçeklenebilir, modüler ve Clean Code prensiplerine uygun bir Yayınevi Yönetim Sistemi prototipidir.

Proje, yayınevlerini, yazarları ve kitapları yönetmek, dış servislerden veri çekmek ve çeşitli raporlama isterlerini karşılamak amacıyla tasarlanmıştır.

## Özellikler

* **CRUD Operasyonları:** Kitap, Yazar ve Yayınevi için veri bütünlüğü korunarak ekleme, güncelleme ve silme işlemleri.
* **İleri Seviye Filtreleme:**
    * Java Stream API ile filtreleme ('A' ile başlayan kitaplar).
    * JPA Query ile database-based filtreleme (2023 sonrası basılanlar).
* **Dış Servis Entegrasyonu:** `OpenFeign` kullanılarak Google Books API entegrasyonu.
* **Raporlama:** Nested (iç içe) DTO yapısı ile detaylı yayınevi ve kitap listeleme.
* **Hata Yönetimi:** `GlobalExceptionHandler` ile merkezi ve standartlaştırılmış hata yönetimi.
* **Dokümantasyon:** `OpenAPI (Swagger UI)` ile interaktif API dokümantasyonu.

## Teknoloji Yığını

* **Dil:** Java 21
* **Framework:** Spring Boot 3.4.x
* **Veritabanı:** PostgreSQL
* **ORM:** Spring Data JPA (Hibernate)
* **API Client:** Spring Cloud OpenFeign
* **Containerization:** Docker & Docker Compose
* **API Documentation:** SpringDoc OpenAPI (Swagger)
* **Testing:** JUnit 5, Mockito
* **Tools:** Lombok, Maven

## Mimari Kararlar ve Tasarım Notları

Proje geliştirilirken **SOLID** prensipleri ve **Layered Architecture** benimsenmiştir.

### 1. Veritabanı Normalizasyonu ve Trade-off'lar
Case gereksinimlerine (%100) sadık kalmak amacıyla `Author` (Yazar) ve `Book` (Kitap) arasında **One-to-One** ilişki kurulmuştur ve Foreign Key (`bookID`) yazar tablosunda tutulmuştur.
* *Not:* Prodüksiyon ortamında Yazarlar ve Kitaplar arasında **Many-to-Many** ilişki kurulması ve `Authors` tablosunun kitaplardan bağımsız olması önerilir. Bu projede case isterleri önceliklendirilmiştir.

### 2. DTO Pattern (Data Transfer Objects)
Entity nesnelerini doğrudan dış dünyaya açmamak (Encapsulation) ve döngüsel bağımlılıkları (Circular Reference) engellemek için Java `record` yapısı kullanılarak Request ve Response DTO'ları oluşturulmuştur.
* Google Books API ve Frontend ihtiyaçları için `@JsonInclude(JsonInclude.Include.NON_NULL)` kullanılarak response payload'ları optimize edilmiştir.

### 3. Güvenlik
Google Books API anahtarı (API Key) kod içerisinde hardcoded olarak tutulmamış, `Environment Variable` üzerinden enjekte edilebilir hale getirilmiştir.

## Kurulum ve Çalıştırma

Projeyi çalıştırmanın en kolay yolu Docker kullanmaktır.

### Ön Gereksinimler
* Docker & Docker Compose
* Java 21 (Local çalıştırma için)
* Maven

### Yöntem 1: Docker ile Çalıştırma (Önerilen)

1.  Projeyi klonlayın:
    ```bash
    git clone [https://github.com/tarikinandi/LibraryApp.git](https://github.com/tarikinandi/LibraryApp.git)
    cd LibraryApp
    ```

2.  Docker Compose ile veritabanını ayağa kaldırın:
    ```bash
    docker-compose up -d
    ```
    *(Not: Veritabanı 5433 portundan yayın yapacak şekilde ayarlanmıştır.)*

3.  Environment Variable Ayarı (Opsiyonel - Google API için):
    IDE'nizin Run Configuration ayarlarından `GOOGLE_API_KEY` değişkenini tanımlayın.

4.  Uygulamayı başlatın:
    ```bash
    mvn spring-boot:run
    ```

### Yöntem 2: Local PostgreSQL ile Çalıştırma

Eğer Docker kullanmıyorsanız, `src/main/resources/application.properties` dosyasındaki veritabanı ayarlarını kendi local PostgreSQL bilgilerinize göre güncelleyin.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/librarydb
spring.datasource.username=postgres
spring.datasource.password=sifreniz
```

## API Dokümantasyonu (Swagger)

Uygulama çalıştıktan sonra tarayıcınızdan aşağıdaki adrese giderek tüm endpoint'leri interaktif olarak test edebilirsiniz:

 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

### Tüm Endpoint Listesi

Aşağıda projede mevcut olan servislerin tam listesi yer almaktadır:

#### Kitap İşlemleri

| Metot | URL | Açıklama                                            |
| :--- | :--- |:----------------------------------------------------|
| `POST` | `/api/v1/books` | Yeni Kitap, Yazar ve Yayınevi kaydeder.             |
| `GET` | `/api/v1/books` | Sistemdeki kayıtlı tüm kitapları listeler.          |
| `PUT` | `/api/v1/books/{id}` | ID'si verilen kitabın bilgilerini günceller.        |
| `DELETE` | `/api/v1/books/{id}` | ID'si verilen kitabı siler.                         |
| `GET` | `/api/v1/books/filter/starts-with-a` | İsmi 'A' harfi ile başlayan kitapları getirir.      |
| `GET` | `/api/v1/books/filter/after-2023` | 2023 yılından sonra basılan kitapları getirir.      |
| `GET` | `/api/v1/books/search/external` | Google Books API üzerinden dış serviste kitap arar. |

#### Yayınevi İşlemleri

| Metot | URL | Açıklama |
| :--- | :--- | :--- |
| `GET` | `/api/v1/publishers` | Sistemdeki tüm yayınevlerini listeler. |
| `GET` | `/api/v1/publishers/top2-details` | ID sırasına göre ilk 2 yayınevini, altındaki kitap listesiyle detaylı raporlar. |

#### ️ Yazar İşlemleri

| Metot | URL | Açıklama |
| :--- | :--- | :--- |
| `GET` | `/api/v1/authors` | Sistemdeki tüm yazarları listeler. |

## Proje Yapısı

Proje, katmanlı mimari (Layered Architecture) prensiplerine uygun olarak aşağıdaki gibi yapılandırılmıştır:

```text
src/main/java/com/libraryapp
├── client          # Feign Client (Google Books Integration)
├── controller      # REST API Katmanı
├── exception       # Global Exception Handling
├── model
│   ├── dto         # Data Transfer Objects (Records)
│   └── entity      # Database Entities
├── repository      # Data Access Layer (JPA)
├── service         # Business Logic Layer
└── LibraryApplication.java
```
