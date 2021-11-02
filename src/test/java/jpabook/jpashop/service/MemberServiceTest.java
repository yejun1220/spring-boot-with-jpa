package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.fail;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception {
        // given
        Member member = new Member();
        member.setName("Lim");

        // when
        Long saveId = memberService.join(member);

        // then
        Assertions.assertThat(member).isEqualTo(memberService.findOne(saveId));
    }

    @Test // junit4의 @Test excepted를 통해 간단하게 표현 가능하다.
   public void 중복_회원_조회() throws Exception {
        // given
        Member member1 = new Member();
        member1.setName("Lim");

        Member member2 = new Member();
        member2.setName("Lim");

        // when
        memberService.join(member1);

        try {
         memberService.join(member2); // 예외 발생
        }
        catch (IllegalStateException e) {
            return;
        }

        // then
        fail("예외가 발생해야 합니다."); // 강제 예외 발생
    }
//


}