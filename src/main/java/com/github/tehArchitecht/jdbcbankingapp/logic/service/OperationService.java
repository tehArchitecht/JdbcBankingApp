package com.github.tehArchitecht.jdbcbankingapp.logic.service;

import com.github.tehArchitecht.jdbcbankingapp.data.exception.DataAccessException;
import com.github.tehArchitecht.jdbcbankingapp.data.model.Operation;
import com.github.tehArchitecht.jdbcbankingapp.data.repository.OperationRepository;

import java.util.List;
import java.util.UUID;

class OperationService {
    static void add(Operation operation) throws DataAccessException {
        OperationRepository.save(operation);
    }

    static List<Operation> findAllByAccountId(UUID accountId) throws DataAccessException {
        return OperationRepository.findDistinctBySenderAccountIdOrReceiverAccountId(accountId);
    }
}
