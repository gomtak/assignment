package com.protopie.api.auth.jwt

import com.protopie.api.auth.security.CustomUserDetails
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.InvalidKeyException
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.kafka.common.security.oauthbearer.internals.secured.HttpAccessTokenRetriever.AUTHORIZATION_HEADER
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = resolveToken(request)
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token!!)) {
                val userId = jwtTokenProvider.getUserId(token)
                val roleType = jwtTokenProvider.getRoleType(token)
                val authentication = JwtAuthenticationToken(userId, roleType)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: JwtException) {
            logger.error(e.message, e)
            request.setAttribute("exception", "JWT 처리 중 오류가 발생했습니다")
        } catch (e: ExpiredJwtException) {
            logger.error(e.message, e)
            request.setAttribute("exception", "토큰이 만료되었습니다. 새로 로그인해 주세요.")
        } catch (e: UnsupportedJwtException) {
            logger.error(e.message, e)
            request.setAttribute("exception", "지원되지 않는 토큰 형식입니다.")
        } catch (e: MalformedJwtException) {
            logger.error(e.message, e)
            request.setAttribute("exception", "토큰이 올바른 형식이 아닙니다. 요청을 확인해 주세요.")
        } catch (e: InvalidKeyException) {
            logger.error(e.message, e)
            request.setAttribute("exception", "토큰의 권한이 유효하지 않습니다. 요청을 확인해 주세요.")
        } catch (e: RuntimeException) {
            logger.error(e.message, e)
            request.setAttribute("exception", "알 수 없는 오류가 발생했습니다.")
        } catch (e: SignatureException) {
            logger.error(e.message, e)
            request.setAttribute("exception", "토큰의 서명이 유효하지 않습니다. 위조된 토큰일 수 있습니다.")
        } catch (e: IllegalArgumentException) {
            logger.error(e.message, e)
            request.setAttribute("exception", "토큰이 비어있거나 잘못된 값입니다. 요청을 확인해 주세요.")
        }

        filterChain.doFilter(request, response)
    }
    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTHORIZATION_HEADER)

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7)
        }

        return null
    }
    companion object {
        const val BEARER_PREFIX: String = "Bearer "
    }
}
