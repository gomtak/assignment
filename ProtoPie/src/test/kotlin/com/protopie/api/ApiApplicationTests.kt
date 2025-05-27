package com.protopie.api

import com.protopie.api.config.TestContainerConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ApiApplicationTests: TestContainerConfig() {

    @Test
    fun contextLoads() {
    }

}
