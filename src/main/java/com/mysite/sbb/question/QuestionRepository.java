package com.mysite.sbb.question;

import java.util.List;

import com.mysite.sbb.api.questionApi.QuestionApiDTO;
import com.mysite.sbb.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
	Question findBySubject(String subject);
	Question findBySubjectAndContent(String subject, String content);
	List<Question> findBySubjectLike(String subject);
	Page<Question> findAll(Pageable pageable);
	Page<Question> findAll(Specification<Question> spec, Pageable pageable);

	@Query("select "
            + "distinct q "
            + "from Question q " 
            + "left outer join SiteUser u1 on q.author=u1 "
            + "left outer join Answer a on a.question=q "
            + "left outer join SiteUser u2 on a.author=u2 "
            + "where "
            + "   (q.subject like %:kw% "
            + "   or q.content like %:kw% "
            + "   or u1.username like %:kw% "
            + "   or a.content like %:kw% "
            + "   or u2.username like %:kw%) "
			+ "   and q.category = %:c"               //20240730 카테고리
	)
    Page<Question> findAllByKeyword(@Param("kw") String kw, Pageable pageable,@Param("c") Category category);


	@Query("SELECT new com.mysite.sbb.api.questionApi.QuestionApiDTO(q.id, q.subject, q.content, q.createDate, q.author.username, q.modifyDate, q.category.name) FROM Question q")
	List<QuestionApiDTO> findDTOsAll();



	@Query("SELECT new com.mysite.sbb.api.questionApi.QuestionApiDTO(q.id, q.subject, q.content, q.createDate, q.author.username, q.modifyDate, q.category.name) FROM Question q WHERE q.author.username = :username")
	List<QuestionApiDTO> findDTOsByUsername(@Param("username") String username);

}
