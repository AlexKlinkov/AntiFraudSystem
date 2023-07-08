package antifraud.services;

import antifraud.dto.*;
import antifraud.exceptions.BadRequestException;
import antifraud.exceptions.ConflictException;
import antifraud.exceptions.NotFoundException;
import antifraud.models.*;
import antifraud.repositories.*;
import antifraud.utilities.ConverterInLocalDataTime;
import antifraud.utilities.IPAddressValidator;
import antifraud.utilities.LuhnAlgorithmChecker;
import antifraud.utilities.Regions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
public class TransactionServiceImpl {
    @Autowired
    private SuspiciousIPAddressRepository ipAddressRepository;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private StolenCardRepository stolenCardRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransactionFeedbackRepository transactionFeedbackRepository;
    @Autowired
    private MaxValueTransactionServiceImpl maxValueTransactionService;
    @Autowired
    private MaxValueTransactionRepository maxValueTransactionRepository;

    public ResponseEntity<Object> displayResultOfTransaction(TransactionDTOInput transaction) {
        userService.ifUserWhoSentQueryHasRoleUnlikeInParamReturnQueryStatusForbidden("ROLE_MERCHANT");
        long transactionAmount = transaction.getAmount();
        String transactionIP = transaction.getIp();
        String transactionNumber = transaction.getNumber();
        String transactionRegion = transaction.getRegion();
        LocalDateTime transactionData = ConverterInLocalDataTime.localDataTimeFromString(transaction.getDate());
        if (!new Regions().getRegions().containsKey(transactionRegion)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(transaction);
        }
        boolean sumBiggerThan1500 = transactionAmount >
                maxValueTransactionService.getTransactionAdmissibleValueByCardNumber(
                        transaction.getNumber()).getMaxMANUAL();
        boolean sumBiggerThan200 = transactionAmount >
                maxValueTransactionService.getTransactionAdmissibleValueByCardNumber(
                        transaction.getNumber()).getMaxALLOWED();
        boolean isCardStolen = stolenCardRepository.existsByNumber(transactionNumber);
        boolean isIpAddressSuspicious = ipAddressRepository.existsByIp(transactionIP);
        assert transactionData != null;
        int countIPCorrelation = getCountIpCorrelation(getTransactionsPerLastHourByCardNumber(transactionNumber,
                transactionData), transaction);
        int countRegionCorrelation = getCountRegionCorrelation(getTransactionsPerLastHourByCardNumber(transactionNumber,
                transactionData), transaction);
        List<Boolean> allReasonsProhibited;
        Transaction transaction1ForSaveInBD;
        TransactionDTOOutput outputDTO = new TransactionDTOOutput();
        if (isIpAddressSuspicious || isCardStolen || countIPCorrelation > 2 || countRegionCorrelation > 2) {
            transaction1ForSaveInBD = createTransaction(transaction, "PROHIBITED");
            transactionRepository.save(transaction1ForSaveInBD);
            outputDTO.setResult("PROHIBITED");
            allReasonsProhibited = List.of(sumBiggerThan1500, isCardStolen, isIpAddressSuspicious,
                    countIPCorrelation > 2, countRegionCorrelation > 2);
            outputDTO.setInfo(getSortedReasonsProhibitedTransaction(allReasonsProhibited));
            return ResponseEntity.status(HttpStatus.OK).body(outputDTO);
        }
        if (sumBiggerThan1500) {
            transaction1ForSaveInBD = createTransaction(transaction, "PROHIBITED");
            transactionRepository.save(transaction1ForSaveInBD);
            outputDTO.setResult("PROHIBITED");
            allReasonsProhibited = List.of(true, false, false, false, false);
            outputDTO.setInfo(getSortedReasonsProhibitedTransaction(allReasonsProhibited));
            return ResponseEntity.status(HttpStatus.OK).body(outputDTO);
        }
        if (countIPCorrelation == 2 || countRegionCorrelation == 2) {
            transaction1ForSaveInBD = createTransaction(transaction, "MANUAL_PROCESSING");
            transactionRepository.save(transaction1ForSaveInBD);
            outputDTO.setResult("MANUAL_PROCESSING");
            allReasonsProhibited = List.of(sumBiggerThan200, false, false,
                    countIPCorrelation == 2, countRegionCorrelation == 2);
            outputDTO.setInfo(getSortedReasonsProhibitedTransaction(allReasonsProhibited));
            return ResponseEntity.status(HttpStatus.OK).body(outputDTO);
        }
        if (sumBiggerThan200) {
            transaction1ForSaveInBD = createTransaction(transaction, "MANUAL_PROCESSING");
            transactionRepository.save(transaction1ForSaveInBD);
            outputDTO.setResult("MANUAL_PROCESSING");
            allReasonsProhibited = List.of(true, false, false,
                    false, false);
            outputDTO.setInfo(getSortedReasonsProhibitedTransaction(allReasonsProhibited));
            return ResponseEntity.status(HttpStatus.OK).body(outputDTO);
        }
        transaction1ForSaveInBD = createTransaction(transaction, "ALLOWED");
        transactionRepository.save(transaction1ForSaveInBD);
        outputDTO.setResult("ALLOWED");
        outputDTO.setInfo("none");
        return ResponseEntity.status(HttpStatus.OK).body(outputDTO);
    }

