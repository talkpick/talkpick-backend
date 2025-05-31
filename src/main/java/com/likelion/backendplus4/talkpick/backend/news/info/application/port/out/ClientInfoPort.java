// application/port/out/ClientInfoPort.java
package com.likelion.backendplus4.talkpick.backend.news.info.application.port.out;

public interface ClientInfoPort {
    /**
     * 클라이언트의 실제 IP 주소를 반환합니다.
     * nginx 프록시 헤더를 고려하여 실제 클라이언트 IP를 추출합니다.
     *
     * @return 클라이언트 IP 주소
     */
    String getClientIpAddress();
}