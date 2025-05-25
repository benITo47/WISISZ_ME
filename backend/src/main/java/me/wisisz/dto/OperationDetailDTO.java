package me.wisisz.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import me.wisisz.model.Operation;

public record OperationDetailDTO(
        String title,
        String description,
        BigDecimal totalAmount,
        OffsetDateTime operationDate,
        String categoryName,
        List<OperationMemberDTO> participants) {

    public OperationDetailDTO(Operation o) {
        this(
            o.getTitle(),
            o.getDescription(),
            o.getTotalAmount(),
            o.getOperationDate(),
            o.getCategory().getCategoryName(),
            calculateParticipantsWithShares(o)
        );
    }

    private static List<OperationMemberDTO> calculateParticipantsWithShares(Operation o) {
        List<BigDecimal> amounts = normalizeAmounts(o);

        BigDecimal min = amounts.stream()
                .filter(a -> a.compareTo(BigDecimal.ZERO) > 0)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ONE);

        List<BigDecimal> rawShares = amounts.stream()
                .map(amount -> amount.divide(min, 10, RoundingMode.HALF_UP).stripTrailingZeros())
                .collect(Collectors.toList());

        List<BigDecimal> denominators = rawShares.stream()
                .map(share -> {
                    int scale = share.scale();
                    if (scale < 0) scale = 0;
                    return BigDecimal.TEN.pow(scale);
                })
                .collect(Collectors.toList());

        BigDecimal lcm = denominators.stream()
                .reduce(OperationDetailDTO::lcm)
                .orElse(BigDecimal.ONE);

        List<BigDecimal> normalizedShares = rawShares.stream()
                .map(share -> share.multiply(lcm).setScale(0, RoundingMode.HALF_UP))
                .collect(Collectors.toList());

        List<Long> sharesLong = normalizedShares.stream()
                .map(BigDecimal::longValueExact)
                .collect(Collectors.toList());

        long gcdAll = sharesLong.stream()
                .reduce(OperationDetailDTO::gcd)
                .orElse(1L);

        List<BigDecimal> simplifiedShares = sharesLong.stream()
                .map(s -> BigDecimal.valueOf(s / gcdAll))
                .collect(Collectors.toList());

        AtomicInteger index = new AtomicInteger();

        return o.getEntries().stream()
                .map(e -> new OperationMemberDTO(
                        e.getTeamMember(),
                        e.getAmount(),
                        simplifiedShares.get(index.getAndIncrement()),
                        o.getCurrencyCode()
                ))
                .distinct()
                .collect(Collectors.toList());
    }

    private static long gcd(long a, long b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    private static BigDecimal gcd(BigDecimal a, BigDecimal b) {
        while (b.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal temp = b;
            b = a.remainder(b);
            a = temp;
        }
        return a;
    }

    private static BigDecimal lcm(BigDecimal a, BigDecimal b) {
        return a.multiply(b).divide(gcd(a, b), 0, RoundingMode.HALF_UP);
    }

    private static List<BigDecimal> normalizeAmounts(Operation o) {
        BigDecimal totalAmount = o.getTotalAmount();
        String operationType = o.getOperationType();

        return o.getEntries().stream()
            .map(e -> {
                BigDecimal paidAmount = e.getAmount();

                if (paidAmount == null || paidAmount.compareTo(BigDecimal.ZERO) == 0) {
                    return BigDecimal.ZERO;
                }

                switch (operationType.toLowerCase()) {
                    case "expense":
                        if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
                            return paidAmount.subtract(totalAmount);
                        }
                        return paidAmount;

                    case "income":
                        return paidAmount;

                    case "transfer":
                        if (paidAmount.compareTo(BigDecimal.ZERO) < 0) {
                            return totalAmount.add(paidAmount);
                        }
                        return paidAmount;

                    default:
                        return paidAmount;
                }
            })
            .collect(Collectors.toList());
    }

}
