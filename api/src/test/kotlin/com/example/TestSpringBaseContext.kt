package com.example

import com.example.config.JacksonConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc

@ActiveProfiles("test")
@SpringBootTest(classes = [TestApplication::class, JacksonConfig::class])
@AutoConfigureMockMvc
abstract class TestSpringBaseContext {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var jacksonConfig: JacksonConfig
}