    public ResponseEntity<Object> saveSuspiciousIPAddress(IPDTOInput ipdtoInput) {
        if (ipAddressRepository.existsByIp(ipdtoInput.getIp())) {
            throw new ConflictException();
        }
        userService.ifUserWhoSentQueryHasRoleUnlikeInParamReturnQueryStatusForbidden("ROLE_SUPPORT");
        SuspiciousIPAddress ipAddress = new SuspiciousIPAddress();
        ipAddress.setIp(ipdtoInput.getIp());
        return ResponseEntity.status(HttpStatus.OK).body(ipAddressRepository.save(ipAddress));
    }

    @Transactional
    public ResponseEntity<Object> deleteSuspiciousIPAddress(String ip) {
        userService.ifUserWhoSentQueryHasRoleUnlikeInParamReturnQueryStatusForbidden("ROLE_SUPPORT");
        if (!IPAddressValidator.isValidIPAddress(ip)) {
            throw new BadRequestException();
        }
        if (!ipAddressRepository.existsByIp(ip)) {
            throw new NotFoundException();
        }
        ipAddressRepository.deleteIPAddressByIp(ip);
        IPDTOOutputForDelete ipdtoOutputForDelete = new IPDTOOutputForDelete();
        ipdtoOutputForDelete.setStatus("IP " + ip + " successfully removed!");
        return ResponseEntity.status(HttpStatus.OK).body(ipdtoOutputForDelete);
    }

