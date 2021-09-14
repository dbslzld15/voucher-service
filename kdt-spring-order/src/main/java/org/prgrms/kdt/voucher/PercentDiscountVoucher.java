package org.prgrms.kdt.voucher;

import java.util.UUID;

public class PercentDiscountVoucher implements Voucher{
    private final UUID voucherId;
    private final long amount;
    private final VoucherType type = VoucherType.PERCENT;

    public PercentDiscountVoucher(UUID voucherId, long percent) {
        if(percent <= 0) throw new IllegalArgumentException("percent is not zero or minus");
        if(percent > 100) throw new IllegalArgumentException("percent is not over 100");
        this.voucherId = voucherId;
        this.amount = percent;
    }

    @Override
    public UUID getVoucherId() {
        return voucherId;
    }

    @Override
    public long getVoucherAmount() {
        return amount;
    }

    @Override
    public VoucherType getVoucherType() {
        return type;
    }

    @Override
    public long discount(long beforeDiscount) {
        return beforeDiscount - (beforeDiscount * amount / 100);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Voucher Id : " + voucherId + " ");
        sb.append("Amount : " + amount + " ");
        sb.append("Type : " + type);
        return sb.toString();
    }
}
