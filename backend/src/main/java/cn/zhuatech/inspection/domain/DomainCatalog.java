/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.inspection.domain;
import org.springframework.stereotype.Component;
import java.util.*;
@Component
public class DomainCatalog {
    private final Map<String, WorkflowAction> actions = new LinkedHashMap<>();
    public DomainCatalog() {
        actions.put("SUBMIT", new WorkflowAction("SUBMIT", "提交巡检结果", List.of("草稿"), "待复核", "OPERATOR"));
        actions.put("VERIFY", new WorkflowAction("VERIFY", "完成问题复核", List.of("待复核"), "整改中", "ADMIN"));
        actions.put("CLOSE", new WorkflowAction("CLOSE", "确认整改销项", List.of("整改中"), "已关闭", "ADMIN"));
    }
    public String systemName() { return "知华科技企业移动巡检与隐患整改系统"; }
    public String scene() { return "巡检标准、区域点位、计划、任务、移动执行、异常隐患、整改、复核、统计与审计"; }
    public String initialStatus() { return "草稿"; }
    public String partyLabel() { return "区域/点位/责任部门"; }
    public String amountLabel() { return "风险损失"; }
    public String quantityLabel() { return "巡检点数量"; }
    public String dueLabel() { return "整改期限"; }
    public List<ModuleDefinition> modules() { return List.of(
            new ModuleDefinition("STANDARD", "巡检标准", "维护检查项、方法、阈值、照片要求和判定规则"),
            new ModuleDefinition("LOCATION", "区域与点位", "管理园区、区域、设备、二维码和责任归属"),
            new ModuleDefinition("PLAN", "巡检计划", "配置周期、路线、人员、班组、日历和漏检升级"),
            new ModuleDefinition("TASK", "任务调度", "自动生成、派发、改派、离线缓存和进度跟踪"),
            new ModuleDefinition("MOBILE", "移动巡检", "支持扫码、定位、拍照、读数、签名和离线提交"),
            new ModuleDefinition("HAZARD", "异常与隐患", "按风险等级登记问题、临时措施和责任人"),
            new ModuleDefinition("REMEDIATION", "整改管理", "管理措施、期限、证据、逾期提醒和升级"),
            new ModuleDefinition("VERIFICATION", "复核销项", "执行独立复核、退回、关闭和复发跟踪"),
            new ModuleDefinition("ANALYTICS", "巡检分析", "分析完成率、漏检率、隐患趋势和整改时效")
        ); }
    public Map<String, WorkflowAction> actions() { return Collections.unmodifiableMap(actions); }
    public record ModuleDefinition(String code,String name,String description) {}
    public record WorkflowAction(String code,String label,List<String> from,String to,String requiredRole) {}
}
