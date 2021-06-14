package io.github.chenshun00.multi.datasource.transactional.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.ResourceHolder;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.util.Assert;

import java.util.*;

/**
 * @author luobo.cs@raycloud.com
 * @since 2021/6/7 9:53 上午
 */
public class MyTransactionSynchronizationManager {

    private static final Log logger = LogFactory.getLog(MyTransactionSynchronizationManager.class);

    private static final ThreadLocal<Map<String, Map<Object, Object>>> resources = new NamedThreadLocal<Map<String, Map<Object, Object>>>("Transactional resources") {
        @Override
        protected Map<String, Map<Object, Object>> initialValue() {
            return new HashMap<>();
        }
    };

    private static final ThreadLocal<Map<String, Set<TransactionSynchronization>>> synchronizations = new NamedThreadLocal<Map<String, Set<TransactionSynchronization>>>("Transaction synchronizations") {
        @Override
        protected Map<String, Set<TransactionSynchronization>> initialValue() {
            return new HashMap<>();
        }
    };

    private static final ThreadLocal<Map<String, String>> currentTransactionName = new NamedThreadLocal<Map<String, String>>("Current transaction name") {
        @Override
        protected Map<String, String> initialValue() {
            return new HashMap<>();
        }
    };

    private static final ThreadLocal<Map<String, Boolean>> currentTransactionReadOnly = new NamedThreadLocal<Map<String, Boolean>>("Current transaction read-only status") {
        @Override
        protected Map<String, Boolean> initialValue() {
            return new HashMap<>();
        }
    };

    private static final ThreadLocal<Map<String, Integer>> currentTransactionIsolationLevel = new NamedThreadLocal<Map<String, Integer>>("Current transaction isolation level") {
        @Override
        protected Map<String, Integer> initialValue() {
            return new HashMap<>();
        }
    };

    private static final ThreadLocal<Map<String, Boolean>> actualTransactionActive = new NamedThreadLocal<Map<String, Boolean>>("Actual transaction active") {
        @Override
        protected Map<String, Boolean> initialValue() {
            return new HashMap<>();
        }
    };


    //-------------------------------------------------------------------------
    // Management of transaction-associated resource handles
    //-------------------------------------------------------------------------

    /**
     * Return all resources that are bound to the current thread.
     * <p>Mainly for debugging purposes. Resource managers should always invoke
     * {@code hasResource} for a specific resource key that they are interested in.
     *
     * @return a Map with resource keys (usually the resource factory) and resource
     * values (usually the active resource object), or an empty Map if there are
     * currently no resources bound
     * @see #hasResource
     */
    public static Map<Object, Object> getResourceMap(String datasource) {
        Map<Object, Object> map = resources.get().get(datasource);
        return (map != null ? Collections.unmodifiableMap(map) : Collections.emptyMap());
    }

    /**
     * Check if there is a resource for the given key bound to the current thread.
     *
     * @param key the key to check (usually the resource factory)
     * @return if there is a value bound to the current thread
     * @see ResourceTransactionManager#getResourceFactory()
     */
    public static boolean hasResource(String datasource, Object key) {
        Object actualKey = MyTransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
        Object value = doGetResource(datasource, actualKey);
        return (value != null);
    }

    /**
     * Retrieve a resource for the given key that is bound to the current thread.
     *
     * @param key the key to check (usually the resource factory)
     * @return a value bound to the current thread (usually the active
     * resource object), or {@code null} if none
     * @see ResourceTransactionManager#getResourceFactory()
     */
    @Nullable
    public static Object getResource(String datasource, Object key) {
        Object actualKey = MyTransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
        Object value = doGetResource(datasource, actualKey);
        if (value != null && logger.isTraceEnabled()) {
            logger.trace("Retrieved value [" + value + "] for key [" + actualKey + "] bound to thread [" +
                    Thread.currentThread().getName() + "]");
        }
        return value;
    }

    /**
     * Actually check the value of the resource that is bound for the given key.
     */
    @Nullable
    private static Object doGetResource(String datasource, Object actualKey) {
        Map<Object, Object> map = resources.get().get(datasource);
        if (map == null) {
            return null;
        }
        Object value = map.get(actualKey);
        // Transparently remove ResourceHolder that was marked as void...
        if (value instanceof ResourceHolder && ((ResourceHolder) value).isVoid()) {
            map.remove(actualKey);
            // Remove entire ThreadLocal if empty...
            if (map.isEmpty()) {
                resources.remove();
            }
            value = null;
        }
        return value;
    }

