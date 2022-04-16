package org.prgrms.kdt.domain.voucher.model;

import org.prgrms.kdt.domain.base.BaseEntity;
import org.prgrms.kdt.domain.voucher.types.VoucherType;

import java.util.UUID;

public class FixedAmountVoucher extends BaseEntity implements Voucher{
    private final UUID voucherId;
    private final long discountPrice;
    private UUID customerId;
    private static final VoucherType voucherType = VoucherType.FIXED_AMOUNT;

    public FixedAmountVoucher(UUID voucherId, long discountPrice) {
        validateDiscountPrice(discountPrice);
        this.voucherId = voucherId;
        this.discountPrice = discountPrice;
    }

    @Override
    public long discount(long beforeDiscount) {
        return beforeDiscount - discountPrice;
    }

    @Override
    public UUID getVoucherId() {
        return voucherId;
    }

    @Override
    public VoucherType getVoucherType() {
        return voucherType;
    }

    @Override
    public long getDiscountValue() {
        return discountPrice;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    private void validateDiscountPrice(long discountPrice) {
        if(discountPrice <= 0) {
            throw new IllegalArgumentException("할인금액은 0원 이하가 될 수 없습니다.");
        }
    }
}
