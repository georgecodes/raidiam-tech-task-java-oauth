package com.raidiam.techtask.accountapi.services;

import com.raidiam.techtask.accountapi.model.Account;
import com.raidiam.techtask.accountapi.model.Customer;
import com.raidiam.techtask.accountapi.model.Transaction;
import com.raidiam.techtask.accountapi.model.http.AccountListResponse;
import com.raidiam.techtask.accountapi.model.http.TransactionListResponse;
import com.raidiam.techtask.accountapi.util.AccountDataFactory;
import com.raidiam.techtask.accountapi.util.Exceptions;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InMemoryAccountService implements AccountService {

    private Map<String, Account> accounts = new HashMap<>();

    public InMemoryAccountService() {
        AccountDataFactory.buildPretendModel().stream()
                .forEach(c -> {
                    c.getAccounts().stream()
                            .forEach(a -> {
                                accounts.put(a.getAccountNumber(), a);
                            });
                });
    }

    @Override
    public AccountListResponse accounts(Customer customer) {
        return AccountListResponse.builder()
                .customerId(customer.getId())
                .data(customer.getAccounts().stream()
                      .map(a -> AccountListResponse.AccountListItem.builder()
                              .accountId(a.getAccountNumber())
                              .balance(balanceFrom(a))
                              .build()).collect(Collectors.toList())
                )
                .build();
    }

    public TransactionListResponse transactions(String accountNumber) {
        Account account = Optional.ofNullable(accounts.get(accountNumber)).orElseThrow(Exceptions::notFound);
        return TransactionListResponse.builder()
                .accountNumber(account.getAccountNumber())
                .transactions(account.getTransactions().stream()
                        .map(t -> TransactionListResponse.TransactionListItem.builder()
                                .reference(t.getReference())
                                .date(t.getDateTime())
                                .amount(t.getAmount())
                                .build())
                .collect(Collectors.toList()))
                .build();
    }

    @Override
    public Account getById(String accountId) {
        return Optional.ofNullable(accounts.get(accountId)).orElseThrow(Exceptions::notFound);
    }

    private BigDecimal balanceFrom(Account a) {
        BigDecimal balance = new BigDecimal("0000.00");
        for(Transaction t: a.getTransactions()) {
            balance = balance.add(t.getAmount());
        }
        return balance;
    }

}
