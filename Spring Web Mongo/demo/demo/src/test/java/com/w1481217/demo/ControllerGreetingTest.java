package com.w1481217.demo;

import Controller.GreetingController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {GreetingController.class})
@WebAppConfiguration
public class ControllerGreetingTest {

    private MockMvc mockMvc;

    @Before
    public void setup(){
        this.mockMvc = standaloneSetup(new GreetingController()).build();
    }

    @Test
        public void shouldReturnSomething() throws Exception {
            mockMvc.perform(get("/greeting"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("greetings"))
                    .andExpect(model().attribute("name","World2"));
        }

        @Test
        public void shouldReturnStatus404() throws Exception {
            mockMvc.perform(get("/greetingFAKEURL"))
                    .andExpect(status().isNotFound());
        }

        @Test
        public void shouldReturnWhenPassingInAWord() throws Exception {
            mockMvc.perform(get("/greeting?name=Bob"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("greetings"))
                    .andExpect(model().attribute("name","Bob"));
        }

}
