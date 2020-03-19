package com.github.tehArchitecht.logic.service;

import com.github.tehArchitecht.data.DbAccessException;
import com.github.tehArchitecht.data.model.Operation;
import com.github.tehArchitecht.data.repository.OperationRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

class OperationService {
    static boolean add(Operation operation) {
        try {
            return OperationRepository.save(operation);
        } catch (DbAccessException e) {
            return false;
        }
    }

    static Optional<List<Operation>> findAllByAccountId(UUID accountId) {
        try {
            List<Operation> operations = OperationRepository.findAllBySenderAccountIdOrReceiverAccountId(accountId);
            return (operations == null) ? Optional.empty() : Optional.of(operations);
        } catch (DbAccessException e) {
            return Optional.empty();
        }
    }
}