    /**
     * Bind the given resource for the given key to the current thread.
     *
     * @param key   the key to bind the value to (usually the resource factory)
     * @param value the value to bind (usually the active resource object)
     * @throws IllegalStateException if there is already a value bound to the thread
     * @see ResourceTransactionManager#getResourceFactory()
     */
    public static void bindResource(String datasource, Object key, Object value) throws IllegalStateException {
        Object actualKey = MyTransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
        Assert.notNull(value, "Value must not be null");
        Map<Object, Object> map = resources.get().computeIfAbsent(datasource, k -> new HashMap<>());
        // set ThreadLocal Map if none found
        Object oldValue = map.put(actualKey, value);
        // Transparently suppress a ResourceHolder that was marked as void...
        if (oldValue instanceof ResourceHolder && ((ResourceHolder) oldValue).isVoid()) {
            oldValue = null;
        }
        if (oldValue != null) {
            throw new IllegalStateException("Already value [" + oldValue + "] for key [" +
                    actualKey + "] bound to thread [" + Thread.currentThread().getName() + "]");
        }
        if (logger.isTraceEnabled()) {
            logger.trace("Bound value [" + value + "] for key [" + actualKey + "] to thread [" +
                    Thread.currentThread().getName() + "]");
        }
    }

    /**
     * Unbind a resource for the given key from the current thread.
     *
     * @param key the key to unbind (usually the resource factory)
     * @return the previously bound value (usually the active resource object)
     * @throws IllegalStateException if there is no value bound to the thread
     * @see ResourceTransactionManager#getResourceFactory()
     */
    public static Object unbindResource(String datasource, Object key) throws IllegalStateException {
        Object actualKey = MyTransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
        Object value = doUnbindResource(datasource, actualKey);
        if (value == null) {
            throw new IllegalStateException(
                    "No value for key [" + actualKey + "] bound to thread [" + Thread.currentThread().getName() + "]");
        }
        return value;
    }

    /**
     * Unbind a resource for the given key from the current thread.
     *
     * @param key the key to unbind (usually the resource factory)
     * @return the previously bound value, or {@code null} if none bound
     */
    @Nullable
    public static Object unbindResourceIfPossible(String datasource, Object key) {
        Object actualKey = MyTransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
        return doUnbindResource(datasource, actualKey);
    }

    /**
     * Actually remove the value of the resource that is bound for the given key.
     */
    @Nullable
    private static Object doUnbindResource(String datasource, Object actualKey) {
        final Map<String, Map<Object, Object>> stringMapMap = resources.get();
        Map<Object, Object> map = resources.get().get(datasource);
        if (map == null) {
            return null;
        }
        Object value = map.remove(actualKey);
        // Remove entire ThreadLocal if empty...
        if (stringMapMap.isEmpty()) {
            resources.remove();
        }
        // Transparently suppress a ResourceHolder that was marked as void...
        if (value instanceof ResourceHolder && ((ResourceHolder) value).isVoid()) {
            value = null;
        }
        if (value != null && logger.isTraceEnabled()) {
            logger.trace("Removed value [" + value + "] for key [" + actualKey + "] from thread [" +
                    Thread.currentThread().getName() + "]");
        }
        return value;
    }


    //-------------------------------------------------------------------------
    // Management of transaction synchronizations
    //-------------------------------------------------------------------------

    /**
     * Return if transaction synchronization is active for the current thread.
     * Can be called before register to avoid unnecessary instance creation.
     *
     * @see #registerSynchronization
     */
    public static boolean isSynchronizationActive(String datasource) {
        return (synchronizations.get().get(datasource) != null);
    }

    /**
     * Activate transaction synchronization for the current thread.
     * Called by a transaction manager on transaction begin.
     *
     * @throws IllegalStateException if synchronization is already active
     */
    public static void initSynchronization(String datasource) throws IllegalStateException {
        if (isSynchronizationActive(datasource)) {
            throw new IllegalStateException("Cannot activate transaction synchronization - already active");
        }
        logger.trace("Initializing transaction synchronization");
        synchronizations.get().put(datasource, new LinkedHashSet<>());
    }

