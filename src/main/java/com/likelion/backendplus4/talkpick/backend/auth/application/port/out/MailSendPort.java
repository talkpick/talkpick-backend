package com.likelion.backendplus4.talkpick.backend.auth.application.port.out;

import org.springframework.mail.MailException;

/**
 * MailSender를 사용하여 인증 코드를 이메일로 전송하는 포트입니다.
 *
 * @since 2025-05-20
 */
public interface MailSendPort {

	/**
	 * 지정된 이메일 주소로 인증 코드를 포함한 메일을 전송합니다.
	 *
	 * @param email 인증 코드를 전송할 대상 이메일 주소
	 * @param verifyCode 전송할 인증 코드
	 * @throws MailException 메일 전송에 실패한 경우
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	void sendMail(String email, String verifyCode);
}
