package com.mysite.sbb.answer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Service
public class AnswerService {

	private final AnswerRepository answerRepository;

	public Answer create(Question question, String content, SiteUser author) {
		Answer answer = new Answer();
		answer.setContent(content);
		answer.setCreateDate(LocalDateTime.now());
		answer.setQuestion(question);
		answer.setAuthor(author);
		this.answerRepository.save(answer);
		return answer;
	}

	public Answer getAnswer(Integer id) {
		Optional<Answer> answer = this.answerRepository.findById(id);
		if (answer.isPresent()) {
			return answer.get();
		} else {
			throw new DataNotFoundException("answer not found");
		}
	}

	public void modify(Answer answer, String content) {
		answer.setContent(content);
		answer.setModifyDate(LocalDateTime.now());
		this.answerRepository.save(answer);
	}

	public void delete(Answer answer) {
		this.answerRepository.delete(answer);
	}

	public void vote(Answer answer, SiteUser siteUser) {
		answer.getVoter().add(siteUser);
		this.answerRepository.save(answer);
	}

	public Page<Answer> getList(int page){
		Pageable pageable = PageRequest.of(page, 10);
		return this.answerRepository.findAll(pageable);

	}
	public Page<Answer> getList(Question question, int page, String order) {
		List<Sort.Order> sorts = new ArrayList<>();
		//int question_id = question.getId();
		if(Objects.equals(order, "recent")) {
			sorts.add(Sort.Order.desc("createDate"));
		}
		else{
			sorts.add(Sort.Order.desc("voter"));
			sorts.add(Sort.Order.desc("createDate"));
		}
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		return this.answerRepository.findAllByQuestion(question, pageable);


	}
}