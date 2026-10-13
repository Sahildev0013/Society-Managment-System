package com.society.management.controller;

import com.society.management.dto.*;
import com.society.management.model.*;
import com.society.management.service.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final FlatService flatService;
    private final ResidentService residentService;
    private final MaintenanceBillService billService;
    private final NoticeService noticeService;
    private final ComplaintService complaintService;
    private final VisitorService visitorService;

    public AdminController(FlatService flatService,
                           ResidentService residentService,
                           MaintenanceBillService billService,
                           NoticeService noticeService,
                           ComplaintService complaintService,
                           VisitorService visitorService) {
        this.flatService = flatService;
        this.residentService = residentService;
        this.billService = billService;
        this.noticeService = noticeService;
        this.complaintService = complaintService;
        this.visitorService = visitorService;
    }

    // =========================================================================
    // Dashboard
    // =========================================================================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalFlats", flatService.getTotalFlatsCount());
        model.addAttribute("totalResidents", residentService.getTotalResidentsCount());
        model.addAttribute("pendingBillsCount", billService.getPendingBillsCount());
        model.addAttribute("openComplaintsCount", complaintService.getOpenComplaintsCount());
        model.addAttribute("totalCollection", billService.getTotalCollection());
        model.addAttribute("totalPendingAmount", billService.getTotalPendingAmount());

        // Data for Chart.js
        Map<String, Double> monthlyCollection = billService.getMonthlyCollectionMap();
        model.addAttribute("collectionLabels", monthlyCollection.keySet());
        model.addAttribute("collectionValues", monthlyCollection.values());

        Map<String, Long> complaintsStatus = complaintService.getComplaintsStatusDistribution();
        model.addAttribute("complaintStatusLabels", complaintsStatus.keySet());
        model.addAttribute("complaintStatusValues", complaintsStatus.values());

        // Recent summaries
        model.addAttribute("recentNotices", noticeService.getRecentNotices());
        model.addAttribute("recentVisitors", visitorService.getAllVisitors().stream().limit(5).toList());

        return "admin/dashboard";
    }

    // =========================================================================
    // Flats CRUD
    // =========================================================================
    @GetMapping("/flats")
    public String listFlats(Model model) {
        model.addAttribute("flats", flatService.getAllFlats());
        if (!model.containsAttribute("flatDto")) {
            model.addAttribute("flatDto", new FlatDto());
        }
        model.addAttribute("isEdit", false);
        return "admin/flats";
    }

    @PostMapping("/flats/save")
    public String saveFlat(@Valid @ModelAttribute("flatDto") FlatDto flatDto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.flatDto", result);
            redirectAttributes.addFlashAttribute("flatDto", flatDto);
            redirectAttributes.addFlashAttribute("error", "Please fix the validation errors in the flat form.");
            return "redirect:/admin/flats";
        }

        try {
            if (flatDto.getId() != null) {
                flatService.updateFlat(flatDto.getId(), flatDto);
                redirectAttributes.addFlashAttribute("success", "Flat updated successfully!");
            } else {
                flatService.createFlat(flatDto);
                redirectAttributes.addFlashAttribute("success", "Flat created successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("flatDto", flatDto);
        }

        return "redirect:/admin/flats";
    }

    @GetMapping("/flats/edit/{id}")
    public String editFlat(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return flatService.getFlatById(id).map(flat -> {
            FlatDto dto = new FlatDto(flat.getId(), flat.getFlatNumber(), flat.getWing(), flat.getFloor(), flat.getType());
            model.addAttribute("flatDto", dto);
            model.addAttribute("flats", flatService.getAllFlats());
            model.addAttribute("isEdit", true);
            return "admin/flats";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("error", "Flat not found!");
            return "redirect:/admin/flats";
        });
    }

    @PostMapping("/flats/delete/{id}")
    public String deleteFlat(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            flatService.deleteFlat(id);
            redirectAttributes.addFlashAttribute("success", "Flat deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/flats";
    }

    // =========================================================================
    // Residents CRUD & Login Account Creation
    // =========================================================================
    @GetMapping("/residents")
    public String listResidents(Model model) {
        model.addAttribute("residents", residentService.getAllResidents());
        model.addAttribute("flats", flatService.getAllFlats());
        if (!model.containsAttribute("residentDto")) {
            model.addAttribute("residentDto", new ResidentDto());
        }
        model.addAttribute("isEdit", false);
        return "admin/residents";
    }

    @PostMapping("/residents/save")
    public String saveResident(@Valid @ModelAttribute("residentDto") ResidentDto residentDto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.residentDto", result);
            redirectAttributes.addFlashAttribute("residentDto", residentDto);
            redirectAttributes.addFlashAttribute("error", "Validation failed. Please correct form fields.");
            return "redirect:/admin/residents";
        }

        try {
            if (residentDto.getId() != null) {
                residentService.updateResident(residentDto.getId(), residentDto);
                redirectAttributes.addFlashAttribute("success", "Resident details updated successfully!");
            } else {
                residentService.createResident(residentDto);
                redirectAttributes.addFlashAttribute("success", "Resident and login account created successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("residentDto", residentDto);
        }

        return "redirect:/admin/residents";
    }

    @GetMapping("/residents/edit/{id}")
    public String editResident(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return residentService.getResidentById(id).map(r -> {
            ResidentDto dto = new ResidentDto();
            dto.setId(r.getId());
            dto.setName(r.getUser().getName());
            dto.setEmail(r.getUser().getEmail());
            dto.setPhone(r.getPhone());
            dto.setFlatId(r.getFlat().getId());
            dto.setResidentType(r.getResidentType());

            model.addAttribute("residentDto", dto);
            model.addAttribute("residents", residentService.getAllResidents());
            model.addAttribute("flats", flatService.getAllFlats());
            model.addAttribute("isEdit", true);
            return "admin/residents";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("error", "Resident not found!");
            return "redirect:/admin/residents";
        });
    }

    @PostMapping("/residents/delete/{id}")
    public String deleteResident(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            residentService.deleteResident(id);
            redirectAttributes.addFlashAttribute("success", "Resident and associated login removed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/residents";
    }

    // =========================================================================
    // Maintenance Bills Management
    // =========================================================================
    @GetMapping("/bills")
    public String listBills(@RequestParam(value = "month", required = false, defaultValue = "ALL") String month,
                            @RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
                            Model model) {
        model.addAttribute("bills", billService.filterBills(month, status));
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("monthsList", billService.getDistinctBillingMonths());

        if (!model.containsAttribute("billGenDto")) {
            BillGenerationDto genDto = new BillGenerationDto();
            genDto.setBillingMonth(LocalDate.now().toString().substring(0, 7)); // e.g. 2026-10
            genDto.setAmount(2500.0);
            genDto.setDueDate(LocalDate.now().plusDays(15));
            model.addAttribute("billGenDto", genDto);
        }

        return "admin/bills";
    }

    @PostMapping("/bills/generate")
    public String generateBills(@Valid @ModelAttribute("billGenDto") BillGenerationDto billGenDto,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.billGenDto", result);
            redirectAttributes.addFlashAttribute("billGenDto", billGenDto);
            redirectAttributes.addFlashAttribute("error", "Please provide valid bill generation inputs.");
            return "redirect:/admin/bills";
        }

        try {
            String summary = billService.generateBillsForAllFlats(billGenDto);
            redirectAttributes.addFlashAttribute("success", summary);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/bills";
    }

    // =========================================================================
    // Notices Management
    // =========================================================================
    @GetMapping("/notices")
    public String listNotices(Model model) {
        model.addAttribute("notices", noticeService.getAllNotices());
        if (!model.containsAttribute("noticeDto")) {
            model.addAttribute("noticeDto", new NoticeDto());
        }
        model.addAttribute("isEdit", false);
        return "admin/notices";
    }

    @PostMapping("/notices/save")
    public String saveNotice(@Valid @ModelAttribute("noticeDto") NoticeDto noticeDto,
                             BindingResult result,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.noticeDto", result);
            redirectAttributes.addFlashAttribute("noticeDto", noticeDto);
            redirectAttributes.addFlashAttribute("error", "Please fix notice form errors.");
            return "redirect:/admin/notices";
        }

        try {
            if (noticeDto.getId() != null) {
                noticeService.updateNotice(noticeDto.getId(), noticeDto);
                redirectAttributes.addFlashAttribute("success", "Notice updated successfully!");
            } else {
                String poster = (authentication != null) ? authentication.getName() : "Admin";
                noticeService.createNotice(noticeDto, poster);
                redirectAttributes.addFlashAttribute("success", "Notice published successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/notices";
    }

    @GetMapping("/notices/edit/{id}")
    public String editNotice(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return noticeService.getNoticeById(id).map(notice -> {
            NoticeDto dto = new NoticeDto(notice.getId(), notice.getTitle(), notice.getMessage());
            model.addAttribute("noticeDto", dto);
            model.addAttribute("notices", noticeService.getAllNotices());
            model.addAttribute("isEdit", true);
            return "admin/notices";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("error", "Notice not found!");
            return "redirect:/admin/notices";
        });
    }

    @PostMapping("/notices/delete/{id}")
    public String deleteNotice(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            noticeService.deleteNotice(id);
            redirectAttributes.addFlashAttribute("success", "Notice removed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/notices";
    }

    // =========================================================================
    // Complaints Management
    // =========================================================================
    @GetMapping("/complaints")
    public String listComplaints(Model model) {
        model.addAttribute("complaints", complaintService.getAllComplaints());
        return "admin/complaints";
    }

    @PostMapping("/complaints/{id}/status")
    public String updateComplaintStatus(@PathVariable Long id,
                                        @RequestParam("status") String status,
                                        @RequestParam(value = "remark", required = false) String remark,
                                        RedirectAttributes redirectAttributes) {
        try {
            complaintService.updateComplaintStatus(id, status, remark);
            redirectAttributes.addFlashAttribute("success", "Complaint status updated to " + status + "!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/complaints";
    }

    // =========================================================================
    // Visitors Log
    // =========================================================================
    @GetMapping("/visitors")
    public String listVisitors(Model model) {
        model.addAttribute("visitors", visitorService.getAllVisitors());
        return "admin/visitors";
    }
}
