
package com.rakesh.expensetracker;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.rakesh.expensetracker.service.analytics.AnalyticsInitializationService;

@SpringBootTest(
	    properties = {
	        "springdoc.api-docs.enabled=false",
	        "springdoc.swagger-ui.enabled=false"
	    }
	)
@AutoConfigureMockMvc(addFilters = false)
@Disabled
class ExpenseTrackerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private AnalyticsInitializationService analyticsInitializationService;

    @Test
    void testRegisterEndpoint() throws Exception {

        String request = """
                {
                    "name":"Integration User",
                    "email":"integration1@test.com",
                    "password":"password123"
                }
                """;

        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        )
        .andExpect(status().isOk());
    }
}