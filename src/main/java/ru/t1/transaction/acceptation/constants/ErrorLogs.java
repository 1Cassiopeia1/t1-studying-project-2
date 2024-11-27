package ru.t1.transaction.acceptation.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorLogs {
    public static final String TRANSACTION_REJECTED = "[STUD2-W001] Транзакция для клиента {} отклонена, так как баланс недостаточно велик";
    public static final String TRANSACTION_BLOCKED = "[STUD2-W002] Были заблокированы {} транзакций для клиента {}";
}
