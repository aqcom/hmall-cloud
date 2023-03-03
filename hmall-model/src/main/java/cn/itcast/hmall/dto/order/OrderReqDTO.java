package cn.itcast.hmall.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mrchen
 * @date 2022/5/30 14:22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderReqDTO {
    Integer num;
    Integer paymentType;
    Long addressId;
    Long itemId;
    String password;
}
