package com.w1481217.demo;

import Controller.GreetingController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@WebMvcTest(GreetingController.class)
public class ControllerGreetingTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnSomething() throws Exception {
        mockMvc.perform(get("/greeting"))
                .andExpect(status().isOk())
                .andExpect(view().name("greetings"))
                .andExpect(model().attribute("name", "World2"));
    }

    @Test
    public void shouldReturnStatus200WithDefaultValueOfWorld2WhenSentInvalidNamedRequest() throws Exception {
        mockMvc.perform(get("/greeting?google=Hob"))
                .andExpect(status().isOk())
                .andExpect(view().name("greetings"))
                .andExpect(model().attribute("name", "World2"));
    }

    @Test
    public void shouldReturnWhenPassingInAWord() throws Exception {
        mockMvc.perform(get("/greeting?name=Bob"))
                .andExpect(status().isOk())
                .andExpect(view().name("greetings"))
                .andExpect(model().attribute("name", "Bob"));
    }

}
