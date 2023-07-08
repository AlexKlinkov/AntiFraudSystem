package antifraud.services;

import antifraud.exceptions.UnprocessableException;
import antifraud.models.MaxValueTransaction;
import antifraud.repositories.MaxValueTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MaxValueTransactionServiceImpl {

    @Autowired
    private MaxValueTransactionRepository maxValueTransactionRepository;

    public void saveNewValueInBD(String number, long amount, String feedBack) {
        long startAllowed = getTransactionAdmissibleValueByCardNumber(number).getMaxALLOWED();
        long finishManual = getTransactionAdmissibleValueByCardNumber(number).getMaxMANUAL();;
        long newAllowedLimit = startAllowed;
        long newManualLimit = finishManual;
        if (amount <= startAllowed) { // transaction validation
            if (feedBack.equals("ALLOWED")) {
                throw new UnprocessableException();
            } else if (feedBack.equals("MANUAL_PROCESSING")) {
                newAllowedLimit = (long) Math.ceil(0.8 * startAllowed - 0.2 * amount);
            } else if (feedBack.equals("PROHIBITED")) {
                newAllowedLimit = (long) Math.ceil(0.8 * startAllowed - 0.2 * amount);
                newManualLimit = (long) Math.ceil(0.8 * finishManual - 0.2 * amount);
            }
        } else if (amount <= finishManual) { // transaction validation
            if (feedBack.equals("MANUAL_PROCESSING")) {
                throw new UnprocessableException();
            } else if (feedBack.equals("ALLOWED")) {
                newAllowedLimit = (long) Math.ceil(0.8 * startAllowed + 0.2 * amount);
            } else if (feedBack.equals("PROHIBITED")) {
                newManualLimit = (long) Math.ceil(0.8 * finishManual - 0.2 * amount);
            }
        } else { // transaction validation
            if (feedBack.equals("PROHIBITED")) {
                throw new UnprocessableException();
            } else if (feedBack.equals("ALLOWED")) {
                newAllowedLimit = (long) Math.ceil(0.8 * startAllowed + 0.2 * amount);
                newManualLimit = (long) Math.ceil(0.8 * finishManual + 0.2 * amount);
            } else if (feedBack.equals("MANUAL_PROCESSING")) {
                newManualLimit = (long) Math.ceil(0.8 * finishManual + 0.2 * amount);
            }
        }
        maxValueTransactionRepository.update(number, newAllowedLimit, newManualLimit);
    }

    public MaxValueTransaction getTransactionAdmissibleValueByCardNumber(String number) {
        if (maxValueTransactionRepository.existsByNumber(number)) {
            return maxValueTransactionRepository.getByNumber(number);
        } else {
            MaxValueTransaction maxValueTransactionDefault = new MaxValueTransaction();
            maxValueTransactionDefault.setNumber(number);
            maxValueTransactionDefault.setMaxALLOWED(200L);
            maxValueTransactionDefault.setMaxMANUAL(1500L);
            return maxValueTransactionRepository.save(maxValueTransactionDefault);
        }
    }
}
