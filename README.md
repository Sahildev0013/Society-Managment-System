# Society Management System (SMS) 🏢
**College Java Project &bull; Spring Boot 3 & Thymeleaf &bull; Role-Based Access Control**

A comprehensive, production-ready residential society web application built using **Java 17**, **Spring Boot 3**, **Spring Security 6**, **Spring Data JPA**, **Thymeleaf**, **Bootstrap 5**, and **Chart.js**.

---

## 🌟 Key Features by Role

### 1. 🛡️ Admin Portal (`ROLE_ADMIN`)
* **Executive Dashboard**:
  * Real-time metrics: Total Flats, Registered Residents, Pending Bills, Open Complaints, and Total Maintenance Collection.
  * **Interactive Chart.js Visualizations**:
    * Monthly maintenance collection bar chart.
    * Complaints distribution doughnut chart (Open, In Progress, Resolved).
  * Quick activity feeds for recent notices and visitor arrivals.
* **Flats Management (CRUD)**:
  * Register new flats (Wing, Flat Number, Floor, Type like 1 BHK, 2 BHK, 3 BHK, Penthouse).
  * Unique constraint validation (`wing` + `flatNumber`) prevents duplicate flat numbers.
  * Edit and delete flats (with safety check preventing deletion of occupied flats).
* **Resident Accounts Management (CRUD)**:
  * Register residents (Full Name, Email, 10-digit Phone, Flat assignment, Owner vs Tenant).
  * Automatically creates Spring Security user login credentials with hashed BCrypt passwords.
  * Edit resident profiles and delete accounts.
* **Maintenance Bills & Invoicing**:
  * **1-Click Batch Bill Generation**: Generate monthly maintenance bills for all flats at once (Billing Month, Amount, Due Date).
  * **Duplicate Prevention**: Intelligently detects and prevents duplicate bills for the same flat in the same billing month.
  * Filter bills by Month and Status (`PAID` / `PENDING`).
  * View payment receipts and transaction records.
* **Notice Board Management**:
  * Publish, update, and remove official society announcements, AGM alerts, and maintenance circulars.
* **Complaints Helpdesk**:
  * Review all logged resident grievances with category tags.
  * Update resolution status (`OPEN` &rarr; `IN_PROGRESS` &rarr; `RESOLVED`) and record admin/technician remarks.
* **Visitor Security Archive**:
  * View complete historical gate entries with in-time, out-time, purpose, and resident approval status.

---

### 2. 🏠 Resident Portal (`ROLE_RESIDENT`)
* **Personalized Dashboard**:
  * Displays own pending maintenance dues, flat details, active tickets, and recent notices.
  * Real-time gate alert banner whenever a visitor is waiting for approval at the security gate.
* **Bills & Simulated Payment Gateway**:
  * Complete maintenance billing history for the resident's flat.
  * **"Pay Now" Workflow**: Instant simulated payment modal supporting **UPI**, **Debit Card**, **Credit Card**, and **Net Banking**.
  * Marks the invoice as `PAID`, records timestamp and mode, and generates an official **Unique Receipt Number** (`REC-YYYYMM-XXXXXX`).
* **Printable Payment Receipt**:
  * Professional official receipt layout with society header, registration details, "PAID" watermark, breakdown, and signature box.
  * Built-in `@media print` support: one click on "Print / Save as PDF" prints the clean receipt.
* **Complaint Registration**:
  * Raise maintenance tickets with categories (Plumbing, Electrical, Carpentry, Security, Noise, Cleaning, etc.).
  * Track status transitions and review remarks left by the society administration.
* **Visitor Gate Approvals**:
  * Receive real-time visitor requests sent by security guards.
  * **1-Click "Approve" or "Reject"** actions directly from the portal.
* **Digital Notice Board**:
  * Read society circulars, event announcements, and water/power maintenance schedules.

---

### 3. 👮 Security Guard Portal (`ROLE_GUARD`)
* **Gate Entry Log**:
  * Log incoming visitors by selecting the destination flat, visitor name, 10-digit phone number, and purpose (Delivery, Guest, Repair, etc.).
  * Status is automatically set to `PENDING` for resident authorization.
* **Today's Visitors Table**:
  * Real-time monitoring of all visitors arriving today and their resident approval status.
* **Visitor Exit Tracking**:
  * Log visitor departures with a single click on "Mark Exit" (records exact timestamp and marks status as `COMPLETED`).

---

## 🔐 Default Login Credentials (Seed Data)

The system automatically seeds initial data on first boot:

| Role | Portal Email | Password | Assigned Entity / Flat |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin@society.com` | `admin123` | Society Management Committee |
| **Resident 1 (Owner)** | `rahul@society.com` | `resident123` | Flat A-101 (2 BHK, Floor 1) |
| **Resident 2 (Tenant)** | `priya@society.com` | `resident123` | Flat A-102 (3 BHK, Floor 1) |
| **Resident 3 (Owner)** | `amit@society.com` | `resident123` | Flat B-201 (2 BHK, Floor 2) |
| **Security Guard** | `guard@society.com` | `guard123` | Main Gate Security Desk |

*(Quick-fill buttons are also available directly on the login page for convenience!)*

---

## 🗄️ Database Architecture & Entity Relationships (ER)

The application uses **Spring Data JPA** with clean object-relational mapping. By default, it operates on a persistent file-based **H2 database** located at `./data/societydb`.

### Entity Table Descriptions:

```
+-----------------------------------------------------------------------------------+
|                                 DATABASE ENTITIES                                 |
+-----------------------------------------------------------------------------------+
|  [users]                   [flats]                       [residents]              |
|  - id (PK)                 - id (PK)                     - id (PK)                |
|  - name                    - wing                        - user_id (FK -> users)  |
|  - email (Unique)          - flat_number                 - flat_id (FK -> flats)  |
|  - password (BCrypt)       - floor                       - phone                  |
|  - role (Enum)             - type                        - resident_type          |
|         |                        |                               |                |
|         +------------------------+-------------------------------+                |
|                                  |                               |                |
|                                  v                               v                |
|                        [maintenance_bills]                 [complaints]           |
|                        - id (PK)                           - id (PK)              |
|                        - flat_id (FK -> flats)             - resident_id (FK)     |
|                        - billing_month                     - category             |
|                        - amount                            - description          |
|                        - due_date                          - status               |
|                        - status (PENDING/PAID)             - created_on           |
|                        - Unique(flat_id, month)            - admin_remark         |
|                                  |                                                |
|                                  v                                                |
|                              [payments]                                           |
|                              - id (PK)                                            |
|                              - bill_id (FK -> bills, Unique)                       |
|                              - paid_on                                            |
|                              - mode (UPI/CARD/NET_BANKING)                        |
|                              - receipt_no (Unique)                                |
|                              - transaction_ref                                    |
|                                                                                   |
|  [notices]                                    [visitors]                          |
|  - id (PK)                                    - id (PK)                           |
|  - title                                      - flat_id (FK -> flats)             |
|  - message                                    - name, phone, purpose              |
|  - created_on                                 - in_time, out_time                 |
|  - posted_by                                  - status (PENDING/APPROVED/etc.)    |
+-----------------------------------------------------------------------------------+
```

### Relational Mapping Details:
* **`User` &harr; `Resident`**: `@OneToOne` linking authentication account to resident profile.
* **`Flat` &harr; `Resident`**: `@ManyToOne` / `@OneToMany` linking flats to occupants.
* **`Flat` &harr; `MaintenanceBill`**: `@ManyToOne` / `@OneToMany` with `UniqueConstraint(flat_id, billing_month)`.
* **`MaintenanceBill` &harr; `Payment`**: `@OneToOne` linking bill to receipt and transaction details.
* **`Flat` &harr; `Visitor`**: `@ManyToOne` associating incoming visitors with destination flats.
* **`Resident` &harr; `Complaint`**: `@ManyToOne` associating service tickets with the resident who submitted them.

---

## 🛠️ Switching to MySQL (Optional)

To switch from the default file-based H2 database to MySQL:

1. Open `src/main/resources/application.properties`.
2. Comment out the active H2 configuration section.
3. Uncomment the MySQL section:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/society_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=your_mysql_password
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
```
4. Start your local MySQL service. The database `society_db` and all tables will be created automatically.

---

## 🚀 How to Run the Application

### Prerequisites:
* **Java 17** or higher installed (`java -version`).
* **Maven 3.8+** (or use the included Maven wrapper `.\mvnw.cmd` / `./mvnw`).

### Steps to Run:

1. Open a terminal or command prompt inside the project folder:
   ```bash
   cd society-management-system
   ```

2. Run the application:
   * **If Maven is installed:**
     ```bash
     mvn spring-boot:run
     ```
   * **Using Maven Wrapper (Windows):**
     ```cmd
     .\mvnw.cmd spring-boot:run
     ```
   * **Using Maven Wrapper (macOS/Linux):**
     ```bash
     ./mvnw spring-boot:run
     ```

3. Open your browser and navigate to:
   ```
   http://localhost:8080
   ```

4. **H2 Console Access** (Optional inspection):
   * URL: `http://localhost:8080/h2-console`
   * JDBC URL: `jdbc:h2:file:./data/societydb`
   * Username: `sa`
   * Password: *(leave blank)*

---

## 🧪 Testing Guide (Role-by-Role Walkthrough)

### Test Flow 1: 🛡️ Admin
1. Open `http://localhost:8080/login` and sign in with `admin@society.com` / `admin123`.
2. **Dashboard**: Observe the collection chart, complaints doughnut chart, and statistic badges.
3. **Flats**: Go to **Flats** in the sidebar. Add flat `C-301` (Floor 3, 3 BHK). Verify it appears in the table.
4. **Residents**: Go to **Residents**. Add a resident assigned to `C-301`. A user login account is created automatically.
5. **Generate Bills**: Go to **Maintenance Bills**. Enter month `2026-11`, amount `2800.00`, and click "Generate Bills For All Flats".
   * Try clicking generate again with the same month: notice the duplicate prevention alert!
