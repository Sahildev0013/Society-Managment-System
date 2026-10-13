package com.society.management.controller;

import com.society.management.dto.ComplaintDto;
import com.society.management.dto.PaymentDto;
import com.society.management.model.*;
import com.society.management.service.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/resident")
public class ResidentController {

    private final ResidentService residentService;
    private final MaintenanceBillService billService;
    private final PaymentService paymentService;
    private final NoticeService noticeService;
    private final ComplaintService complaintService;
    private final VisitorService visitorService;

    public ResidentController(ResidentService residentService,
                              MaintenanceBillService billService,
                              PaymentService paymentService,
                              NoticeService noticeService,
                              ComplaintService complaintService,
                              VisitorService visitorService) {
        this.residentService = residentService;
        this.billService = billService;
        this.paymentService = paymentService;
        this.noticeService = noticeService;
        this.complaintService = complaintService;
        this.visitorService = visitorService;
    }

    private Resident getLoggedInResident(Authentication authentication) {
        String email = authentication.getName();
        return residentService.getResidentByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Resident profile not found for user: " + email));
    }

    // =========================================================================
    // Dashboard
    // =========================================================================
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        Resident resident = getLoggedInResident(authentication);
        model.addAttribute("resident", resident);

        List<MaintenanceBill> ownBills = billService.getBillsForFlat(resident.getFlat().getId());
        long pendingBillsCount = ownBills.stream().filter(b -> "PENDING".equalsIgnoreCase(b.getStatus())).count();
        double pendingAmount = ownBills.stream()
                .filter(b -> "PENDING".equalsIgnoreCase(b.getStatus()))
                .mapToDouble(MaintenanceBill::getAmount)
                .sum();

        model.addAttribute("pendingBillsCount", pendingBillsCount);
        model.addAttribute("pendingAmount", pendingAmount);
        model.addAttribute("ownPendingBills", ownBills.stream().filter(b -> "PENDING".equalsIgnoreCase(b.getStatus())).toList());
        model.addAttribute("latestNotices", noticeService.getRecentNotices());
        model.addAttribute("ownComplaints", complaintService.getComplaintsByResident(resident));
        model.addAttribute("visitorRequests", visitorService.getVisitorsForFlat(resident.getFlat().getId()).stream()
                .filter(v -> "PENDING".equalsIgnoreCase(v.getStatus())).toList());

