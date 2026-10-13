package com.acme.salary;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void seedsTenThousandEmployeesAndReturnsBoundedPages() throws Exception {
        mockMvc.perform(get("/api/employees").param("size", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(10000)))
                .andExpect(jsonPath("$.content", hasSize(7)));
    }

    @Test
    void searchesAndFiltersEmployeesWithoutLoadingTheWholeDirectory() throws Exception {
        mockMvc.perform(get("/api/employees")
                        .param("search", "employee00001")
                        .param("country", "United States"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content[0].employeeNumber", is("AC00001")));
    }

    @Test
    void reportsPayrollByCurrencyAndCountry() throws Exception {
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeCount", is(10000)))
                .andExpect(jsonPath("$.payrollByCurrency", hasSize(9)))
                .andExpect(jsonPath("$.payrollByCountry", hasSize(10)))
                .andExpect(jsonPath("$.payrollByCountry[0].currency").exists())
                .andExpect(jsonPath("$.payrollByCountry[0].totalPayroll").exists());
    }

    @Test
    void createsUpdatesAndDeletesAnEmployee() throws Exception {
        String employee = "{\"employeeNumber\":\"AC-TEST-1\",\"firstName\":\"Ari\","
                + "\"lastName\":\"Example\",\"email\":\"ari@example.test\","
                + "\"country\":\"United States\",\"department\":\"Engineering\","
                + "\"jobTitle\":\"Engineer\",\"currency\":\"USD\","
                + "\"annualSalary\":125000.50,\"hireDate\":\"2024-01-15\"}";

        MvcResult created = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON).content(employee))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.annualSalary", is(125000.5)))
                .andReturn();
        String id = com.jayway.jsonpath.JsonPath.read(created.getResponse().getContentAsString(), "$.id").toString();

        String updated = employee.replace("Ari", "Aria").replace("125000.50", "130000");
        mockMvc.perform(put("/api/employees/" + id)
                        .contentType(MediaType.APPLICATION_JSON).content(updated))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Aria")))
                .andExpect(jsonPath("$.annualSalary", is(130000)));

        mockMvc.perform(delete("/api/employees/" + id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/employees/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsInvalidAndDuplicateEmployeeRecords() throws Exception {
        String invalid = "{\"employeeNumber\":\"AC-BAD\",\"firstName\":\"A\","
                + "\"lastName\":\"B\",\"email\":\"not-an-email\",\"country\":\"India\","
                + "\"department\":\"Engineering\",\"jobTitle\":\"Engineer\","
                + "\"currency\":\"XYZ\",\"annualSalary\":0,\"hireDate\":\"2024-01-15\"}";
        mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.currency").exists())
                .andExpect(jsonPath("$.annualSalary").exists());

        String duplicate = "{\"employeeNumber\":\"AC00001\",\"firstName\":\"Ari\","
                + "\"lastName\":\"Example\",\"email\":\"new@example.test\","
                + "\"country\":\"United States\",\"department\":\"Engineering\","
                + "\"jobTitle\":\"Engineer\",\"currency\":\"USD\","
                + "\"annualSalary\":125000,\"hireDate\":\"2024-01-15\"}";
        mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content(duplicate))
                .andExpect(status().isConflict());
    }

    @Test
    void rejectsUnsupportedSortFieldsAndCapsPageSize() throws Exception {
        mockMvc.perform(get("/api/employees").param("sort", "password"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/employees").param("size", "500").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(100)));
    }
}
