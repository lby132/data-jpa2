package study.datajpa2.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa2.dto.MemberDto;
import study.datajpa2.entity.Member;
import study.datajpa2.entity.Team;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    void testMember() {
        final Member member = new Member("memberA");
        final Member savedMember = memberRepository.save(member);
        final Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void basicCRUD() {
        final Member member1 = new Member("member1");
        final Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        final Member findMember1 = memberRepository.findById(member1.getId()).get();
        final Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        final List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        final long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        final long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    void findByUsernameAndAgeGreaterThan() {
        final Member m1 = new Member("AAA", 10);
        final Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        final List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void testNamedQuery() {
        final Member m1 = new Member("AAA", 10);
        final Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        final List<Member> result = memberRepository.findByUsername("AAA");
        final Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    void testQuery() {
        final Member m1 = new Member("AAA", 10);
        final Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        final List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    void findUsernameList() {
        final Member m1 = new Member("AAA", 10);
        final Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        final List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    void findMemberDto() {
        final Team team = new Team("teamA");
        teamRepository.save(team);

        final Member m1 = new Member("AAA", 10);
        memberRepository.save(m1);

        final List<MemberDto> memberDto = memberRepository.findMemberDto();

        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    void findByNames() {
        final Member m1 = new Member("AAA", 10);
        final Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        final List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    void returnType() {
        final Member m1 = new Member("AAA", 10);
        final Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        //final List<Member> aaa = memberRepository.findListByUsername("AAA");
        //final Member aaa = memberRepository.findMemberByUsername("AAA");
        //final Optional<Member> aaa = memberRepository.findOptionalByUsername("AAA");
    }

    @Test
    void paging() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        //이름순으로 정렬된 값을 가져옴
        final PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        //반환 타입이 Page면 totalCount쿼리까지 같이 날린다.
        final Page<Member> page = memberRepository.findByAge(age, pageRequest);

        //일반적인 페이징처리방법. 데이터가 뒤에 더있고 상관없이 내가 정한 갯수만큼만 페이징처리함
        List<Member> page1 = memberRepository.findByAge1(age, pageRequest);

        //반환 타입이 Slice totalCount를 날리지 않는다.
        Slice<Member> page2 = memberRepository.findByAge2(age, pageRequest);

        //responseBody로 내보내기 위해 Page는 map()이라는 메소드를 제공한다.
        final Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        //then
        final List<Member> content = page.getContent();
        final long totalElements = page.getTotalElements();
        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

        //Slice는 토탈카운트를 제공하지 않는다. 그리고 페이징 사이즈도 내가 정한것에 +1을 해서 보내줌. (모바일 환경에서 더보기 버튼 같은 기능제공)
        assertThat(content.size()).isEqualTo(3);
        assertThat(page2.getNumber()).isEqualTo(0);
        assertThat(page2.isFirst()).isTrue();
        assertThat(page2.hasNext()).isTrue();
    }

    @Test
    void bulkUpdate() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        int resultCount = memberRepository.bulkAgePlus(20);
//        em.flush();
//        em.clear();

        final List<Member> result = memberRepository.findByUsername("member5");
        final Member member = result.get(0);

        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    void findMemberLazy() {
        final Team teamA = new Team("teamA");
        final Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        final Member member1 = new Member("member1", 10, teamA);
        final Member member2 = new Member("member1", 10, teamA);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //Member만 가져오는 쿼리가 나감
        //final List<Member> members = memberRepository.findAll();
        //final List<Member> members = memberRepository.findMemberFetchJoin();
        final List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        for (Member member : members) {
            //Member에 있는 데이터 출력
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam = " + member.getTeam().getClass());
            //member.getTeam()여기까지는 쿼리가 안나감 프록시로 가짜 객체를 가져와서 team Select쿼리 나감 getName()을 호출했을때 진짜 DB에서 조회해서 가져옴
            System.out.println("member.team.name = " + member.getTeam().getName());
        }
    }
}