6. **Notices**: Publish a new notice. Verify it appears on the board.
7. **Complaints**: Open **Complaints**. Select a status dropdown (e.g., mark as `RESOLVED`), enter an admin remark, and click Update.

### Test Flow 2: 👮 Security Guard
1. Logout from Admin and login as `guard@society.com` / `guard123`.
2. Select destination flat **A-101**, enter Visitor Name "Rohit Sharma", Phone "9988776655", Purpose "Courier Delivery", and submit.
3. Observe that Rohit Sharma appears in **Today's Visitors** with status `PENDING`.

### Test Flow 3: 🏠 Resident
1. Logout from Guard and login as `rahul@society.com` / `resident123` (Owner of Flat A-101).
2. **Visitor Approvals**: You will see a banner alert about the pending visitor. Go to **Visitor Approvals** and click **"Approve"**.
3. **Pay Maintenance**:
   * Go to **My Bills & Payments**.
   * Locate the pending bill for `2026-10` and click **"Pay Now"**.
   * Select a payment mode (e.g., UPI) and confirm.
   * You will be redirected to the **Official Printable Receipt** (`REC-YYYYMM-XXXXXX`) with receipt details and "PAID" stamp.
   * Click **"Print / Save as PDF"** to preview the print stylesheet.
4. **Raise Complaint**: Go to **Raise Complaint**, select "Plumbing", enter a description, and submit.
5. **Notice Board**: View notices posted by the administration.

### Test Flow 4: 🔒 Security & Access Control Verification
1. While logged in as `rahul@society.com` (Resident), try navigating directly in the address bar to `http://localhost:8080/admin/dashboard` or `http://localhost:8080/guard/dashboard`.
2. The system intercepts the request and renders the custom **403 Access Denied** page, protecting administrative and security endpoints.

---

## 📂 Project Architecture

```
society-management-system/
├── pom.xml
├── mvnw.cmd
├── mvnw
├── README.md
├── src/
│   ├── main/
│   │   ├── java/com/society/management/
│   │   │   ├── SocietyManagementApplication.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── CustomAuthenticationSuccessHandler.java
│   │   │   │   └── DataInitializer.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── AdminController.java
│   │   │   │   ├── ResidentController.java
│   │   │   │   └── GuardController.java
│   │   │   ├── dto/
│   │   │   │   ├── BillGenerationDto.java
│   │   │   │   ├── PaymentDto.java
│   │   │   │   ├── ComplaintDto.java
│   │   │   │   ├── ResidentDto.java
│   │   │   │   ├── FlatDto.java
│   │   │   │   ├── VisitorDto.java
│   │   │   │   └── NoticeDto.java
│   │   │   ├── model/
│   │   │   │   ├── Role.java
│   │   │   │   ├── User.java
│   │   │   │   ├── Flat.java
│   │   │   │   ├── Resident.java
│   │   │   │   ├── MaintenanceBill.java
│   │   │   │   ├── Payment.java
│   │   │   │   ├── Visitor.java
│   │   │   │   ├── Notice.java
│   │   │   │   └── Complaint.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── FlatRepository.java
│   │   │   │   ├── ResidentRepository.java
│   │   │   │   ├── MaintenanceBillRepository.java
│   │   │   │   ├── PaymentRepository.java
│   │   │   │   ├── VisitorRepository.java
│   │   │   │   ├── NoticeRepository.java
│   │   │   │   └── ComplaintRepository.java
│   │   │   └── service/
│   │   │       ├── CustomUserDetailsService.java
│   │   │       ├── UserService.java
│   │   │       ├── FlatService.java
│   │   │       ├── ResidentService.java
│   │   │       ├── MaintenanceBillService.java
│   │   │       ├── PaymentService.java
│   │   │       ├── VisitorService.java
│   │   │       ├── NoticeService.java
│   │   │       └── ComplaintService.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       │   ├── css/custom.css
│   │       │   └── js/app.js
│   │       └── templates/
│   │           ├── login.html
│   │           ├── error/403.html
│   │           ├── fragments/layout.html
│   │           ├── admin/
│   │           │   ├── dashboard.html
│   │           │   ├── flats.html
│   │           │   ├── residents.html
│   │           │   ├── bills.html
│   │           │   ├── notices.html
│   │           │   ├── complaints.html
│   │           │   └── visitors.html
│   │           ├── resident/
│   │           │   ├── dashboard.html
│   │           │   ├── bills.html
│   │           │   ├── receipt.html
│   │           │   ├── complaints.html
│   │           │   ├── visitors.html
│   │           │   └── notices.html
│   │           └── guard/
│   │               └── dashboard.html
```
