package com.protopie.api.mail.adapters.`in`.event

import com.protopie.api.user.adapters.dto.UserDeletedEvent
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class UserDeletedMailConsumer {

    private val log = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        id = "mail-service",
        topics = ["user.deleted"],
        containerFactory = "kafkaListenerUserDeletedEventFactory",
    )
    fun handle(message: ConsumerRecord<String, UserDeletedEvent>) {
        val event = message.value()
        log.info("[UserDeletedMailConsumer] 수신: userId=${event.userId}, email=${event.email}")
        log.info("[UserDeletedMailConsumer] 수신: userId=${event.userId}, email=${event.email}")
        log.info("[UserDeletedMailConsumer] 수신: userId=${event.userId}, email=${event.email}")
        log.info("📧 Mail 전송 예시 → 메일 전송 등 비동기 처리 예정")
    }
}
