package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.UserAdminBusinessService;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/")
public class AdminController {

    @Autowired
    private UserAdminBusinessService userAdminBusinessService;

/**
     * Get the user details provided the userId.
     * @param userId user id of the user whose details has to be fetched.
     * @param accessToken Access token to authenticate the user who is requesting for user details.
     * @return
     * @throws AuthorizationFailedException - if the access token is invalid or already logged out or user is not an admin or user with enetered uuid does not exist
     * @throws UserNotFoundException - if the user with given id is not present in the records.
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/admin/users/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> getUser(@RequestHeader("authorization") final String authorization, @PathVariable("id") final String userUuid) throws AuthorizationFailedException, UserNotFoundException {
        String uid = userAdminBusinessService.deleteUser(userUuid, authorization);

        UserDeleteResponse userDeleteResponse =  new UserDeleteResponse().id(uid).status("USER SUCCESSFULLY DELETED");

        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse, HttpStatus.OK);


    }

}
