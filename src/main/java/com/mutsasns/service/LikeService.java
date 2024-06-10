package com.mutsasns.service;

import com.mutsasns.domain.dto.like.LikeAddResponse;
import com.mutsasns.domain.entity.*;
import com.mutsasns.repository.*;
import com.mutsasns.utils.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@Slf4j
public class LikeService {

	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final AlarmRepository alarmRepository;
	private final LikeRepository likeRepository;
	private final Validator validator;

	public LikeService(UserRepository userRepository, PostRepository postRepository,
			AlarmRepository alarmRepository, LikeRepository likeRepository) {
		this.userRepository = userRepository;
		this.postRepository = postRepository;
		this.alarmRepository = alarmRepository;
		this.likeRepository = likeRepository;
		this.validator = Validator.builder()
				.userRepository(userRepository)
				.postRepository(postRepository)
				.build();
	}


	// 좋아요 누르기
	public LikeAddResponse addLike(Long postId, String userName) {

		// userName이 있는지 체크
		User user = validator.validatorUser(userName);

		// postId가 있는지 체크
		Post post = validator.validatorPost(postId);

		// postId에 userName의 like가 있는지 확인(isPresent 사용을 위해 Optional로 구현)
		Optional<Like> optionalLike = likeRepository.findByUserAndPost(user, post);

		if (optionalLike.isPresent()) { // like가 존재한다면,
			likeRepository.delete(optionalLike.get()); // like를 삭제한다.

			return LikeAddResponse.builder()
					.message("좋아요를 취소했습니다.")
					.build();

		} else { // like가 존재하지 않는다면,
			likeRepository.save(Like.addLike(user, post)); // like를 저장한다.
			alarmRepository.save(Alarm.addAlarm(AlarmType.NEW_LIKE_ON_POST, user, post)); // 알람 저장

		}

		return LikeAddResponse.builder()
				.message("좋아요를 눌렀습니다.")
				.build();

	}

	// 좋아요 개수
	public Long countLike(Long postId, String userName) {

		// postId가 있는지 체크
		Post post = validator.validatorPost(postId);

		// postId에 like 수 확인
		Long count = likeRepository.countByPost(post);

		return count;

	}
}
