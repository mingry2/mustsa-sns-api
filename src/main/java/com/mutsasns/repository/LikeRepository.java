package com.mutsasns.repository;

import com.mutsasns.domain.entity.Like;
import com.mutsasns.domain.entity.Post;
import com.mutsasns.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);
    Long countByPost(Post post);
    void deleteAllByPost(Post post);

}
