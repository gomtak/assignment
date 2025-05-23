package com.protopie.api.user.adapters.out

import com.protopie.api.common.RoleType
import com.protopie.api.user.adapters.out.mapper.UserMapper.toDomain
import com.protopie.api.user.adapters.out.mapper.UserMapper.toEntity
import com.protopie.api.user.adapters.out.repository.UserRepository
import com.protopie.api.user.application.port.out.UserPort
import com.protopie.api.user.domain.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserAdapter(
    private val userRepository: UserRepository,
) : UserPort {
    override fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    override fun save(user: User): UUID {
        val savedUser = userRepository.save(user.toEntity())
        return savedUser.id ?: error("User ID is null")
    }

    override fun findByEmail(email: String): User? {
        val userEntity = userRepository.findByEmail(email)
        return userEntity?.toDomain()
    }

    override fun getUserById(userId: UUID): User? {
        val userEntity = userRepository.findById(userId)
        return if (userEntity.isPresent) {
            userEntity.get().toDomain()
        } else {
            null
        }
    }

    override fun updateUser(user: User) {
        val userEntity = userRepository.getReferenceById(user.id)
        userEntity.update(
            password = user.password,
            email = user.email,
            name = user.name,
            role = user.roleType,
        )
    }

    override fun deleteUser(userId: UUID) {
        val userEntity = userRepository.getReferenceById(userId)
        userRepository.delete(userEntity)
    }

    override fun getAllUsers(pageable: Pageable): Page<User> {
        val userEntities = userRepository.findAllByRoleNot(RoleType.ROLE_ADMIN, pageable)
        return userEntities.map { it.toDomain() }
    }
}
