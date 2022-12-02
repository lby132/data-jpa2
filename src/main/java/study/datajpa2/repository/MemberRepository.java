package study.datajpa2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa2.dto.MemberDto;
import study.datajpa2.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor<Member> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa2.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
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

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    // 페치조인 다른 방법 1
    @Override //JpaRepository에 있는 findAll()을 오버라이딩함
    // jpql로 select m from Member m left join fetch m.team 이렇게 페치조인을
    // 걸어주기 귀찮을때 써도되는 방법 내부적으로는 fetch join을 쓴다고 함.
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    // 페치조인 다른 방법 2 jpql이랑 같이 쓰고 싶을때
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // 페치조인 다른 방법 3 메소드이름이랑 같이 쓰고 싶을때
    //@EntityGraph("Member.all")  Member에서 설정한 NamedEntityGraph에 설정한 이름을 넣어주면 된다. 이 방법은 잘 안씀.
    @EntityGraph(attributePaths = {"team"}) // 간단할때 사용. 아니면 jpql 로 페치조인 사용.
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    //@Lock은 이게 걸려있는 애가 실행되면 다른 곳에서는 손대지 못하게 하는 어노테이션. 옵션마다 기능이 다르다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    <T> List <T> findProjectionsByUsername(@Param("username") String username, Class<T> type);

    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    @Query(value = "select m.member_id as id, m.username, t.name as teamName " +
            "from member m left join team t",
            countQuery = "select count(*) from member",
            nativeQuery = true)
    Page<MemberProject> findByNativeProjection(Pageable pageable);
}

