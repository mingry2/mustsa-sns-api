package com.mutsasns.repository;

import com.mutsasns.domain.entity.Alarm;
import com.mutsasns.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findAllByUser(User user);
}
