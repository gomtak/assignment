package com.protopie.api.config

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.GenericContainer
import org.testcontainers.kafka.ConfluentKafkaContainer
import org.testcontainers.utility.DockerImageName
import kotlin.apply

@ContextConfiguration(initializers = [TestContainerConfig.Initializer::class])
abstract class TestContainerConfig {

    companion object {
        val postgresContainer = GenericContainer<Nothing>("postgres:15-alpine")
            .apply {
                withExposedPorts(5432)
                withEnv("POSTGRES_USER", "testuser")
                withEnv("POSTGRES_PASSWORD", "testpassword")
                withEnv("POSTGRES_DB", "testdb")
            }

        val kafkaContainer = ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.1"))

    }

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            postgresContainer.start()
            kafkaContainer.start()

            TestPropertyValues.of(
                // postgres
                "spring.datasource.url=jdbc:postgresql://${postgresContainer.host}:${postgresContainer.firstMappedPort}/testdb",
                "spring.datasource.username=testuser",
                "spring.datasource.password=testpassword",

                // kafka
                "spring.kafka.bootstrap-servers=${kafkaContainer.bootstrapServers}",

                // jpa
                "spring.jpa.hibernate.ddl-auto=update",
                "spring.jpa.show-sql=true",
                "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect",
            ).applyTo(applicationContext.environment)

        }
    }
}
