package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAdminBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private  PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteUser(final String userUuid, final String authorizationToken) throws AuthorizationFailedException, UserNotFoundException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        UserEntity userEntity = userDao.getUser(userUuid);
        String uuid = userEntity.getUuid();


        if(userAuthTokenEntity == null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        else  if ( userAuthTokenEntity.getLogoutAt()!= null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        }

        else if (userEntity.getRole() == "nonadmin"){
            throw new UserNotFoundException ("USR-001", "Unauthorized Access, Entered user is not an admin");
        }

        else {

            userDao.deleteUser(userEntity);
            return uuid;
        }

    }









}