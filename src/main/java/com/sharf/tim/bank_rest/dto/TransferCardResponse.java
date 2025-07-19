package com.sharf.tim.bank_rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class TransferCardResponse {

    private BigDecimal sourceCardBalance;
    private BigDecimal destinationCardBalance;

}
