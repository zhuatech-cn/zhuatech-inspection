/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.inspection.controller;
import cn.zhuatech.inspection.common.ApiResponse;
import cn.zhuatech.inspection.service.CapaClosureGovernanceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/enterprise/inspection")
public class CapaClosureGovernanceController {
    private final CapaClosureGovernanceService service;
    public CapaClosureGovernanceController(CapaClosureGovernanceService service) { this.service = service; }
    @PostMapping("/capa-readiness")
    public ApiResponse<CapaClosureGovernanceService.Result> evaluate(
            @Valid @RequestBody CapaClosureGovernanceService.Request request) { return ApiResponse.ok(service.evaluate(request)); }
}
