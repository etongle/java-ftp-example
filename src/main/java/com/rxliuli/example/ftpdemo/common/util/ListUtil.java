package com.rxliuli.example.ftpdemo.common.util;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * List 的工具类
 *
 * @author rxliuli
 */
public class ListUtil {
    /**
     * 获取两个集合中的交集
     * 比较相等性是直接使用 {@link T#hashCode()} 方法，所以集合中的元素类型 {@link T} 必须要重写这个方法
     * 注意：该方法的时间复杂度为 On，所以应尽可能的优先使用这个方法
     *
     * @param left  第一个集合
     * @param right 第二个集合
     * @param <T>   集合中元素的类型
     * @return 两个集合的交集
     */
    public static <T> Set<T> intersection(List<T> left, List<T> right) {
        final Set<T> set = new HashSet<>(right);
        return left.stream().filter(set::contains).collect(Collectors.toSet());
    }

    /**
     * 获取两个集合的并集
     * 比较相等性是直接使用 {@link T#hashCode()} 方法，所以集合中的元素类型 {@link T} 必须要重写这个方法
     * 注意：该方法的时间复杂度为 On，所以应尽可能的优先使用这个方法
     *
     * @param left  第一个集合
     * @param right 第二个集合
     * @param <T>   集合中元素的类型
     * @return 两个集合的并集
     */
    public static <T> Set<T> union(List<T> left, List<T> right) {
        final Set<T> unionSet = new HashSet<>();
        unionSet.addAll(left);
        unionSet.addAll(right);
        return unionSet;
    }

    /**
     * 从第一个集合中过滤掉在第二个集合中的元素
     * 比较相等性是直接使用 {@link T#hashCode()} 方法，所以集合中的元素类型 {@link T} 必须要重写这个方法
     * 注意：该方法的时间复杂度为 On，所以应尽可能的优先使用这个方法
     *
     * @param left  第一个集合
     * @param right 第二个集合
     * @param <T>   集合中元素的类型
     * @return 第一个集合过滤掉第二个集合所得到的剩下的元素集合
     */
    public static <T> List<T> filter(List<T> left, List<T> right) {
        final HashSet<T> set = new HashSet<>(right);
        return left.stream()
                .filter(l -> !set.contains(l))
                .collect(Collectors.toList());
    }

    /**
     * 从第一个集合中过滤掉在第二个集合中的元素
     * 需要相同类型，但不需要重写 {@code hashCode/equals} 方法，直接传入生成唯一标识的函数 {@param identity} 即可
     * 注意：该方法的时间复杂度为 On^2，如果类型相同没有特别的需求请使用 {@link #filter(List, List)} 方法
     *
     * @param left     第一个集合
     * @param right    第二个集合
     * @param identity 为元素生成唯一标识
     * @param <T>      集合中元素的类型
     * @param <R>      生成的唯一标识的类型
     * @return 第一个集合过滤掉第二个集合所得到的剩下的元素集合
     */
    public static <T, R> List<T> filter(List<T> left, List<T> right, Function<T, R> identity) {
        return filter(left, right, (BiFunction<T, T, Boolean>) (l, r) -> Objects.equals(identity.apply(l), identity.apply(r)));
    }

    /**
     * 从第一个集合中过滤掉在第二个集合中的元素
     * 需要相同类型，但不需要重写 {@code hashCode/equals} 方法，直接比较函数 {@param f} 即可
     * 注意：该方法的时间复杂度为 {@code On^2}，如果类型相同没有特别的需求请使用 {@link #filter(List, List)} 方法
     * <p>
     * 如果需要比较的仅仅是单个元素相关联的，即元素的属性之类的，那么请使用 {@link #filter(List, List, Function)} 更简单
     * 如果需要涉及到两个元素的才需要使用这个或者比较元素的类型实现了 {@link Comparable<T>} 接口，例如：
     * <code>
     * final List<User> users1 = Lists.newArrayList(new User("rx", 18), new User("haor", 15));
     * final List<User> users2 = Lists.newArrayList(new User("Ling", 15), new User("rx", 17));
     * final List<User> filter = ListUtil.filter(users1, users2, User::compareTo);
     * </code>
     *
     * @param left       第一个集合
     * @param right      第二个集合
     * @param comparator 比较器，指定如何比较两个元素是否相同
     * @param <T>        集合中元素的类型
     * @return 第一个集合过滤掉第二个集合所得到的剩下的元素集合
     */
    public static <T> List<T> filter(List<T> left, List<T> right, Comparator<T> comparator) {
        return filter(left, right, (BiFunction<T, T, Boolean>) (l, r) -> comparator.compare(l, r) == 0);
    }

