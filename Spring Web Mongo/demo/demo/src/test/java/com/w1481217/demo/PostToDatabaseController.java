package com.w1481217.demo;

import DAO.DAOInterface;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PostToDatabaseController.class)
public class PostToDatabaseController {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    DAOInterface daoInterface;

    @Test
    public void shouldReturnA200WhenSentACorrectRequest() throws Exception {
        mockMvc.perform(
                get("/posttodb?id=3&longitude=3222&latitude=55&status=TESTDATA")
                .contextPath("/posttodb"))
                .andExpect(status().isOk());
    }


}
