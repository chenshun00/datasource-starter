package io.github.chenshun00.filter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author chenshun00@gmail.com
 * @since 2022/1/23 2:12 下午
 */
public class FilterChain {
    private final List<Filter> filters = new ArrayList<>();

    public FilterChain() {
    }

    public FilterChain(List<Filter> filter) {
        filters.addAll(filter);
    }

    public FilterChain add(Filter filter) {
        filters.add(filter);
        return this;
    }

    public FilterChain sort() {
        if (filters.isEmpty()) {
            return this;
        }
        filters.sort(Comparator.comparingInt(Filter::order));
        return this;
    }

    public boolean filter(Context context) {

        // Verify our parameters
        if (context == null) {
            throw new IllegalArgumentException();
        }

        // Execute the commands in this list until one returns true
        // or throws an exception
        boolean saveResult = true;
        for (Filter filter : filters) {
            try {
                final boolean b = filter.onPreExecute(context);
                if (b) {
                    saveResult = filter.execute(context);
                    if (!saveResult) {
                        break;
                    }
                    filter.onPostExecute(context);
                }
            } catch (Exception e) {
                filter.onError(context, e);
                break;
            }
        }
        return saveResult;
    }
}
