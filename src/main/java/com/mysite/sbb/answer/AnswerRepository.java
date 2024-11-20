package com.mysite.sbb.answer;

import com.mysite.sbb.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {


    @Query(value = "SELECT a.* " +
            "FROM answer a " +
            "LEFT JOIN answer_voter av ON a.id = av.answer_id " +
            "LEFT JOIN site_user su ON av.voter_id = su.id " +
            "WHERE a.question_id = :questionId " +
            "GROUP BY a.id " +
            "ORDER BY COUNT(av.voter_id) DESC, a.create_date DESC",
            nativeQuery = true)
    Page<Answer> findAnswersByQuestionOrdered(@Param("questionId") Integer questionId, Pageable pageable);

    Page<Answer> findAllByQuestion(Question question, Pageable pageable);
}
