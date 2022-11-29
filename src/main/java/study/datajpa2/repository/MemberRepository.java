package study.datajpa2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa2.dto.MemberDto;
import study.datajpa2.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username);   //다건

    Member findMemberByUsername(String username);   //단건

    Optional<Member> findOptionalByUsername(String username);   //단건

    Page<Member> findByAge(int age, Pageable pageable);

    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m")
    List<Member> findByAge1(int age, Pageable pageable);

    Slice<Member> findByAge2(int age, Pageable pageable);

    //변경할땐 @Modifying 어노테이션을 꼭 넣어주어야한다.
    //clearAutomatically = true는 영속성컨텍스트를 초기화해주는 옵션이다.
    //이유는 벌크성 수정쿼리는 영속성 컨텍스트를 신경쓰지 않고 바로 DB로 업데이트를 쳐버리기때문.
    //그래서 영속성 컨텍스트는 변경되지 않은 값이 남아있게 된다.
    //em.clear()를 하던 clearAutomatically = true를 하던 둘중하나는 해줘야함.
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);
}

