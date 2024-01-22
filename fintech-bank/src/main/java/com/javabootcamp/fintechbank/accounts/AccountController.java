package com.javabootcamp.fintechbank.accounts;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/accounts")
@RestController
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "list all accounts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "list all accounts",
                    content = {
                            @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = AccountResponse.class)))
                    })
    })
    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<AccountResponse> getAccounts() {
        return accountService.getAccounts();
    }

    @Operation(summary = "withdraw from an account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "withdraw money from specific account",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AccountResponse.class))
                    })
    })
    @RequestMapping(value = "/{accountNo}/deposit", method = RequestMethod.POST)
    public AccountResponse depositAccount(
            @PathVariable(name = "accountNo") Integer accountNo,
            @RequestBody @Valid DepositRequest depositRequest
    ) {
        return accountService.depositAccount(accountNo, depositRequest);
    }
    // Challenge 1
    @Operation(summary = "creates a new account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "creates a new account",
             content = @Content(mediaType = "application/json",
             schema = @Schema(implementation = AccountRequest.class)))
    })
    @RequestMapping(value = "/accounts", method = RequestMethod.POST)
    public AccountRequest createNewAccount(@RequestBody AccountRequest restore){
        return accountService.createAccount(restore);
    }
    // Challenge 2
    @Operation(summary = "creates a new transaction")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "creates a new transaction",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountRequest.class)))
    })
    @RequestMapping(value = "/accounts/{accountNo}/withdraw", method = RequestMethod.POST)
    public AccountRequest createAccountNoWithDraw(@PathVariable("accountNo") int accountNo , @RequestBody AccountRequest restore){
        return accountService.createAccountNoWithDraw(accountNo, restore);
    }

    // Challenge 3
    @Operation(summary = "create a new withdraw transaction")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "create a new withdraw transaction",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class)))
    })
    @RequestMapping(value = "/accounts/{accountNo}/transfer/{targetAccountNo}", method = RequestMethod.POST)
    public AccountResponse createTransferToAccount(@RequestBody AccountRequest restore, @PathVariable("accountNo") int accountNo, @PathVariable("targetAccountNo") int targetAccountNo){
        return accountService.createTransferToAccount(restore, accountNo, targetAccountNo);
    }

    // Challenge 3
    @Operation(summary = "get my account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "get my account",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class)))
    })
    @RequestMapping(value = "/accounts/{accountNo}", method = RequestMethod.GET)
    public AccountResponse getAccount(@PathVariable("accountNo") int accountNo){
        return accountService.getAccount(accountNo);
    }
}

