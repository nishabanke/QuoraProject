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
        UserEntity adminuserEntity = userAuthTokenEntity.getUser();
        String role = adminuserEntity.getRole();
        UserEntity userEntity = userDao.getUser(userUuid);
		
		/***
		If the user with uuid whose profile is to be deleted does not exist in the database, 
		throw 'UserNotFoundException' with the message code -'USR-001' 
		and message -'User with entered uuid to be deleted does not exist'.
		***/
		
        if (userEntity == null){
            throw new UserNotFoundException ("USR-001", "User with entered uuid to be deleted does not exist");
        }

        String uuid = userEntity.getUuid();
		
		/***
		If the access token provided by the user does not exist in the database 
		throw 'AuthorizationFailedException' with the message code-'ATHR-001' and message -'User has not signed in'.
		***/


        if(userAuthTokenEntity == null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
		
		/***
		If the user has signed out, throw 'AuthorizationFailedException' 
		with the message code- 'ATHR-002' and message -'User is signed out'.
		***/


        if ( userAuthTokenEntity.getLogoutAt()!= null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.");
        }
		
		/***
		If the role of the user is 'nonadmin',  
		throw 'AuthorizationFailedException' with the message code-'ATHR-003' 
		and message -'Unauthorized Access, Entered user is not an admin'.
		***/

        if (role.equals("nonadmin")){
            throw new AuthorizationFailedException ("ATH-003", "Unauthorized Access, Entered user is not an admin");
        }



        userDao.deleteUser(userEntity);
        return uuid;

    }









}