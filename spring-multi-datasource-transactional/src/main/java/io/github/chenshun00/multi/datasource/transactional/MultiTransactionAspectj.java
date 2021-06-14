package io.github.chenshun00.multi.datasource.transactional;

import io.github.chenshun00.multi.datasource.transactional.datasource.MyDataSourceTransactionManager;
import io.github.chenshun00.multi.datasource.transactional.support.MyTransactionSynchronizationManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.TransactionStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author luobo.cs@raycloud.com
 * @since 2021/6/6 11:15 下午
 */
@Aspect
public class MultiTransactionAspectj {

    private final ApplicationContext applicationContext;

    public MultiTransactionAspectj(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Around("@annotation(multiTransactional)")
    public Object metric(ProceedingJoinPoint joinPoint, MultiTransactional multiTransactional) throws Throwable {
        String[] values = multiTransactional.values();
        final String[] beanNamesForType = applicationContext.getBeanNamesForType(MyDataSourceTransactionManager.class);
        if (values.length == 0) {
            values = beanNamesForType;
        }
        boolean error = false;
        final List<String> collect = Arrays.stream(values).collect(Collectors.toList());
        List<TrContext> trContexts = new ArrayList<>();
        try {

            for (String s : beanNamesForType) {
                if (collect.contains(s)) {
                    final MyDataSourceTransactionManager dataSourceTransactionManager = applicationContext.getBean(s, MyDataSourceTransactionManager.class);
                    final TransactionStatus transaction = dataSourceTransactionManager.getTransaction(null);
                    trContexts.add(new TrContext(dataSourceTransactionManager, transaction));
                }
            }
            return joinPoint.proceed();
        } catch (Throwable e) {
            error = true;
            trContexts.forEach(x -> x.dataSourceTransactionManager.rollback(x.transaction));
            throw e;
        } finally {
            if (!error) {
                for (TrContext trContext : trContexts) {
                    trContext.dataSourceTransactionManager.commit(trContext.transaction);
                }
            }
            MyTransactionSynchronizationManager.clear();
        }
    }

    public static class TrContext {
        MyDataSourceTransactionManager dataSourceTransactionManager;
        TransactionStatus transaction;

        public TrContext(MyDataSourceTransactionManager dataSourceTransactionManager, TransactionStatus transaction) {
            this.dataSourceTransactionManager = dataSourceTransactionManager;
            this.transaction = transaction;
        }
    }

}
