/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.inspection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc class DomainDecisionApiTests {
 @Autowired MockMvc mvc;
 @Test void domainDecisionReturnsAuditableScoreMetricsAndActions() throws Exception {
  mvc.perform(post("/api/domain/decision").with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content("{\"taskNo\":\"INS-2026-1008\",\"plannedPoints\":50,\"completedPoints\":50,\"hazardsFound\":3,\"hazardsClosed\":3,\"overdueHazards\":0,\"locationVerified\":true,\"evidenceComplete\":true,\"criticalHazardContained\":true}"))
   .andExpect(status().isOk()).andExpect(jsonPath("$.data.decision").isString()).andExpect(jsonPath("$.data.score").isNumber()).andExpect(jsonPath("$.data.metrics").isMap()).andExpect(jsonPath("$.data.actions").isArray());
 }
 @Test void domainRiskScenarioReturnsExpectedBlockingDecision() throws Exception {
  mvc.perform(post("/api/domain/decision").with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content("{\"taskNo\":\"INS-2026-1008\",\"plannedPoints\":50,\"completedPoints\":5,\"hazardsFound\":20,\"hazardsClosed\":0,\"overdueHazards\":10,\"locationVerified\":false,\"evidenceComplete\":false,\"criticalHazardContained\":false}"))
   .andExpect(status().isOk()).andExpect(jsonPath("$.data.decision").value("STOP_WORK")).andExpect(jsonPath("$.data.actions").isNotEmpty());
 }
 @Test void domainDecisionRequiresAuthentication() throws Exception {mvc.perform(post("/api/domain/decision").contentType(MediaType.APPLICATION_JSON).content("{}" )).andExpect(status().isUnauthorized());}
}
