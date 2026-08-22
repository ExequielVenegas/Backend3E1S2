package cl.duoc.bancoxyz.legacy_batch_migrator.policies;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.SimpleRetryPolicy;

import java.util.Collections;

@Slf4j
public class BancoRetryPolicy extends SimpleRetryPolicy {

    public BancoRetryPolicy() {
        super(3, Collections.singletonMap(RuntimeException.class, true));
    }

    @Override
    public void registerThrowable(RetryContext context, Throwable throwable) {
        super.registerThrowable(context, throwable);

        log.warn("RetryPolicy activada: Reintento {}/{} debido a error transitorio: {}",
                context.getRetryCount(),
                getMaxAttempts(),
                throwable.getMessage());
    }
}