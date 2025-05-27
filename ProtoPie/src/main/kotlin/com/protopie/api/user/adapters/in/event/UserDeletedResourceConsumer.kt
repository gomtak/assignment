package com.protopie.api.user.adapters.`in`.event

import com.protopie.api.user.adapters.dto.UserDeletedEvent
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component


@Component
class UserDeletedResourceConsumer {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        id = "user-service",
        topics = ["user.deleted"],
        containerFactory = "kafkaListenerUserDeletedEventFactory",
    )
    fun handle(message: ConsumerRecord<String, UserDeletedEvent>) {
        val event = message.value()
        logger.info("[UserDeletedResourceConsumer] 수신: userId=${event.userId}, email=${event.email}")
        logger.info("[UserDeletedResourceConsumer] 수신: userId=${event.userId}, email=${event.email}")
        logger.info("[UserDeletedResourceConsumer] 수신: userId=${event.userId}, email=${event.email}")
        logger.info("🧹 정리할 리소스 예시 → 파일 삭제, 메일 전송 등 비동기 처리 예정")
    }
}
