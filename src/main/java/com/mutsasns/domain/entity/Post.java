package com.mutsasns.domain.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private String title;
    private String body;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


}
