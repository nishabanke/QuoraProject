package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class AuthenticationService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;


    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity authenticate(final String username, final String password) throws AuthenticationFailedException {
        UserEntity userEntity = userDao.getUserByUsername(username);
		
		/***
		If the username provided by the user does not exist, 
		throw "AuthenticationFailedException" with the message code -
		'ATH-001' and message-'This username does not exist'.
		***/
        if(userEntity == null){
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        final String encryptedPassword = cryptographyProvider.encrypt(password, userEntity.getSalt());
		
		/***
		If the credentials provided by the user match the details in the database, save the user login information in the database and return the 'uuid' of the authenticated user from 'users' table and message 
		'SIGNED IN SUCCESSFULLY' in the JSON response with the corresponding HTTP status. Note that 'JwtAccessToken' class has been given in the stub file to generate an access token.
		***/
		
        if(encryptedPassword.equals(userEntity.getPassword())){
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthTokenEntity userAuthToken = new UserAuthTokenEntity();
            userAuthToken.setUuid(userEntity.getUuid());
            userAuthToken.setUser(userEntity);

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuthToken.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
            userAuthToken.setExpiresAt(expiresAt);
            userAuthToken.setLoginAt(now);
            userAuthToken.setLogoutAt(null);


            userDao.createAuthToken(userAuthToken);

            return userAuthToken;
			
        }
		/***
		If the password provided by the user does not match the password in the existing database,
		throw 'AuthenticationFailedException' with the message code -'ATH-002' and message -'Password failed'.
		
		***/
		
        else{
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }

    }
}