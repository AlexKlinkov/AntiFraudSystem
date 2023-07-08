package antifraud.dto;

import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class StolenCardDTOInput {
    @NotEmpty
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    @Size(min = 16, max = 16, message = "Field must not exceed or less then 16 digits")
    private String number;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
