package com.society.management.config;

import com.society.management.model.*;
import com.society.management.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FlatRepository flatRepository;
    private final ResidentRepository residentRepository;
    private final MaintenanceBillRepository billRepository;
    private final PaymentRepository paymentRepository;
    private final NoticeRepository noticeRepository;
    private final ComplaintRepository complaintRepository;
    private final VisitorRepository visitorRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           FlatRepository flatRepository,
                           ResidentRepository residentRepository,
                           MaintenanceBillRepository billRepository,
                           PaymentRepository paymentRepository,
                           NoticeRepository noticeRepository,
                           ComplaintRepository complaintRepository,
                           VisitorRepository visitorRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.flatRepository = flatRepository;
        this.residentRepository = residentRepository;
        this.billRepository = billRepository;
        this.paymentRepository = paymentRepository;
        this.noticeRepository = noticeRepository;
        this.complaintRepository = complaintRepository;
        this.visitorRepository = visitorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return; // Data already seeded
        }

        System.out.println(">>> Initializing Society Management System Seed Data...");

        // 1. Seed Admin
        User admin = new User(
                "Society Admin",
                "admin@society.com",
                passwordEncoder.encode("admin123"),
                Role.ROLE_ADMIN
        );
        userRepository.save(admin);

        // 2. Seed Guard
        User guard = new User(
                "Security Gate Guard",
                "guard@society.com",
                passwordEncoder.encode("guard123"),
                Role.ROLE_GUARD
        );
        userRepository.save(guard);

        // 3. Seed Flats
        Flat flat1 = flatRepository.save(new Flat("101", "A", 1, "2 BHK"));
        Flat flat2 = flatRepository.save(new Flat("102", "A", 1, "3 BHK"));
        Flat flat3 = flatRepository.save(new Flat("201", "B", 2, "2 BHK"));
        Flat flat4 = flatRepository.save(new Flat("202", "B", 2, "1 BHK"));

        // 4. Seed 3 Residents with Flats & Login Accounts
        User residentUser1 = userRepository.save(new User(
                "Rahul Sharma",
                "rahul@society.com",
                passwordEncoder.encode("resident123"),
                Role.ROLE_RESIDENT
        ));
        Resident resident1 = residentRepository.save(new Resident(residentUser1, flat1, "9876543210", "OWNER"));

        User residentUser2 = userRepository.save(new User(
                "Priya Patel",
                "priya@society.com",
                passwordEncoder.encode("resident123"),
                Role.ROLE_RESIDENT
        ));
        Resident resident2 = residentRepository.save(new Resident(residentUser2, flat2, "9876543211", "TENANT"));

        User residentUser3 = userRepository.save(new User(
                "Amit Verma",
                "amit@society.com",
                passwordEncoder.encode("resident123"),
                Role.ROLE_RESIDENT
        ));
        Resident resident3 = residentRepository.save(new Resident(residentUser3, flat3, "9876543212", "OWNER"));

        // 5. Seed Notices
        noticeRepository.save(new Notice(
                "Annual General Body Meeting (AGM)",
                "The Annual General Body Meeting (AGM) of Green Valley Society will be held on Sunday at 10:00 AM in the Clubhouse. Major agenda points include solar installation and annual audit reports. All members are requested to attend.",
                LocalDateTime.now().minusDays(2),
                "Society Secretary"
        ));

        noticeRepository.save(new Notice(
                "Water Tank Cleaning Schedule",
                "Please note that the overhead and underground water tanks will undergo deep chemical cleaning on Thursday between 10:00 AM and 3:00 PM. Water supply will remain paused during this interval. Kindly store adequate water.",
                LocalDateTime.now().minusDays(1),
                "Estate Manager"
        ));

        noticeRepository.save(new Notice(
                "Diwali Celebration & Cultural Fest",
                "Green Valley Cultural Committee invites all residents to celebrate Diwali together on Saturday evening. Games for kids, musical performances, and society dinner will begin at 6:30 PM at the central garden.",
                LocalDateTime.now().minusHours(4),
                "Cultural Committee"
        ));

        // 6. Seed Maintenance Bills & Payments
        // Month: 2026-09 (September)
        MaintenanceBill billSep1 = billRepository.save(new MaintenanceBill(flat1, "2026-09", 2500.0, LocalDate.now().minusDays(18), "PAID"));
        paymentRepository.save(new Payment(billSep1, LocalDateTime.now().minusDays(20), "UPI", "REC-202609-881231", "TXN-172700101"));

        MaintenanceBill billSep2 = billRepository.save(new MaintenanceBill(flat2, "2026-09", 3000.0, LocalDate.now().minusDays(18), "PAID"));
        paymentRepository.save(new Payment(billSep2, LocalDateTime.now().minusDays(19), "NET_BANKING", "REC-202609-881232", "TXN-172700102"));

        MaintenanceBill billSep3 = billRepository.save(new MaintenanceBill(flat3, "2026-09", 2500.0, LocalDate.now().minusDays(18), "PAID"));
        paymentRepository.save(new Payment(billSep3, LocalDateTime.now().minusDays(17), "CREDIT_CARD", "REC-202609-881233", "TXN-172700103"));

        // Month: 2026-10 (October - Current)
        billRepository.save(new MaintenanceBill(flat1, "2026-10", 2500.0, LocalDate.now().plusDays(12), "PENDING"));
        billRepository.save(new MaintenanceBill(flat2, "2026-10", 3000.0, LocalDate.now().plusDays(12), "PENDING"));

        MaintenanceBill billOct3 = billRepository.save(new MaintenanceBill(flat3, "2026-10", 2500.0, LocalDate.now().plusDays(12), "PAID"));
        paymentRepository.save(new Payment(billOct3, LocalDateTime.now().minusDays(2), "UPI", "REC-202610-994321", "TXN-172800201"));

        billRepository.save(new MaintenanceBill(flat4, "2026-10", 2000.0, LocalDate.now().plusDays(12), "PENDING"));

        // 7. Seed Complaints
        Complaint c1 = new Complaint(
                resident1,
                "Plumbing",
                "Low water pressure in the master bathroom shower over the last three days.",
                "IN_PROGRESS",
                LocalDateTime.now().minusDays(2)
        );
        c1.setAdminRemark("Plumber Deepak has been scheduled for inspection today at 4:00 PM.");
        complaintRepository.save(c1);

        Complaint c2 = new Complaint(
                resident2,
                "Electrical",
                "Corridor motion sensor light on 1st floor near flat A-102 flickers constantly.",
                "RESOLVED",
                LocalDateTime.now().minusDays(3)
        );
        c2.setAdminRemark("Electrician replaced faulty LED capacitor. Issue resolved.");
        complaintRepository.save(c2);

        Complaint c3 = new Complaint(
                resident3,
                "Security",
                "Unauthorized two-wheeler parked in private designated parking slot B-201.",
                "OPEN",
                LocalDateTime.now().minusHours(8)
        );
        complaintRepository.save(c3);

        // 8. Seed Visitors
        visitorRepository.save(new Visitor(
                flat1,
                "Suresh Roy",
                "9811223344",
                "Amazon Package Delivery",
                LocalDateTime.now().minusHours(3),
                "APPROVED"
        ));

        visitorRepository.save(new Visitor(
                flat2,
                "Kavita Verma",
                "9822334455",
                "Family Guest / Relative",
                LocalDateTime.now().minusHours(1),
                "PENDING"
        ));

        Visitor completedVisitor = new Visitor(
                flat3,
                "Manoj Electrician",
                "9833445566",
                "AC Servicing Work",
                LocalDateTime.now().minusDays(1).withHour(11).withMinute(15),
                "COMPLETED"
        );
        completedVisitor.setOutTime(LocalDateTime.now().minusDays(1).withHour(13).withMinute(45));
        visitorRepository.save(completedVisitor);

        System.out.println(">>> Society Management System Seed Data initialized successfully!");
    }
}
