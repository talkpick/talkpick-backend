package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.web.adapter;

import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.ClientInfoPort;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class ClientInfoWebAdapter implements ClientInfoPort {

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

    private boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip);
    }
}