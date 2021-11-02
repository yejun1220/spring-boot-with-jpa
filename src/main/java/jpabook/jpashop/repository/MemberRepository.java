package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

// 컴포넌트 스캔 대상으로, 스프링 빈 자동 등록
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    // 스프링 엔티티 매니저를 만들어 주입 해준다.
    // EntityManger 주입, 여러 개가 쓰이므로 하나씩 주입 해준다.
//    @PersistenceContext
    // 스프링 부트에서 스프링 JPA를 쓰면 @Autowired를 @PersistenceContext로 사용할 수 있다.
    private final EntityManager em;

//    @RequiredArgsConstructor로 생략 가능
//    public MemberRepository(EntityManager em) {
//        this.em = em;
//    }

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        // Member 엔티티 조회
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    public List<Member> findByName(String name) {
        // :변수 는 변수를 setParameter를 통해 바인딩 해주는 것이다.
        return em.createQuery("select m from Member m where m.name = :name", Member.class).setParameter("name", name).getResultList();
    }

}
