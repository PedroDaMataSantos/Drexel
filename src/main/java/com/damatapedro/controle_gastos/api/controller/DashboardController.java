package com.damatapedro.controle_gastos.api.controller;


import com.damatapedro.controle_gastos.application.dto.DashboardResponse;
import com.damatapedro.controle_gastos.application.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scontg/dashboard")

public class DashboardController {

    public final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping
    public DashboardResponse dashboard() {
        return service.dashboard();
    }
}


