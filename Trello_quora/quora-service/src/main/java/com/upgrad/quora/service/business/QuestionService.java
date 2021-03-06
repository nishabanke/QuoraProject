package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class QuestionService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    /**
     * Creates question in the DB if the accessToken is valid.
     *
     * @param accessToken accessToken of the user for valid authentication.
     * @throws AuthorizationFailedException ATHR-001 - if user token is not present in DB. ATHR-002 if the user has already signed out.
     */
    @Transactional
    public QuestionEntity createQuestion(QuestionEntity questionEntity, final String accessToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }
        questionEntity.setDate(ZonedDateTime.now());
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setUserEntity(userAuthTokenEntity.getUser());
        return questionDao.createQuestion(questionEntity);
    }

    /**
     * Gets all the questions in the DB.
     *
     * @param accessToken accessToken of the user for valid authentication.
     * @return List of QuestionEntity
     * @throws AuthorizationFailedException ATHR-001 - if user token is not present in DB. ATHR-002 if the user has already signed out.
     */
    public List<QuestionEntity> getAllQuestions(final String accessToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        }
        return questionDao.getAllQuestions();
    }

    /**
     * * Edit the question
     *
     * @param accessToken accessToken of the user for valid authentication.
     * @param questionId  id of the question to be edited.
     * @param content     new content for the existing question.
     * @return QuestionEntity
     * @throws AuthorizationFailedException ATHR-001 - if user token is not present in DB. ATHR-002 if the user has already signed out.
     * @throws InvalidQuestionException     if the question with id doesn't exist.
     */
    @Transactional
    public QuestionEntity editQuestion(final String accessToken, final String questionId, final String content) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        }
        QuestionEntity questionEntity = questionDao.getQuestionById(questionId);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        if (!questionEntity.getUserEntity().getUuid().equals(userAuthTokenEntity.getUser().getUuid())) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }
        questionEntity.setContent(content);
        questionDao.updateQuestion(questionEntity);
        return questionEntity;
    }
}
