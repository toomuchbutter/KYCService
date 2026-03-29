# Customer KYC Profile Management Service

## 1. PROJECT OVERVIEW
The KYC (Know Your Customer) Profile Management Service is a robust Spring Boot application designed to handle the lifecycle of customer KYC records. It allows financial institutions to securely maintain customer identities, verify them, and manage associated documentation.
**Business Logic & Use Case:** The service enforces standard state transitions (e.g., PENDING -> VERIFIED / REJECTED). It supports soft-deletion, advanced filtering of records, and secure management of user documents by computing SHA-256 checksums to guarantee data integrity.

## 2. ARCHITECTURE EXPLANATION
The project strictly adheres to a standard multi-tiered architecture, which provides clean separation of concerns:
- **Controller Layer (`CustomerKYCController.java`):** Exposes the RESTful APIs. It receives HTTP requests, routes them, and returns standard HTTP responses.
- **Service Layer (`CustomerKYCService.java`):** Contains the core business logic, including validations, state transition checks, and checksum generations.
- **Repository Layer (`CustomerKYCRepository.java`):** Interfaces directly with the database using Spring Data JPA.
- **Entity Layer (`CustomerKYC.java`):** POJOs mapped to database tables using Hibernate.
- **DTO Layer (`KYCCreationRequestDto.java`):** Data Transfer Objects used to receive request payloads without exposing the internal Entity structure.

**Data Flow:**
`Client Request` ➔ `Controller` ➔ `Service` ➔ `Repository` ➔ `Database`

## 3. API DOCUMENTATION

### 3.1 Create KYC Record
- **URL:** `/KYC`
- **Method:** `POST`
- **Description:** Creates a new KYC record with PENDING status.
- **Request Body:**
  ```json
  {
    "customerId": 101,
    "fullName": "John Doe",
    "pan": "ABCDE1234F",
    "aadhar": "123456789012"
  }
  ```
- **Response:** `200 OK` (String: "KYC Created")

### 3.2 Get KYC Records (with filters)
- **URL:** `/KYC?status=PENDING&pan=ABCDE1234F`
- **Method:** `GET`
- **Description:** Fetches all non-deleted KYC records, with optional query parameters.
- **Response:**
  ```json
  [
    {
      "id": 1,
      "customerId": 101,
      "fullName": "John Doe",
      "pan": "ABCDE1234F",
      "aadharMasked": "XXXX-XXXX-9012",
      "status": "PENDING",
      "deleted": false
    }
  ]
  ```

### 3.3 Update KYC Record
- **URL:** `/KYC/{id}`
- **Method:** `PUT`
- **Description:** Updates details of an existing KYC record (only if status is PENDING).

### 3.4 Soft Delete KYC
- **URL:** `/KYC/{id}`
- **Method:** `DELETE`
- **Description:** Marks a KYC record as deleted (`deleted = true`) rather than physically removing it.

### 3.5 Verify KYC
- **URL:** `/KYC/{id}/verify`
- **Method:** `POST`
- **Description:** Transitions a PENDING KYC record to VERIFIED.

### 3.6 Reject KYC
- **URL:** `/KYC/{id}/reject`
- **Method:** `POST`
- **Description:** Transitions a PENDING KYC record to REJECTED with remarks.
- **Request Body:**
  ```json
  {
    "remarks": "Document resolution is too low."
  }
  ```

### 3.7 Upload Document
- **URL:** `/KYC/{id}/Documents`
- **Method:** `POST`
- **Description:** Uploads a Multipart file, computes its SHA-256 checksum, and links it to the KYC.

### 3.8 Fetch Documents
- **URL:** `/KYC/{id}/Documents`
- **Method:** `GET`
- **Description:** Fetches a list of metadata for all documents linked to a specific KYC ID.

## 4. WORKFLOW / DATA FLOW
1. **Creation:** A client submits customer demographic data. The Controller receives a `KYCCreationRequestDto`. The Service validates the PAN and Aadhaar, masks the Aadhaar, checks for duplicates in the Repository, and persists a `CustomerKYC` entity with `PENDING` status.
2. **Document Upload:** Documents are uploaded. The service calculates the file's hash and stores its metadata (`KYCDocument`).
3. **Verification:** A back-office agent reviews the data. The frontend calls `/verify`. The Service ensures the state is `PENDING`, updates it to `VERIFIED`, and saves the Entity.

