package com.mutsasns.service;

import com.mutsasns.domain.dto.alarm.AlarmResponse;
import com.mutsasns.domain.entity.Alarm;
import com.mutsasns.domain.entity.User;
import com.mutsasns.repository.AlarmRepository;
import com.mutsasns.repository.UserRepository;
import com.mutsasns.utils.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AlarmService {

	private final UserRepository userRepository;
	private final AlarmRepository alarmRepository;
	private Validator validator;

	private AlarmService(UserRepository userRepository, AlarmRepository alarmRepository) {
		this.userRepository = userRepository;
		this.alarmRepository = alarmRepository;
		this.validator = Validator.builder()
				.userRepository(userRepository)
				.build();
	}

	public List<AlarmResponse> listAlarm(String userName, Pageable pageable) {

		// userName이 있는지 체크
		User user = validator.validatorUser(userName);

		// userName이 받은 알람 조회
		List<Alarm> alarms = alarmRepository.findAllByUser(user);

		List<AlarmResponse> alarmResponses = alarms.stream().map(alarm -> alarm.toResponse())
				.collect(Collectors.toList());

		return alarmResponses;
	}
}
