package com.javabootcamp.fintechbank.accounts;

import com.javabootcamp.fintechbank.exceptions.BadRequestException;
import com.javabootcamp.fintechbank.exceptions.InternalServerException;
import com.javabootcamp.fintechbank.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<AccountResponse> getAccounts() {
        return accountRepository
                .findAll()
                .stream()
                .map(acc -> new AccountResponse(acc.getNo(), acc.getType(), acc.getName(), acc.getBalance()))
                .toList();
    }

    public AccountResponse depositAccount(Integer accountNo, DepositRequest depositRequest) {
        Optional<Account> optionalAccount = accountRepository.findById(accountNo);
        if (optionalAccount.isEmpty()) {
            throw new NotFoundException("Account not found");
        }

        Account account = optionalAccount.get();
        Double newBalance = account.getBalance() + depositRequest.amount();
        account.setBalance(newBalance);

        try {
            accountRepository.save(account);
        } catch (Exception e) {
            throw new InternalServerException("Failed to deposit");
        }
        return new AccountResponse(account.getNo(), account.getType(), account.getName(), account.getBalance());
    }

    @Transactional
    public AccountRequest createAccount(AccountRequest restore) {

        try {
            Account account = new Account();
            account.setName(restore.getName());
            account.setType(restore.getType());
            account.setBalance(restore.getBalance());
            accountRepository.save(account);
        } catch (Exception e) {
            throw new InternalServerException("Failed to deposit");
        }
        return restore;
    }

    public AccountRequest createAccountNoWithDraw(int accountNo, AccountRequest restore) {
        Optional<Account> account = accountRepository.findById(accountNo);
        if(account.isEmpty()) {
            throw new NotFoundException("Account not found");
        }
        Account yourAccount = account.get();
        Double checkWithDraw = yourAccount.getBalance() - restore.getBalance();
        if(checkWithDraw < 0) {
            throw new BadRequestException("Your money is not enough to withdraw");
        }
        yourAccount.setBalance(checkWithDraw);
        restore.setBalance(checkWithDraw);
        accountRepository.save(yourAccount);

        return restore;
    }

    public AccountResponse createTransferToAccount(AccountRequest myAccount,int accountNo, int targetNo) {
        // check account'withdraw
        Optional<Account> accountWithDraw = accountRepository.findById(accountNo);
        if(accountWithDraw.isEmpty()) {
            throw new NotFoundException("Account not found");
        }
        // check account target
        Optional<Account> accountTargetNo = accountRepository.findById(targetNo);
        if(accountTargetNo.isEmpty()) {
            throw new NotFoundException("Account not found");
        }
        Account myAccountWithDraw = accountWithDraw.get();
        Double checkWithDraw = myAccountWithDraw.getBalance() - myAccount.getBalance(); // check money can withdraw
        if(checkWithDraw < 0) {
            throw new BadRequestException("Your money is not enough to withdraw");
        }

        Account targetAccountWithDraw = accountTargetNo.get();
        Double deposit = targetAccountWithDraw.getBalance() + myAccount.getBalance();
        targetAccountWithDraw.setBalance(deposit); // deposit
        myAccountWithDraw.setBalance(checkWithDraw); // withdraw

        accountRepository.save(myAccountWithDraw); // withdraw the account
        accountRepository.save(targetAccountWithDraw); // deposit the account

        return new AccountResponse(myAccountWithDraw.getNo(), myAccountWithDraw.getType(), myAccountWithDraw.getName(), myAccountWithDraw.getBalance());
    }

    public AccountResponse getAccount(int accountNo) {
        Optional<Account> account = accountRepository.findById(accountNo);
        if(account.isEmpty()) {
            throw new NotFoundException("Account not found");
        }
        Account myAccount = account.get();
        return new AccountResponse(myAccount.getNo(), myAccount.getType(), myAccount.getName(), myAccount.getBalance());
    }
}