    /**
     * 从第一个集合中过滤掉在第二个集合中的元素
     * 不需要相同类型，也不需要重写 {@code hashCode/equals} 方法，直接比较函数 {@param f} 即可
     * 注意：该方法的时间复杂度为 On^2，如果类型相同没有特别的需求请使用 {@link #filter(List, List)} 方法
     * <p>
     * 如果是相同类型的 List，推荐你使用 {@link #filter(List, List, Comparator)}。
     * 如果非要使用该方法，请参考下面的代码
     * <code>
     * final List<User> users1 = Lists.newArrayList(new User("rx", 17), new User("haor", 15));
     * final List<User> users2 = Lists.newArrayList(new User("Ling", 15), new User("rx", 17));
     * final List<User> filter = ListUtil.filter(users1, users2, (User u1, User u2) -> Objects.equals(u1.getName(), u2.getName()));
     * //或者
     * final List<User> filter = ListUtil.filter(users1, users2, (BiFunction<User, User, Boolean>) (u1, u2) -> Objects.equals(u1.getName(), u2.getName()));
     * </code>
     * 发生这种情况的原因是 {@link Comparator} 是一种特殊的 {@link BiFunction}
     *
     * @param left  第一个集合
     * @param right 第二个集合
     * @param f     比较函数
     * @param <T>   第一个集合的泛型
     * @param <U>   第二个集合的泛型
     * @return 第一个集合独有元素组成的列表
     */
    public static <T, U> List<T> filter(List<T> left, List<U> right, BiFunction<T, U, Boolean> f) {
        return left.stream()
                .filter(l -> right.stream().noneMatch(r -> f.apply(l, r)))
                .collect(Collectors.toList());
    }

    /**
     * 两个集合中的差异
     * 比较相等性是直接使用 {@link T#hashCode()} 方法，所以集合中的元素类型 {@link T} 必须要重写这个方法
     * 注意：该方法的时间复杂度为 On，所以应尽可能的优先使用这个方法
     *
     * @param left  左边的集合
     * @param right 右边的集合
     * @param <T>   集合泛型
     * @return 差异，主要分为 {@param left} 独有元素，{@param right} 独有元素以及公共元素
     */
    public static <T> Map<ListDiffState, List<T>> different(List<T> left, List<T> right) {
        final List<T> commonList = new ArrayList<>(intersection(left, right));
        final List<T> leftExclusive = filter(left, commonList);
        final List<T> rightExclusive = filter(right, commonList);
        final HashMap<ListDiffState, List<T>> map = new HashMap<>(3);
        map.put(ListDiffState.left, leftExclusive);
        map.put(ListDiffState.right, rightExclusive);
        map.put(ListDiffState.common, commonList);
        return map;
    }

//    /**
//     * 从第一个集合中过滤掉在第二个集合中的元素
//     * 不需要相同类型，也不需要重写 {@code hashCode/equals} 方法，直接比较函数 {@param f} 即可
//     * 注意：该方法的时间复杂度为 On^2，如果类型相同没有特别的需求请使用 {@link #union(List, List)} 方法   *
//     *
//     * @param left  第一个集合
//     * @param right 第二个集合
//     * @param f     比较器，指定如何比较两个元素是否相同
//     * @param <T>   集合中元素的类型
//     * @return 第一个集合过滤掉第二个集合所得到的剩下的元素集合
//     */
//    public static <T> Set<T> union(List<T> left, List<T> right, Comparator<T> f) {
//        return union(left, right, (BiFunction<T, T, Boolean>) (l, r) -> f.compare(l, r) == 0);
//    }
//
//
//    /**
//     * 获取两个集合的并集
//     * 不需要相同类型，也不需要重写 {@code hashCode/equals} 方法，直接比较函数 {@param f} 即可
//     * 注意：该方法的时间复杂度为 On^2，如果类型相同没有特别的需求请使用 {@link #union(List, List)} 方法
//     *
//     * @param left  第一个集合
//     * @param right 第二个集合
//     * @param f     比较两个集合中元素是否相等的方法
//     * @param <T>   第一个集合元素的类型
//     * @param <U>   第二个集合元素的类型
//     * @param <R>   返回结果集合元素的类型，此处使用了强制类型转换，需要用户去生命类型 {@link T} 和 {@link U} 的最小类型上界 {@link R}
//     * @return 两个集合的并集
//     */
//    @SuppressWarnings("unchecked")
//    public static <T, U, R> Set<R> union(List<T> left, List<U> right, BiFunction<T, U, Boolean> f) {
//        final Set<R> result = filter(left, right, f).stream().map(l -> (R) l).collect(Collectors.toSet());
//        result.addAll(right.stream().map(r -> (R) r).collect(Collectors.toList()));
//        return result;
//    }

    /**
     * List 差异状态
     */
    public static enum ListDiffState {
        /**
         * 左边集合的独有元素
         */
        left,
        /**
         * 右边集合的独有元素
         */
        right,
        /**
         * 两个集合的共有元素
         */
        common
    }
}
