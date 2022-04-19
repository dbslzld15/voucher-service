package org.prgrms.kdt.domain.voucher.repository;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.prgrms.kdt.domain.customer.model.Customer;
import org.prgrms.kdt.domain.customer.repository.JdbcCustomerRepository;
import org.prgrms.kdt.domain.voucher.model.Voucher;
import org.prgrms.kdt.domain.voucher.model.VoucherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringJUnitConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcVoucherRepositoryTest {

    @Configuration
    @ComponentScan(
            basePackages = {"org.prgrms.kdt"}
    )
    static class config {
        @Bean
        public DataSource dataSource() {
            return DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost/voucher")
                    .username("root")
                    .password("1234")
                    .type(HikariDataSource.class)
                    .build();
        }

    }

    @Autowired
    JdbcVoucherRepository voucherRepository;

    @Autowired
    JdbcCustomerRepository customerRepository;

    @BeforeEach
    void cleanData() {
        voucherRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("바우처가 정상적으로 저장된다.")
    void save() {
        //given
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UUID voucherId = UUID.randomUUID();
        Voucher voucher = new Voucher(voucherId, VoucherType.PERCENT_DISCOUNT, 10L, now, now);
        //when
        UUID savedId = voucherRepository.save(voucher);
        //then
        assertThat(voucherId).isEqualTo(savedId);
    }

    @Test
    @DisplayName("바우처 ID로 바우처를 조회한다.")
    void findById() {
        //given
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UUID voucherId = UUID.randomUUID();
        Voucher voucher = new Voucher(voucherId, VoucherType.PERCENT_DISCOUNT, 10L, now, now);
        //when
        voucherRepository.save(voucher);
        Optional<Voucher> findVoucher = voucherRepository.findById(voucherId);
        //then
        assertThat(findVoucher.get()).usingRecursiveComparison().isEqualTo(voucher);
    }

    @Test
    @DisplayName("저장된 바우처를 전부 조회한다.")
    void findAll() {
        //given
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UUID voucherId = UUID.randomUUID();
        Voucher voucher = new Voucher(voucherId, VoucherType.PERCENT_DISCOUNT, 10L, now, now);
        //when
        voucherRepository.save(voucher);
        List<Voucher> vouchers = voucherRepository.findAll();
        //then
        assertThat(vouchers.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("고객 ID를 통해 바우처를 조회할 수 있다.")
    public void findByCustomerId() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UUID customerId = UUID.randomUUID();
        customerRepository.save(new Customer(customerId, "park", "d@naver.com", now, now));
        UUID voucherId = UUID.randomUUID();
        Voucher voucher = new Voucher(voucherId, VoucherType.PERCENT_DISCOUNT, 10L, customerId, now, now);
        voucherRepository.save(voucher);
        //when
        List<Voucher> findVouchers = voucherRepository.findByCustomerId(customerId);
        //then
        assertThat(findVouchers.size()).isEqualTo(1);
        assertThat(findVouchers.get(0)).usingRecursiveComparison().isEqualTo(voucher);
    }

    @Test
    @DisplayName("바우처 타입과 날짜를 통해 바우처를 조회할 수 있다.")
    void findByVoucherTypeAndDate() {
        //given
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UUID voucherId = UUID.randomUUID();
        Voucher voucher = new Voucher(voucherId, VoucherType.PERCENT_DISCOUNT, 10L, now, now);
        voucherRepository.save(voucher);
        //when
        List<Voucher> findVouchers = voucherRepository.findByVoucherTypeAndDate(VoucherType.PERCENT_DISCOUNT, now.toLocalDate());
        //then
        assertThat(findVouchers.size()).isEqualTo(1);
        assertThat(findVouchers.get(0)).usingRecursiveComparison().isEqualTo(voucher);
    }

    @Test
    @DisplayName("바우처 ID를 통해 바우처를 업데이트 할 수 있다.")
    void updateById() {
        //given
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UUID voucherId = UUID.randomUUID();
        Voucher voucher = new Voucher(voucherId, VoucherType.PERCENT_DISCOUNT, 10L, now, now);
        voucherRepository.save(voucher);
        Voucher updateVoucher = new Voucher(voucherId, VoucherType.FIXED_AMOUNT, 20000L, now, now);
        //when
        int updateCnt = voucherRepository.updateById(updateVoucher);
        //then
        assertThat(updateCnt).isEqualTo(1);
    }

    @Test
    @DisplayName("잘못된 ID를 입력해 업데이트된 컬럼이 없을경우 예외가 발생한다.")
    void updateById_exception() {
        //given
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UUID voucherId = UUID.randomUUID();
        Voucher voucher = new Voucher(voucherId, VoucherType.PERCENT_DISCOUNT, 10L, now, now);
        voucherRepository.save(voucher);
        Voucher updateVoucher = new Voucher(UUID.randomUUID(), VoucherType.FIXED_AMOUNT, 20000L, now, now);
        //when
        //then
        assertThatThrownBy(() -> voucherRepository.updateById(updateVoucher))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("업데이트 된 컬럼이 없습니다.");
    }

    @Test
    void updateCustomerId() {
        //given
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UUID customerId = UUID.randomUUID();
        customerRepository.save(new Customer(customerId, "park", "d@naver.com", now, now));
        UUID voucherId = UUID.randomUUID();
        Voucher voucher = new Voucher(voucherId, VoucherType.PERCENT_DISCOUNT, 10L, now, now);
        voucherRepository.save(voucher);
        //when
        int updateCnt = voucherRepository.updateCustomerId(voucherId, customerId);
        //then
        assertThat(updateCnt).isEqualTo(1);
    }

    @Test
    void deleteById() {
        //given
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UUID voucherId = UUID.randomUUID();
        Voucher voucher = new Voucher(voucherId, VoucherType.PERCENT_DISCOUNT, 10L, now, now);
        voucherRepository.save(voucher);
        //when
        int deletedRows = voucherRepository.deleteById(voucherId);
        //then
        assertThat(deletedRows).isEqualTo(1);
    }

    @Test
    void deleteAll() {
        //given
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UUID voucherId = UUID.randomUUID();
        Voucher voucher = new Voucher(voucherId, VoucherType.PERCENT_DISCOUNT, 10L, now, now);
        voucherRepository.save(voucher);
        //when
        int deletedRows = voucherRepository.deleteAll();
        //then
        assertThat(deletedRows).isEqualTo(1);
    }

    @Test
    void deleteByCustomerId() {
        //given
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UUID customerId = UUID.randomUUID();
        customerRepository.save(new Customer(customerId, "park", "d@naver.com", now, now));
        UUID voucherId = UUID.randomUUID();
        Voucher voucher = new Voucher(voucherId, VoucherType.PERCENT_DISCOUNT, 10L, customerId, now, now);
        voucherRepository.save(voucher);
        //when
        int deletedRows = voucherRepository.deleteByCustomerId(customerId);
        //then
        assertThat(deletedRows).isEqualTo(1);
    }
}