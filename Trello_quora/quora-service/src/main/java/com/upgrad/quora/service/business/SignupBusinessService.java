package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupBusinessService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;



    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {

        String password = userEntity.getPassword();
        if(password == null){
            userEntity.setPassword("quora@123");
        }
        String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        UserEntity userByEmail = userDao.getUserByEmail(userEntity.getEmail());
        UserEntity userByUsername = userDao.getUserByUsername(userEntity.getUserName());
        if (userByEmail != null){

            throw new SignUpRestrictedException("SGR-002" , "This user has already been registered, try with any other emailId");

        }

        else if (userByUsername !=null) {
            throw new SignUpRestrictedException("SGR-001" ,"Try any other Username, this Username has already been taken");
        }

        else {
            return userDao.createUser(userEntity);

        }
    }
}
