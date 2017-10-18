package com.w1481217.demo;

import Constants.Constants;
import Controller.PostToDatabaseController;
import DAO.DAOInterface;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PostToDatabaseController.class)
public class PostToDatabaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    DAOInterface daoInterface;

    @Test
    public void shouldReturnA200WhenSentACorrectRequest() throws Exception {
        mockMvc.perform(
                get("/posttodb?id=123&longitude=TESTING&latitude=TESTING&status=TESTING"))
                .andExpect(status().isOk());

        verify(daoInterface, atLeastOnce()).addGPSEntry("123",
                "TESTING",
                "TESTING",
                "TESTING",
                Constants.database,
                Constants.collection);
    }

    @Test
    public void shouldReturnA400WhenSentABadRequest() throws Exception {
        mockMvc.perform(get("/posttodb?id=3"))
                .andExpect(status().isBadRequest());

        verify(daoInterface, times(0)).addGPSEntry("123",
                "TESTING",
                "TESTING",
                "TESTING",
                "TESTING",
                "TESTING");
    }


}
