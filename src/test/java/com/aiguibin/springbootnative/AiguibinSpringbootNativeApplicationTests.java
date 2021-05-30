package com.aiguibin.springbootnative;

import com.aiguibin.springbootnative.controller.HelloWorldController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AiguibinSpringbootNativeApplicationTests {

    private MockMvc mvc;
    private WebApplicationContext webApplicationContext;
    @Before
    public void before(){
        this.mvc=MockMvcBuilders.standaloneSetup(new HelloWorldController()).build();
    }
    @Test
    public void contextLoads() {
        this.webApplicationContext=mvc.getDispatcherServlet().getWebApplicationContext();
    }
    @Test
    public  void helloWorldTest() throws Exception {
        RequestBuilder request=get("/index/world");
        mvc.perform(request).andExpect(status().isOk()).andExpect(content().string("world"));
    }

}
