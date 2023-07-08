package antifraud.controllers;

import antifraud.dto.IPDTOInput;
import antifraud.dto.StolenCardDTOInput;
import antifraud.dto.TransactionDTOInput;
import antifraud.dto.TransactionFeedBackDTOInput;
import antifraud.services.TransactionServiceImpl;
import antifraud.utilities.LuhnAlgorithmChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/api/antifraud")
public class TransactionController {

    private final TransactionServiceImpl transactionService;

    @Autowired
    public TransactionController(TransactionServiceImpl transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transaction")
    public ResponseEntity<Object> transactionInformation(@Valid @RequestBody(required = false)
                                                         TransactionDTOInput transactionDTOInput
    ) {
        return transactionService.displayResultOfTransaction(transactionDTOInput);
    }

    @GetMapping("/history")
    public ResponseEntity<Object> getTransactionsHistory() {
        return transactionService.getTransactionsHistory();
    }

    @GetMapping("/history/{number}")
    public ResponseEntity<Object> getTransactionsHistoryByCardNumber(@NotEmpty @PathVariable String number) {
        if (!LuhnAlgorithmChecker.isLuhnValid(number)) {
            return ResponseEntity.badRequest().body("Invalid card number");
        }
        return transactionService.getTransactionsHistoryByCardNumber(number);
    }

    @PutMapping("/transaction")
    public ResponseEntity<Object> addTransactionFeedback(
            @Valid @RequestBody TransactionFeedBackDTOInput transactionFeedBackDTOInput) {
        return transactionService.addTransactionFeedback(transactionFeedBackDTOInput);
    }

    @PostMapping("/suspicious-ip")
    public ResponseEntity<Object> saveSuspiciousIPAddress(@Valid @RequestBody IPDTOInput ipdtoInput) {
        return transactionService.saveSuspiciousIPAddress(ipdtoInput);
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    public ResponseEntity<Object> deleteSuspiciousIPAddress(@PathVariable String ip) {
        return transactionService.deleteSuspiciousIPAddress(ip);
    }

    @GetMapping("/suspicious-ip")
    public ResponseEntity<Object> getSuspiciousIPAddresses() {
        return transactionService.getSuspiciousIPAddresses();
    }

    @PostMapping("/stolencard")
    public ResponseEntity<Object> saveStolenCard(@Valid @RequestBody StolenCardDTOInput stolenCardDTOInput) {
        return transactionService.saveStolenCard(stolenCardDTOInput);
    }

    @DeleteMapping("/stolencard/{number}")
    public ResponseEntity<Object> deleteStolenCard(@PathVariable String number) {
        return transactionService.deleteStolenCard(number);
    }

    @GetMapping("/stolencard")
    public ResponseEntity<Object> getStolenCards() {
        return transactionService.getStolenCards();
    }

    @GetMapping("/transaction/max_value/{cardNumber}")
    public ResponseEntity<Object> getStolenCards(@PathVariable String cardNumber) {
        if (!LuhnAlgorithmChecker.isLuhnValid(cardNumber)) {
            return ResponseEntity.badRequest().body("Invalid card number");
        }
        return transactionService.getMaxValueTransactionByNumberCard(cardNumber);
    }
}
