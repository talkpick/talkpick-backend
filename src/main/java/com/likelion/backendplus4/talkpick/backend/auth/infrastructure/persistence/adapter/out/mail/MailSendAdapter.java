package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.persistence.adapter.out.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.MailSendPort;

import lombok.RequiredArgsConstructor;

/**
 * Spring의 JavaMailSender를 사용하여 인증 코드를 이메일로 전송하는 어댑터입니다.
 *
 * @since 2025-05-20
 */
@Component
@RequiredArgsConstructor
public class MailSendAdapter implements MailSendPort {

	private final JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String FROM;

	/**
	 * 지정된 이메일 주소로 인증 코드를 포함한 메일을 전송합니다.
	 *
	 * @param email 인증 코드를 전송할 대상 이메일 주소
	 * @param verifyCode 전송할 인증 코드
	 * @throws MailException 메일 전송에 실패한 경우
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	@Override
	public void sendMail(String email, String verifyCode) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(email);
		msg.setFrom(FROM);
		msg.setSubject("인증 코드 안내");
		msg.setText("인증 번호는 “" + verifyCode + "” 입니다. 5분 내에 입력해주세요.");
		mailSender.send(msg);
	}
}