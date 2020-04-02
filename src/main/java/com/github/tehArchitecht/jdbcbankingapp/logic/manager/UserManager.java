package com.github.tehArchitecht.jdbcbankingapp.logic.manager;

import com.github.tehArchitecht.jdbcbankingapp.data.exception.DataAccessException;
import com.github.tehArchitecht.jdbcbankingapp.data.model.User;
import com.github.tehArchitecht.jdbcbankingapp.logic.EntityMapper;
import com.github.tehArchitecht.jdbcbankingapp.logic.Result;
import com.github.tehArchitecht.jdbcbankingapp.logic.Status;
import com.github.tehArchitecht.jdbcbankingapp.logic.dto.request.SignInWithNameRequest;
import com.github.tehArchitecht.jdbcbankingapp.logic.dto.request.SignInWithPhoneNumberRequest;
import com.github.tehArchitecht.jdbcbankingapp.logic.dto.request.SignUpRequest;
import com.github.tehArchitecht.jdbcbankingapp.security.SecurityManager;
import com.github.tehArchitecht.jdbcbankingapp.security.SecurityToken;
import com.github.tehArchitecht.jdbcbankingapp.service.UserService;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class UserManager extends Manager {
    public UserManager(SecurityManager securityManager) {
        super(securityManager);
    }

    public Status signUp(SignUpRequest request) {
        String userName = request.getUserName();
        String phoneNumber = request.getPhoneNumber();

        try {
            if (UserService.isNameInUse(userName) || UserService.isPhoneNumberInUse(phoneNumber))
                return Status.SING_UP_FAILURE_NAME_OR_PHONE_NUMBER_TAKEN;

            User user = EntityMapper.extractUser(request);
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            UserService.add(user);
            return Status.SIGN_UP_SUCCESS;
        } catch (DataAccessException e) {
            return Status.FAILURE_INTERNAL_ERROR;
        }
    }

    public Result<SecurityToken> signInWithName(SignInWithNameRequest request) {
        String userName = request.getUserName();
        String password = request.getPassword();

        try {
            return signIn(
                    UserService.getByName(userName),
                    password,
                    Status.SIGN_IN_WITH_NAME_SUCCESS,
                    Status.SIGN_IN_WITH_NAME_FAILURE_WRONG_DATA
            );
        } catch (DataAccessException e) {
            return Result.ofFailure(Status.FAILURE_INTERNAL_ERROR);
        }
    }

    public Result<SecurityToken> signWithPhoneNumber(SignInWithPhoneNumberRequest request) {
        String phoneNumber = request.getPhoneNumber();
        String password = request.getPassword();

        try {
            return signIn(
                    UserService.getByPhoneNumber(phoneNumber),
                    password,
                    Status.SIGN_IN_WITH_PHONE_NUMBER_SUCCESS,
                    Status.SIGN_IN_WITH_PHONE_NUMBER_FAILURE_WRONG_DATA
            );
        } catch (DataAccessException e) {
            return Result.ofFailure(Status.FAILURE_INTERNAL_ERROR);
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Result<SecurityToken> signIn(Optional<User> optional, String password, Status success, Status failure) {
        if (!optional.isPresent())
            return Result.ofFailure(failure);

        User user = optional.get();
        if (BCrypt.checkpw(password, user.getPassword()))
            return Result.ofSuccess(success, securityManager.signIn(user.getId()));
        else
            return Result.ofFailure(failure);
    }

    public void signOut(SecurityToken token) {
        securityManager.signOut(token);
    }
}
