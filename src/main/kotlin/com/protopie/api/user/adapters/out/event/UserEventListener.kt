package com.protopie.api.user.adapters.out.event

import com.protopie.api.user.adapters.dto.UserDeletedEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener


@Component
class UserEventListener(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleUserDeletedEvent(event: UserDeletedEvent) {
        logger.info("[UserEventListener] Publishing user deleted event: userId=${event.userId}, email=${event.email}")
        kafkaTemplate.send("user.deleted", event.userId.toString(), event)
    }
}
