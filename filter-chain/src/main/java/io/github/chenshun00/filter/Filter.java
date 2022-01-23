package io.github.chenshun00.filter;

/**
 * @author chenshun00@gmail.com
 * @since 2022/1/23 2:08 下午
 */
public interface Filter {

    default int order() {
        return 100;
    }

    default String name() {
        return this.getClass().getName();
    }

    /**
     * 前执行
     *
     * @param context 上下文
     * @return boolean
     */
    default boolean onPreExecute(Context context) {
        return true;
    }

    /**
     * 执行
     *
     * @param context 上下文
     * @return boolean
     */
    boolean execute(Context context);

    /**
     * 后执行
     *
     * @param context 上下文
     */
    default void onPostExecute(Context context) {

    }

    default void onError(Context context, Throwable throwable) {

    }

}
