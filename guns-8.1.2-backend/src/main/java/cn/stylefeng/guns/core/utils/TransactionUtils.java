package cn.stylefeng.guns.core.utils;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Component
public class TransactionUtils {

    @Transactional
    public <T> T executeInTransaction(Supplier<T> businessLogic) {
        return businessLogic.get();
    }

}
