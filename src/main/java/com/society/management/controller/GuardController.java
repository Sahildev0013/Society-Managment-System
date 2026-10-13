package com.society.management.controller;

import com.society.management.dto.VisitorDto;
import com.society.management.model.Visitor;
import com.society.management.service.FlatService;
import com.society.management.service.VisitorService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/guard")
public class GuardController {

    private final VisitorService visitorService;
    private final FlatService flatService;

    public GuardController(VisitorService visitorService, FlatService flatService) {
        this.visitorService = visitorService;
        this.flatService = flatService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("todayVisitors", visitorService.getTodayVisitors());
        model.addAttribute("flats", flatService.getAllFlats());
        if (!model.containsAttribute("visitorDto")) {
            model.addAttribute("visitorDto", new VisitorDto());
        }
        return "guard/dashboard";
    }

    @PostMapping("/visitors/entry")
    public String logEntry(@Valid @ModelAttribute("visitorDto") VisitorDto visitorDto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.visitorDto", result);
            redirectAttributes.addFlashAttribute("visitorDto", visitorDto);
            redirectAttributes.addFlashAttribute("error", "Please provide all required visitor details accurately.");
            return "redirect:/guard/dashboard";
        }

        try {
            Visitor saved = visitorService.logEntry(visitorDto);
            redirectAttributes.addFlashAttribute("success", "Visitor entry recorded for " + saved.getName() + " (Flat " + saved.getFlat().getShortName() + "). Notification sent for resident approval.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("visitorDto", visitorDto);
        }

        return "redirect:/guard/dashboard";
    }

    @PostMapping("/visitors/{id}/exit")
    public String markExit(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Visitor visitor = visitorService.markExit(id);
            redirectAttributes.addFlashAttribute("success", "Exit logged for visitor " + visitor.getName() + ".");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/guard/dashboard";
    }
}
