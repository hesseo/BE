package com.phraiz.back.member.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id; // 멤버 식별자

}