    /**
     * Register a new transaction synchronization for the current thread.
     * Typically called by resource management code.
     * <p>Note that synchronizations can implement the
     * {@link org.springframework.core.Ordered} interface.
     * They will be executed in an order according to their order value (if any).
     *
     * @param synchronization the synchronization object to register
     * @throws IllegalStateException if transaction synchronization is not active
     * @see org.springframework.core.Ordered
     */
    public static void registerSynchronization(String datasource, TransactionSynchronization synchronization)
            throws IllegalStateException {

        Assert.notNull(synchronization, "TransactionSynchronization must not be null");
        Set<TransactionSynchronization> synchs = synchronizations.get().get(datasource);
        if (synchs == null) {
            throw new IllegalStateException("Transaction synchronization is not active");
        }
        synchs.add(synchronization);
    }

    /**
     * Return an unmodifiable snapshot list of all registered synchronizations
     * for the current thread.
     *
     * @return unmodifiable List of TransactionSynchronization instances
     * @throws IllegalStateException if synchronization is not active
     * @see TransactionSynchronization
     */
    public static List<TransactionSynchronization> getSynchronizations(String datasource) throws IllegalStateException {
        Set<TransactionSynchronization> synchs = synchronizations.get().get(datasource);
        if (synchs == null) {
            throw new IllegalStateException("Transaction synchronization is not active");
        }
        // Return unmodifiable snapshot, to avoid ConcurrentModificationExceptions
        // while iterating and invoking synchronization callbacks that in turn
        // might register further synchronizations.
        if (synchs.isEmpty()) {
            return Collections.emptyList();
        } else {
            // Sort lazily here, not in registerSynchronization.
            List<TransactionSynchronization> sortedSynchs = new ArrayList<>(synchs);
            AnnotationAwareOrderComparator.sort(sortedSynchs);
            return Collections.unmodifiableList(sortedSynchs);
        }
    }

    /**
     * Deactivate transaction synchronization for the current thread.
     * Called by the transaction manager on transaction cleanup.
     *
     * @throws IllegalStateException if synchronization is not active
     */
    public static void clearSynchronization(String datasource) throws IllegalStateException {
        if (!isSynchronizationActive(datasource)) {
            throw new IllegalStateException("Cannot deactivate transaction synchronization - not active");
        }
        logger.trace("Clearing transaction synchronization");
        synchronizations.get().get(datasource).clear();
    }


    //-------------------------------------------------------------------------
    // Exposure of transaction characteristics
    //-------------------------------------------------------------------------

    /**
     * Expose the name of the current transaction, if any.
     * Called by the transaction manager on transaction begin and on cleanup.
     *
     * @param name the name of the transaction, or {@code null} to reset it
     * @see org.springframework.transaction.TransactionDefinition#getName()
     */
    public static void setCurrentTransactionName(String datasource, @Nullable String name) {
        currentTransactionName.get().put(datasource, name);
    }

    /**
     * Return the name of the current transaction, or {@code null} if none set.
     * To be called by resource management code for optimizations per use case,
     * for example to optimize fetch strategies for specific named transactions.
     *
     * @see org.springframework.transaction.TransactionDefinition#getName()
     */
    @Nullable
    public static String getCurrentTransactionName(String datasource) {
        return currentTransactionName.get().get(datasource);
    }

    /**
     * Expose a read-only flag for the current transaction.
     * Called by the transaction manager on transaction begin and on cleanup.
     *
     * @param readOnly {@code true} to mark the current transaction
     *                 as read-only; {@code false} to reset such a read-only marker
     * @see org.springframework.transaction.TransactionDefinition#isReadOnly()
     */
    public static void setCurrentTransactionReadOnly(String datasource, boolean readOnly) {
        currentTransactionReadOnly.get().put(datasource, readOnly ? Boolean.TRUE : null);
    }

    /**
     * Return whether the current transaction is marked as read-only.
     * To be called by resource management code when preparing a newly
     * created resource (for example, a Hibernate Session).
     * <p>Note that transaction synchronizations receive the read-only flag
     * as argument for the {@code beforeCommit} callback, to be able
     * to suppress change detection on commit. The present method is meant
     * to be used for earlier read-only checks, for example to set the
     * flush mode of a Hibernate Session to "FlushMode.NEVER" upfront.
     *
     * @see org.springframework.transaction.TransactionDefinition#isReadOnly()
     * @see TransactionSynchronization#beforeCommit(boolean)
     */
    public static boolean isCurrentTransactionReadOnly(String datasource) {
        return (currentTransactionReadOnly.get().get(datasource) != null);
    }

