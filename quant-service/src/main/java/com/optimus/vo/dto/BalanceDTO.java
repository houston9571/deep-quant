package com.optimus.vo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId;

    private BigDecimal balance;

    private String currency;

    private Integer status = -1;

}
