package com.protopie.api.auth.jwt

import com.protopie.api.auth.adapters.dto.TokenResponse
import com.protopie.api.auth.security.CustomUserDetails
import com.protopie.api.common.RoleType
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.Date
import java.util.UUID

@Component
class JwtTokenProvider(
    @Value("\${jwt.access-token-lifetime-in-seconds}")
    private val accessTokenLifetimeInSeconds: Long,
    @Value("\${jwt.refresh-token-lifetime-in-seconds}")
    private val refreshTokenLifetimeInSeconds: Long,
    @Value("\${jwt.secret-key}")
    private val secretKey: String,
) {

    fun createToken(userDetails: CustomUserDetails): TokenResponse {
        val key = key
        val accessTokenLifetimeInMs = accessTokenLifetimeInSeconds * 1000
        val refreshTokenLifetimeInMs = refreshTokenLifetimeInSeconds * 1000

        val accessToken =
            Jwts.builder()
                .setSubject(userDetails.id.toString())
                .setExpiration(Date(System.currentTimeMillis() + accessTokenLifetimeInMs))
                .setNotBefore(Date())
                .setIssuedAt(Date())
                .setId(UUID.randomUUID().toString())
                .claim(AUTHORITIES_KEY, userDetails.roleType)
                .claim(EMAIL, userDetails.email)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact()

        val refreshToken =
            Jwts.builder()
                .setSubject(userDetails.id.toString())
                .setExpiration(Date(System.currentTimeMillis() + refreshTokenLifetimeInMs))
                .setNotBefore(Date())
                .setIssuedAt(Date())
                .setId(UUID.randomUUID().toString())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact()

        return TokenResponse(
            accessToken,
            refreshToken,
        )
    }

    private val key: Key
        get() {
            val keyBytes = Decoders.BASE64.decode(secretKey)
            return Keys.hmacShaKeyFor(keyBytes)
        }

    private fun parseClaims(accessToken: String?): Claims {
        val key = key
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).body
    }

    fun validateToken(token: String): Boolean {
        val claims = parseClaims(token)
        return !isTokenExpired(claims)
    }

    // 토큰 만료 확인
    private fun isTokenExpired(claims: Claims): Boolean {
        return claims.expiration.before(Date())
    }

    fun getUserId(token: String): UUID {
        val userId = parseClaims(token).subject
        return UUID.fromString(userId)
    }

    fun getRoleType(token: String): RoleType {
        val strRoleType = parseClaims(token)[AUTHORITIES_KEY] as String
        return RoleType.valueOf(strRoleType)
    }

    companion object {
        private const val AUTHORITIES_KEY = "role"
        private const val EMAIL = "email"
    }
}