    /**
     * Expose an isolation level for the current transaction.
     * Called by the transaction manager on transaction begin and on cleanup.
     *
     * @param isolationLevel the isolation level to expose, according to the
     *                       JDBC Connection constants (equivalent to the corresponding Spring
     *                       TransactionDefinition constants), or {@code null} to reset it
     * @see java.sql.Connection#TRANSACTION_READ_UNCOMMITTED
     * @see java.sql.Connection#TRANSACTION_READ_COMMITTED
     * @see java.sql.Connection#TRANSACTION_REPEATABLE_READ
     * @see java.sql.Connection#TRANSACTION_SERIALIZABLE
     * @see org.springframework.transaction.TransactionDefinition#ISOLATION_READ_UNCOMMITTED
     * @see org.springframework.transaction.TransactionDefinition#ISOLATION_READ_COMMITTED
     * @see org.springframework.transaction.TransactionDefinition#ISOLATION_REPEATABLE_READ
     * @see org.springframework.transaction.TransactionDefinition#ISOLATION_SERIALIZABLE
     * @see org.springframework.transaction.TransactionDefinition#getIsolationLevel()
     */
    public static void setCurrentTransactionIsolationLevel(String datasource, @Nullable Integer isolationLevel) {
        currentTransactionIsolationLevel.get().put(datasource, isolationLevel);
    }

    /**
     * Return the isolation level for the current transaction, if any.
     * To be called by resource management code when preparing a newly
     * created resource (for example, a JDBC Connection).
     *
     * @return the currently exposed isolation level, according to the
     * JDBC Connection constants (equivalent to the corresponding Spring
     * TransactionDefinition constants), or {@code null} if none
     * @see java.sql.Connection#TRANSACTION_READ_UNCOMMITTED
     * @see java.sql.Connection#TRANSACTION_READ_COMMITTED
     * @see java.sql.Connection#TRANSACTION_REPEATABLE_READ
     * @see java.sql.Connection#TRANSACTION_SERIALIZABLE
     * @see org.springframework.transaction.TransactionDefinition#ISOLATION_READ_UNCOMMITTED
     * @see org.springframework.transaction.TransactionDefinition#ISOLATION_READ_COMMITTED
     * @see org.springframework.transaction.TransactionDefinition#ISOLATION_REPEATABLE_READ
     * @see org.springframework.transaction.TransactionDefinition#ISOLATION_SERIALIZABLE
     * @see org.springframework.transaction.TransactionDefinition#getIsolationLevel()
     */
    @Nullable
    public static Integer getCurrentTransactionIsolationLevel(String datasource) {
        return currentTransactionIsolationLevel.get().get(datasource);
    }

    /**
     * Expose whether there currently is an actual transaction active.
     * Called by the transaction manager on transaction begin and on cleanup.
     *
     * @param active {@code true} to mark the current thread as being associated
     *               with an actual transaction; {@code false} to reset that marker
     */
    public static void setActualTransactionActive(String datasource, boolean active) {
        actualTransactionActive.get().put(datasource, active ? Boolean.TRUE : null);
    }

    /**
     * Return whether there currently is an actual transaction active.
     * This indicates whether the current thread is associated with an actual
     * transaction rather than just with active transaction synchronization.
     * <p>To be called by resource management code that wants to discriminate
     * between active transaction synchronization (with or without backing
     * resource transaction; also on PROPAGATION_SUPPORTS) and an actual
     * transaction being active (with backing resource transaction;
     * on PROPAGATION_REQUIRED, PROPAGATION_REQUIRES_NEW, etc).
     */
    public static boolean isActualTransactionActive(String datasource) {
        return actualTransactionActive.get().get(datasource) != null;
    }


    /**
     * Clear the entire transaction synchronization state for the current thread:
     * registered synchronizations as well as the various transaction characteristics.
     *
     * @see #setCurrentTransactionName
     * @see #setCurrentTransactionReadOnly
     * @see #setCurrentTransactionIsolationLevel
     * @see #setActualTransactionActive
     */
    public static void clear(String datasource) {
        synchronizations.get().remove(datasource);
        currentTransactionName.get().remove(datasource);
        currentTransactionReadOnly.get().remove(datasource);
        currentTransactionIsolationLevel.get().remove(datasource);
        actualTransactionActive.get().remove(datasource);
    }

    public static void clear() {
        synchronizations.remove();
        currentTransactionName.remove();
        currentTransactionReadOnly.remove();
        currentTransactionIsolationLevel.remove();
        actualTransactionActive.remove();
    }


}
