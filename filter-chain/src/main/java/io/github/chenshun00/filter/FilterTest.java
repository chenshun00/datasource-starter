package io.github.chenshun00.filter;

import io.github.chenshun00.filter.impl.DefaultContext;

import java.util.HashMap;

/**
 * @author chenshun00@gmail.com
 * @since 2022/1/23 2:25 下午
 */
public class FilterTest {

    public static void main(String[] args) {
        FilterChain filterChain = new FilterChain();
        filterChain
                .add(new Filter() {

                    @Override
                    public String name() {
                        return "dd";
                    }

                    @Override
                    public int order() {
                        return 20;
                    }

                    @Override
                    public boolean execute(Context context) {
                        System.out.println(name());
                        return true;
                    }
                })
                .add(new Filter() {
                    @Override
                    public String name() {
                        return "dde";
                    }

                    @Override
                    public boolean execute(Context context) {
                        System.out.println(name());
                        return true;
                    }
                }).sort();
        System.out.println(filterChain.filter(new DefaultContext(new HashMap<>())));
    }

}
