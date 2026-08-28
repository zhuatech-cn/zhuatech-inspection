/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.inspection.service;
import jakarta.validation.constraints.*;
import org.springframework.stereotype.Service;
import java.util.*;
@Service public class DomainDecisionService {
 public DecisionResult assess(DecisionRequest request) { if(request.completedPoints()>request.plannedPoints())throw new IllegalArgumentException("已巡检点位不能大于计划点位");if(request.hazardsClosed()>request.hazardsFound())throw new IllegalArgumentException("已关闭隐患不能大于发现隐患");double completion=request.completedPoints()*100d/request.plannedPoints();double closure=request.hazardsFound()==0?100:request.hazardsClosed()*100d/request.hazardsFound();int score=(int)Math.round((completion+closure)/2);List<String> actions=new ArrayList<>();if(completion<100)actions.add("完成漏检点位并说明原因");if(closure<100)actions.add("完成剩余隐患整改复核");if(request.overdueHazards()>0){score-=Math.min(35,request.overdueHazards()*10);actions.add("升级逾期隐患至责任部门");}if(!request.locationVerified()){score-=25;actions.add("补充点位扫码或定位证明");}if(!request.evidenceComplete()){score-=30;actions.add("补齐照片、读数和签名证据");}if(!request.criticalHazardContained()){score-=60;actions.add("立即停止相关作业并控制重大隐患");}return result(score,actions,"CLOSE_READY","REMEDIATE","STOP_WORK",Map.of("completionRate",completion,"hazardClosureRate",closure,"openHazards",request.hazardsFound()-request.hazardsClosed(),"overdueHazards",request.overdueHazards())); }
 private DecisionResult result(int raw,List<String> actions,String good,String warn,String bad,Map<String,Object> metrics) { int score=Math.max(0,Math.min(100,raw));String decision=score>=80?good:score>=50?warn:bad;return new DecisionResult(decision,score,metrics,List.copyOf(actions)); }
 private DecisionResult riskResult(int raw,List<String> actions,String good,String warn,String bad,Map<String,Object> metrics) { int score=Math.max(0,Math.min(100,raw));String decision=score>=70?bad:score>=40?warn:good;return new DecisionResult(decision,score,metrics,List.copyOf(actions)); }
 public record DecisionRequest(
        @NotBlank String taskNo,
        @Positive int plannedPoints,
        @PositiveOrZero int completedPoints,
        @PositiveOrZero int hazardsFound,
        @PositiveOrZero int hazardsClosed,
        @PositiveOrZero int overdueHazards,
        boolean locationVerified,
        boolean evidenceComplete,
        boolean criticalHazardContained) {}
 public record DecisionResult(String decision,int score,Map<String,Object> metrics,List<String> actions) {}
}