## 5. EXCEPTION HANDLING
A comprehensive `@RestControllerAdvice` called `GlobalExceptionHandler` intercepts exceptions thrown by the system globally.
- **DuplicatePanException:** Thrown when a PAN already exists. Returns `400 Bad Request`.
- **InvalidStateException:** Thrown when attempting to update or verify a KYC that is NOT in the `PENDING` state. Returns `400 Bad Request`.
- **ResourceNotFoundException:** Thrown when fetching an ID that doesn't exist. Returns `404 Not Found`.

## 6. VALIDATION
The application enforces strict data integrity rules before persisting to the database.
- **Field Validations:** Uses `ValidationUtil` to strictly format PANs using Regex (`^[A-Z]{5}[0-9]{4}[A-Z]{1}$`).
- **Aadhaar Masking:** Sensitive data masking ensures full Aadhaar numbers are never persisted entirely. Only the last 4 digits remain visible (`XXXX-XXXX-1234`).
- **State Validation:** Business logic explicitly blocks state transitions if a KYC is already Verified or Rejected via programmatic validation throws.
*(Note: For enterprise scale applications, adding Jakarta `@Valid`, `@NotBlank`, `@Pattern` on incoming DTOs is recommended).*

## 7. DATABASE DESIGN
The application currently utilizes an an initial fast-prototyping relational setup (H2 Database) designed to migrate easily to MySQL/PostgreSQL.
- **CustomerKYC Table:** 
  Fields (`id` PK, `customer_id`, `full_name`, `pan`, `status`, `created_at`, `deleted`).
  - *Indexes:* Indexes are defined on `status`, `pan`, and `created_at` for optimized queries at scale.
- **KYCDocument Table:**
  Fields (`id` PK, `kyc_id` FK logical relationship to CustomerKYC, `file_name`, `content_type`, and `checksum` SHA-256).

## 8. SECURITY (Design Considerations)
In a production environment:
- Endpoints should be secured using Spring Security with **JWT (JSON Web Tokens)**.
- Public/Authentication endpoints (like token generation) would be Whitelisted, while API endpoints like `/KYC/**` would require a valid Bearer token containing appropriate roles (e.g., `ROLE_AGENT`, `ROLE_CUSTOMER`).

## 9. SAMPLE REQUEST FLOW
1. **CREATE**: `POST /KYC` (Payload: user details). Returns a PENDING record.
2. **FETCH**: `GET /KYC` shows the newly created record.
3. **UPLOAD DOC**: `POST /KYC/1/Documents` uploading a passport PDF.
4. **UPDATE**: Client realized they made a typo, `PUT /KYC/1` with new details.
5. **VERIFY**: Agent calls `POST /KYC/1/verify`. State -> VERIFIED.
6. **UPDATE ATTEMPT**: Client attempts `PUT /KYC/1`. **Result**: Error `400 Bad Request` ("Only PENDING KYC can be updated").

## 10. CODE WALKTHROUGH
- **`CustomerKYCController.java`**: The entry point. Note how it keeps methods extremely thin, directly delegating payloads to the `CustomerKYCService`.
- **`CustomerKYCService.java`**: The "heavy lifter". The `uploadDocs` method is particularly important as it handles SHA-256 hash generation for the `MultipartFile` bytes to maintain strict auditability of user-uploaded files.
- **`CustomerKYC.java`**: Uses JPA annotations like `@Entity`, `@Table(indexes = ...)` showing awareness of database query optimization.

## 11. TESTING STRATEGY
Production systems require comprehensive testing suites.
- **Unit Tests:** using Mockito to mock `CustomerKYCRepository` and test the PAN validation logic inside `CustomerKYCService` completely decoupled from Spring Context.
- **Integration Tests:** using `@SpringBootTest` and `MockMvc` to persist to an in-memory database and verify HTTP status responses (e.g. testing the `DuplicatePanException` trigger).

