/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.inspection.service;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
@Service
public class CapaClosureGovernanceService {
    public Result evaluate(Request request) {
        List<String> gaps = new ArrayList<>();
        if (!request.rootCauseApproved()) gaps.add("根因分析尚未批准");
        if (!request.actionsCompleted()) gaps.add("纠正预防措施尚未全部完成");
        if (!request.evidenceComplete()) gaps.add("整改证据不完整");
        if (!request.effectivenessCheckPassed()) gaps.add("有效性验证未通过");
        if (request.overdueActions() > 0) gaps.add("存在逾期措施: " + request.overdueActions());
        if (request.recurrenceDetected()) gaps.add("复发监测发现同类问题");
        String decision = request.recurrenceDetected() ? "REOPEN"
                : gaps.isEmpty() ? "CLOSE" : "REVIEW";
        int readiness = Math.max(0, 100 - gaps.size() * 18);
        return new Result(request.capaId(), decision, readiness, List.copyOf(gaps), gaps.isEmpty());
    }
    public record Request(@NotBlank String capaId, boolean rootCauseApproved,
                          boolean actionsCompleted, boolean evidenceComplete,
                          boolean effectivenessCheckPassed, @Min(0) int overdueActions,
                          boolean recurrenceDetected) {
        public Request {
            if (capaId == null || capaId.isBlank()) throw new IllegalArgumentException("capaId is required");
            if (overdueActions < 0) throw new IllegalArgumentException("overdueActions must be non-negative");
        }
    }
    public record Result(String capaId, String decision, int closureReadiness,
                         List<String> gaps, boolean closureAllowed) {}
}
