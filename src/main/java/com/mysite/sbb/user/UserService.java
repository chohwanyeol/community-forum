package com.mysite.sbb.user;

import java.util.Optional;

import com.mysite.sbb.mail.EmailException;
import com.mysite.sbb.mail.MailRole;
import com.mysite.sbb.mail.TempPasswordMail;
import com.mysite.sbb.CommonUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mysite.sbb.DataNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TempPasswordMail tempPasswordMail;
	private final CommonUtil commonUtil;



	public SiteUser create(String username, String email, String password) {
		SiteUser user = new SiteUser();
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));
		this.userRepository.save(user);
		return user;
	}

	public SiteUser getUser(String username) {
		Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
		if (siteUser.isPresent()) {
			return siteUser.get();
		} else {
			throw new DataNotFoundException("siteuser not found");
		}
	}

	@Transactional
	public boolean changePassword(String username, String password, String newPassword) {
		Optional<SiteUser> _siteUser = this.userRepository.findByusername(username);
		SiteUser siteUser = new SiteUser();
		if (_siteUser.isPresent()) {
			siteUser = _siteUser.get();
		}
		if (passwordEncoder.matches(password,siteUser.getPassword())){
			siteUser.setPassword(passwordEncoder.encode(newPassword));
			return true;
		}
		else{
			return false;
		}

	}

	@Transactional
	public void mailTempPassword(String userName,String email) throws EmailException {
		String tempPassword = commonUtil.createTempPassword();
		SiteUser user = userRepository.findByusername(userName)
				.orElseThrow(()->new DataNotFoundException("아이디를 확인해 주세요. 해당 유저가 없습니다."));
		user = userRepository.findByEmail(email)
				.orElseThrow(() -> new DataNotFoundException("이메일을 확인해 주세요. 해당 유저가 없습니다."));
		user.setPassword(passwordEncoder.encode(tempPassword));
		userRepository.save(user);
		tempPasswordMail.sendSimpleMessage(email, tempPassword, MailRole.PASSWORD.getValue());
	}

	@Transactional
	public void mailTempDeviceInfo(String userName,String email, String DeviceInfo) throws EmailException {
		SiteUser user = userRepository.findByusername(userName)
				.orElseThrow(()->new DataNotFoundException("아이디를 확인해 주세요. 해당 유저가 없습니다."));
		user = userRepository.findByEmail(email)
				.orElseThrow(() -> new DataNotFoundException("이메일을 확인해 주세요. 해당 유저가 없습니다."));
		tempPasswordMail.sendSimpleMessage(email, DeviceInfo, MailRole.DEVICEINFO.getValue());
	}




}