    public ResponseEntity<Object> getSuspiciousIPAddresses() {
        List<IPDTOOutput> result = new ArrayList<>();
        List<SuspiciousIPAddress> ipAddressListFromBD = ipAddressRepository.findAll();
        userService.ifUserWhoSentQueryHasRoleUnlikeInParamReturnQueryStatusForbidden("ROLE_SUPPORT");
        if (ipAddressListFromBD.size() > 0) {
            for (SuspiciousIPAddress ipAddress : ipAddressListFromBD) {
                IPDTOOutput ipdtoOutput = new IPDTOOutput();
                ipdtoOutput.setId(ipAddress.getId());
                ipdtoOutput.setIp(ipAddress.getIp());
                result.add(ipdtoOutput);
            }
            result.sort(Comparator.comparing(IPDTOOutput::getId));
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public ResponseEntity<Object> saveStolenCard(StolenCardDTOInput stolenCardDTOInput) {
        if (!LuhnAlgorithmChecker.isLuhnValid(stolenCardDTOInput.getNumber())) {
            throw new BadRequestException();
        }
        if (stolenCardRepository.existsByNumber(stolenCardDTOInput.getNumber())) {
            throw new ConflictException();
        }
        userService.ifUserWhoSentQueryHasRoleUnlikeInParamReturnQueryStatusForbidden("ROLE_SUPPORT");
        StolenCard stolenCard = new StolenCard();
        stolenCard.setNumber(stolenCardDTOInput.getNumber());
        return ResponseEntity.status(HttpStatus.OK).body(stolenCardRepository.save(stolenCard));
    }

    @Transactional
    public ResponseEntity<Object> deleteStolenCard(String number) {
        if (!LuhnAlgorithmChecker.isLuhnValid(number)) {
            throw new BadRequestException();
        }
        userService.ifUserWhoSentQueryHasRoleUnlikeInParamReturnQueryStatusForbidden("ROLE_SUPPORT");
        if (!stolenCardRepository.existsByNumber(number)) {
            throw new NotFoundException();
        }
        stolenCardRepository.deleteStolenCardByNumber(number);
        StolenCardDTOOutputForDelete stolenCardDTOOutputForDelete = new StolenCardDTOOutputForDelete();
        stolenCardDTOOutputForDelete.setStatus("Card " + number + " successfully removed!");
        return ResponseEntity.status(HttpStatus.OK).body(stolenCardDTOOutputForDelete);
    }

    public ResponseEntity<Object> getStolenCards() {
        List<StolenCardDTOOutput> result = new ArrayList<>();
        List<StolenCard> stolenCardsFromBD = stolenCardRepository.findAll();
        userService.ifUserWhoSentQueryHasRoleUnlikeInParamReturnQueryStatusForbidden("ROLE_SUPPORT");
        if (stolenCardsFromBD.size() > 0) {
            for (StolenCard stolenCard : stolenCardsFromBD) {
                StolenCardDTOOutput stolenCardDTOOutput = new StolenCardDTOOutput();
                stolenCardDTOOutput.setId(stolenCard.getId());
                stolenCardDTOOutput.setNumber(stolenCard.getNumber());
                result.add(stolenCardDTOOutput);
            }
            result.sort(Comparator.comparing(StolenCardDTOOutput::getId));
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    private Transaction createTransaction(TransactionDTOInput transactionDTOInput, String resultOfTransaction) {
        if (transactionDTOInput == null) {
            return null;
        }
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDTOInput.getAmount());
        transaction.setIpID(transactionDTOInput.getIp());
        transaction.setNumber(transactionDTOInput.getNumber());
        transaction.setRegion(transactionDTOInput.getRegion());
        transaction.setCreatedDate(ConverterInLocalDataTime.localDataTimeFromString(transactionDTOInput.getDate()));
        transaction.setResult(resultOfTransaction);
        return transaction;
    }

    private String getSortedReasonsProhibitedTransaction(List<Boolean> reasons) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean flag = true;
        if (reasons.get(0)) {
            stringBuilder.append("amount, ");
            flag = false;
        }
        if (reasons.get(1)) {
            stringBuilder.append("card-number, ");
            flag = false;
        }
        if (reasons.get(2)) {
            stringBuilder.append("ip, ");
            flag = false;
        }
        if (reasons.get(3)) {
            stringBuilder.append("ip-correlation, ");
            flag = false;
        }
        if (reasons.get(4)) {
            stringBuilder.append("region-correlation, ");
            flag = false;
        }
        if (flag) {
            stringBuilder.append("none");
        } else {
            return stringBuilder.substring(0, stringBuilder.length() - 2);
        }
        return stringBuilder.toString();
    }

    private List<Transaction> getTransactionsPerLastHourByCardNumber(String cardNumber, LocalDateTime time) {
        LocalDateTime oneHourAgo = time.minusHours(1);
        return transactionRepository.findAllByNumberAndCreatedDateIsBetween(cardNumber, oneHourAgo, time);
    }

    private int getCountIpCorrelation(List<Transaction> transactionsPerLastHour,
                                            TransactionDTOInput currentTransaction) {
        Set<String> uniqueIPs = new HashSet<>();

        for (Transaction transaction : transactionsPerLastHour) {
            if (!currentTransaction.getIp().equals(transaction.getIp())) {
                uniqueIPs.add(transaction.getIp());
            }
        }
        return uniqueIPs.size();
    }

    private int getCountRegionCorrelation(List<Transaction> transactionsPerLastHour,
                                                TransactionDTOInput currentTransaction) {
        Set<String> uniqueRegions = new HashSet<>();

        for (Transaction transaction : transactionsPerLastHour) {
            if (!currentTransaction.getRegion().equals(transaction.getRegion())) {
                uniqueRegions.add(transaction.getRegion());
            }
        }
        return uniqueRegions.size();
    }

    public ResponseEntity<Object> getTransactionsHistory() {
        userService.ifUserWhoSentQueryHasRoleUnlikeInParamReturnQueryStatusForbidden("ROLE_SUPPORT");
        List<TransactionFeedBackDTOOutput> result = new ArrayList<>();
        List<Transaction> transactionList = transactionRepository.findAll();
        return getObjectResponseEntity(result, transactionList);
    }

    private ResponseEntity<Object> getObjectResponseEntity(List<TransactionFeedBackDTOOutput> result,
                                                           List<Transaction> transactionList) {
        for (Transaction transaction : transactionList) {
            if (transactionFeedbackRepository.existsByTransactionId(transaction.getId())) {
                TransactionFeedBack transactionFeedBack =
                        transactionFeedbackRepository.getByTransactionId(transaction.getId());
                TransactionFeedBackDTOOutput transactionFeedBackDTOOutput = createTransactionFeedBackOutput(
                        transaction, transactionFeedBack.getFeedback(), transaction.getResult());
                result.add(transactionFeedBackDTOOutput);
            } else {
                TransactionFeedBackDTOOutput transactionFeedBackDTOOutput = createTransactionFeedBackOutput(
                        transaction, "", transaction.getResult());
                result.add(transactionFeedBackDTOOutput);
            }
        }
        result.sort(Comparator.comparing(TransactionFeedBackDTOOutput::getTransactionId));
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public ResponseEntity<Object> getTransactionsHistoryByCardNumber(String number) {
        userService.ifUserWhoSentQueryHasRoleUnlikeInParamReturnQueryStatusForbidden("ROLE_SUPPORT");
        List<Transaction> transactionsFromBD = transactionRepository.findAllByNumber(number);
        if (transactionsFromBD.isEmpty()) {
            throw new NotFoundException();
        }
        List<TransactionFeedBackDTOOutput> result = new ArrayList<>();
        return getObjectResponseEntity(result, transactionsFromBD);
    }

    public ResponseEntity<Object> addTransactionFeedback (TransactionFeedBackDTOInput transactionFeedBackDTOInput) {
        userService.ifUserWhoSentQueryHasRoleUnlikeInParamReturnQueryStatusForbidden("ROLE_SUPPORT");
        if (transactionFeedbackRepository.existsByTransactionId(transactionFeedBackDTOInput.getTransactionId())) {
            throw new ConflictException();
        }
        if (!transactionRepository.existsById(transactionFeedBackDTOInput.getTransactionId())) {
            throw new NotFoundException();
        }
        Transaction transactionFromBD = transactionRepository.getById(transactionFeedBackDTOInput.getTransactionId());
        TransactionFeedBackDTOOutput result = createTransactionFeedBackOutput(transactionFromBD,
                transactionFeedBackDTOInput.getFeedback(),
                transactionFromBD.getResult());
        TransactionFeedBack transactionFeedBack = new TransactionFeedBack();
        transactionFeedBack.setTransactionId(result.getTransactionId());
        transactionFeedBack.setFeedback(result.getFeedback());
        maxValueTransactionService.saveNewValueInBD(transactionFromBD.getNumber(), transactionFromBD.getAmount(),
                result.getFeedback());
        transactionFeedbackRepository.save(transactionFeedBack);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public ResponseEntity<Object> getMaxValueTransactionByNumberCard(String cardNumber) {
        MaxValueTransaction maxValueTransaction = maxValueTransactionRepository.getByNumber(cardNumber);
        if (maxValueTransaction != null) {
            MaxValueTransactionAtCardNumberDTOOutput result = new MaxValueTransactionAtCardNumberDTOOutput();
            result.setCardNumber(cardNumber);
            result.setMaxALLOWED(maxValueTransactionRepository.getByNumber(cardNumber).getMaxALLOWED());
            result.setMaxMANUAL(maxValueTransactionRepository.getByNumber(cardNumber).getMaxMANUAL());
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
        return null;
    }
    private TransactionFeedBackDTOOutput createTransactionFeedBackOutput(Transaction transactionFromBD,
                                                                         String transactionFeedBack,
                                                                         String resultOfTransaction) {
        TransactionFeedBackDTOOutput result = new TransactionFeedBackDTOOutput();
        result.setTransactionId(transactionFromBD.getId());
        result.setAmount(transactionFromBD.getAmount());
        result.setIp(transactionFromBD.getIp());
        result.setNumber(transactionFromBD.getNumber());
        result.setRegion(transactionFromBD.getRegion());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        result.setDate(transactionFromBD.getCreatedDate().format(formatter));
        result.setResult(resultOfTransaction);
        result.setFeedback(transactionFeedBack);
        return result;
    }
}
