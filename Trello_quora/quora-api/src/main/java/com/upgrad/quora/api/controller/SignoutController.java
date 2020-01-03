package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.service.business.SignoutService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/")
public  class SignoutController {

    @Autowired
    SignoutService signoutService;


    @RequestMapping(method = RequestMethod.POST, path = "/users/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> Signout(@RequestHeader("authorization") final String authorization) throws SignOutRestrictedException {


        UserEntity userEntity = signoutService.Signout(authorization);

        SignoutResponse signoutResponse = new SignoutResponse().id(userEntity.getUuid()).message("SIGNED OUT SUCCESSFULLY");

        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);






    }


}