        return "resident/dashboard";
    }

    // =========================================================================
    // Bills & Payment
    // =========================================================================
    @GetMapping("/bills")
    public String viewBills(Authentication authentication, Model model) {
        Resident resident = getLoggedInResident(authentication);
        model.addAttribute("resident", resident);
        model.addAttribute("bills", billService.getBillsForFlat(resident.getFlat().getId()));
        if (!model.containsAttribute("paymentDto")) {
            model.addAttribute("paymentDto", new PaymentDto());
        }
        return "resident/bills";
    }

    @PostMapping("/bills/pay")
    public String payBill(@Valid @ModelAttribute("paymentDto") PaymentDto paymentDto,
                          BindingResult result,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        Resident resident = getLoggedInResident(authentication);

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please select a valid payment mode.");
            return "redirect:/resident/bills";
        }

        try {
            MaintenanceBill bill = billService.getBillById(paymentDto.getBillId())
                    .orElseThrow(() -> new IllegalArgumentException("Bill not found"));

            // Verify bill belongs to this resident's flat
            if (!bill.getFlat().getId().equals(resident.getFlat().getId())) {
                redirectAttributes.addFlashAttribute("error", "Unauthorized: You can only pay bills for your own flat.");
                return "redirect:/resident/bills";
            }

            Payment payment = paymentService.processPayment(paymentDto);
            redirectAttributes.addFlashAttribute("success", "Payment successful! Receipt generated: " + payment.getReceiptNo());
            return "redirect:/resident/receipt/" + payment.getReceiptNo();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/resident/bills";
        }
    }

    // =========================================================================
    // Printable Receipt
    // =========================================================================
    @GetMapping("/receipt/{receiptNo}")
    public String viewReceipt(@PathVariable String receiptNo,
                              Authentication authentication,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        Resident resident = getLoggedInResident(authentication);

        return paymentService.getPaymentByReceiptNo(receiptNo).map(payment -> {
            // Verify payment belongs to resident's flat
            if (!payment.getBill().getFlat().getId().equals(resident.getFlat().getId())) {
                redirectAttributes.addFlashAttribute("error", "Unauthorized access to this receipt.");
                return "redirect:/resident/bills";
            }
            model.addAttribute("payment", payment);
            model.addAttribute("bill", payment.getBill());
            model.addAttribute("resident", resident);
            return "resident/receipt";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("error", "Receipt not found!");
            return "redirect:/resident/bills";
        });
    }

    // =========================================================================
    // Complaints
    // =========================================================================
    @GetMapping("/complaints")
    public String complaints(Authentication authentication, Model model) {
        Resident resident = getLoggedInResident(authentication);
        model.addAttribute("resident", resident);
        model.addAttribute("complaints", complaintService.getComplaintsByResident(resident));
        if (!model.containsAttribute("complaintDto")) {
            model.addAttribute("complaintDto", new ComplaintDto());
        }
        return "resident/complaints";
    }

    @PostMapping("/complaints/save")
    public String raiseComplaint(@Valid @ModelAttribute("complaintDto") ComplaintDto complaintDto,
                                 BindingResult result,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        Resident resident = getLoggedInResident(authentication);

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.complaintDto", result);
            redirectAttributes.addFlashAttribute("complaintDto", complaintDto);
            redirectAttributes.addFlashAttribute("error", "Please fill in all complaint details properly.");
            return "redirect:/resident/complaints";
        }

        try {
            complaintService.raiseComplaint(resident, complaintDto);
            redirectAttributes.addFlashAttribute("success", "Complaint registered successfully! The management team will review it shortly.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/resident/complaints";
    }

    // =========================================================================
    // Visitors Approval / Rejection
    // =========================================================================
    @GetMapping("/visitors")
    public String visitorRequests(Authentication authentication, Model model) {
        Resident resident = getLoggedInResident(authentication);
        model.addAttribute("resident", resident);
        model.addAttribute("visitors", visitorService.getVisitorsForFlat(resident.getFlat().getId()));
        return "resident/visitors";
    }

    @PostMapping("/visitors/{id}/approve")
    public String approveVisitor(@PathVariable Long id,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        Resident resident = getLoggedInResident(authentication);
        Visitor visitor = visitorService.getVisitorById(id)
                .orElseThrow(() -> new IllegalArgumentException("Visitor not found"));

        if (!visitor.getFlat().getId().equals(resident.getFlat().getId())) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access.");
            return "redirect:/resident/visitors";
        }

        visitorService.updateApprovalStatus(id, "APPROVED");
        redirectAttributes.addFlashAttribute("success", "Visitor '" + visitor.getName() + "' approved successfully!");
        return "redirect:/resident/visitors";
    }

    @PostMapping("/visitors/{id}/reject")
    public String rejectVisitor(@PathVariable Long id,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        Resident resident = getLoggedInResident(authentication);
        Visitor visitor = visitorService.getVisitorById(id)
                .orElseThrow(() -> new IllegalArgumentException("Visitor not found"));

        if (!visitor.getFlat().getId().equals(resident.getFlat().getId())) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access.");
            return "redirect:/resident/visitors";
        }

        visitorService.updateApprovalStatus(id, "REJECTED");
        redirectAttributes.addFlashAttribute("success", "Visitor '" + visitor.getName() + "' has been rejected.");
        return "redirect:/resident/visitors";
    }

    // =========================================================================
    // Notices Board
    // =========================================================================
    @GetMapping("/notices")
    public String viewNotices(Model model) {
        model.addAttribute("notices", noticeService.getAllNotices());
        return "resident/notices";
    }
}
