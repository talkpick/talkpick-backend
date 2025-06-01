package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.web.adapter;

import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.ClientInfoPort;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 클라이언트 정보 조회를 담당하는 웹 어댑터입니다.
 *
 * @since 2025-06-01 최초 작성
 */
@Component
public class ClientInfoWebAdapter implements ClientInfoPort {

    /**
     * 클라이언트의 실제 IP 주소를 반환합니다.
     * nginx 프록시 헤더를 고려하여 실제 클라이언트 IP를 추출합니다.
     *
     * 우선순위: X-Real-IP → X-Forwarded-For → RemoteAddr
     *
     * @return 클라이언트 IP 주소
     * @author 양병학
     * @since 2025-06-01 최초 작성
     */
    @Override
    public String getClientIpAddress() {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();

        String xRealIp = request.getHeader("X-Real-IP");
        if (isValidIp(xRealIp)) {
            return xRealIp;
        }

        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (isValidIp(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    /**
     * IP 주소가 유효한지 검증합니다.
     *
     * @param ip 검증할 IP 주소
     * @return IP 주소 유효성 여부
     * @author 양병학
     * @since 2025-06-01 최초 작성
     */
    private boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip);
    }
}