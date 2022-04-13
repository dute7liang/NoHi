package com.nohi.common.utils.bean;

import com.fasterxml.jackson.databind.BeanProperty;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.cglib.core.Converter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public final class BeanConvertUtils extends BeanUtils {
    private BeanConvertUtils() {
    }

    public static <T> T newInstance(Class<?> clazz) {
        return (T) instantiateClass(clazz);
    }

    public static <T> T newInstance(String clazzStr) {
        try {
            Class<?> clazz = Class.forName(clazzStr);
            return newInstance(clazz);
        } catch (ClassNotFoundException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static Object getProperty(Object bean, String propertyName) {
        PropertyDescriptor pd = getPropertyDescriptor(bean.getClass(), propertyName);
        if (pd == null) {
            throw new RuntimeException("Could not read property '" + propertyName + "' from bean PropertyDescriptor is null");
        } else {
            Method readMethod = pd.getReadMethod();
            if (readMethod == null) {
                throw new RuntimeException("Could not read property '" + propertyName + "' from bean readMethod is null");
            } else {
                if (!readMethod.isAccessible()) {
                    readMethod.setAccessible(true);
                }

                try {
                    return readMethod.invoke(bean, new Object[0]);
                } catch (Throwable var5) {
                    throw new RuntimeException("Could not read property '" + propertyName + "' from bean", var5);
                }
            }
        }
    }

    public static void setProperty(Object bean, String propertyName, Object value) {
        PropertyDescriptor pd = getPropertyDescriptor(bean.getClass(), propertyName);
        if (pd == null) {
            throw new RuntimeException("Could not set property '" + propertyName + "' to bean PropertyDescriptor is null");
        } else {
            Method writeMethod = pd.getWriteMethod();
            if (writeMethod == null) {
                throw new RuntimeException("Could not set property '" + propertyName + "' to bean writeMethod is null");
            } else {
                if (!writeMethod.isAccessible()) {
                    writeMethod.setAccessible(true);
                }

                try {
                    writeMethod.invoke(bean, new Object[]{value});
                } catch (Throwable var6) {
                    throw new RuntimeException("Could not set property '" + propertyName + "' to bean", var6);
                }
            }
        }
    }

    public static Object generator(Object superBean, BeanProperty... props) {
        Class<?> superclass = superBean.getClass();
        Object genBean = generator(superclass, props);
        copy(superBean, genBean);
        return genBean;
    }

    public static MultiValueMap<String, String> object2Map(Object obj) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap();
        if (obj == null) {
            return map;
        } else {
            Class clazz = obj.getClass();
            Field[] fields = clazz.getDeclaredFields();

            try {
                Field[] var4 = fields;
                int var5 = fields.length;

                for (int var6 = 0; var6 < var5; ++var6) {
                    Field field = var4[var6];
                    field.setAccessible(true);
                    if (field.get(obj) != null) {
                        map.add(field.getName(), field.get(obj).toString());
                    }
                }

                return map;
            } catch (Exception var8) {
                var8.printStackTrace();
                return null;
            }
        }
    }

    public static <T> T copy(Object src, Class<T> clazz) {
        T to = newInstance(clazz);
        if (src != null) {
            BeanCopier copier = BeanCopier.create(src.getClass(), clazz, false);
            copier.copy(src, to, (Converter) null);
        }

        return to;
    }

    public static <S, T> List<T> copy(List<S> srcList, Class<T> clazz) {
        return (List) (Optional.ofNullable(srcList).isPresent() ? (List) srcList.stream().map((srcObj) -> {
            return copy(srcObj, clazz);
        }).collect(Collectors.toList()) : new ArrayList());
    }

    public static void copy(Object src, Object dist) {
        BeanCopier copier = BeanCopier.create(src.getClass(), dist.getClass(), false);
        copier.copy(src, dist, (Converter) null);
    }

    public static <T, R> R convert(T source, Class<R> targetClass) {
        if (source == null) {
            return null;
        } else {
            Object target = null;

            try {
                target = targetClass.newInstance();
            } catch (IllegalAccessException | InstantiationException var4) {
                throw new RuntimeException("转换错误", var4);
            }

            copyProperties(source, target);
            return (R) target;
        }
    }

    public static <T, R> R convert(T source, Class<R> targetClass, String... ignoredProperties) {
        if (source == null) {
            return null;
        } else {
            Object target = null;

            try {
                target = targetClass.newInstance();
            } catch (IllegalAccessException | InstantiationException var5) {
                throw new RuntimeException("转换错误", var5);
            }

            copyProperties(source, target, ignoredProperties);
            return (R) target;
        }
    }

    public static Map toMap(Object src) {
        return BeanMap.create(src);
    }

    public static <T> T toBean(Map<String, Object> beanMap, Class<T> valueType) {
        T bean = newInstance(valueType);
        PropertyDescriptor[] beanPds = getPropertyDescriptors(valueType);
        PropertyDescriptor[] var4 = beanPds;
        int var5 = beanPds.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            PropertyDescriptor propDescriptor = var4[var6];
            String propName = propDescriptor.getName();
            if (!propName.equals("class") && beanMap.containsKey(propName)) {
                Method writeMethod = propDescriptor.getWriteMethod();
                if (null != writeMethod) {
                    Object value = beanMap.get(propName);
                    if (!writeMethod.isAccessible()) {
                        writeMethod.setAccessible(true);
                    }

                    try {
                        writeMethod.invoke(bean, new Object[]{value});
                    } catch (Throwable var12) {
                        throw new RuntimeException("Could not set property '" + propName + "' to bean", var12);
                    }
                }
            }
        }

        return bean;
    }

    public static <T> List<T> buildTree(List<T> list, Function<T, ?> getId, Function<T, ?> getParentId, BiConsumer<T, List<T>> setChildren) {
        List<Object> idList = new ArrayList();
        Map<Object, List<T>> childrenMap = new HashMap();
        Iterator var6 = list.iterator();

        while (var6.hasNext()) {
            T item = (T) var6.next();
            Object parentId = getParentId.apply(item);
            if (parentId == null) {
                parentId = "";
            }

            ((List) childrenMap.computeIfAbsent(parentId, (k) -> {
                return new ArrayList();
            })).add(item);
            Object id = getId.apply(item);
            setChildren.accept(item, childrenMap.computeIfAbsent(id, (k) -> {
                return new ArrayList();
            }));
            idList.add(id);
        }

        return (List) list.stream().filter((item) -> {
            return !idList.contains(getParentId.apply(item));
        }).collect(Collectors.toList());
    }

    public static <T, R> List<R> convertList(List<T> list, Function<T, R> converter) {
        if (list == null) {
            return null;
        } else {
            List<R> newList = new ArrayList(list.size());
            list.forEach((item) -> {
                newList.add(converter.apply(item));
            });
            return newList;
        }
    }

    public static <T, R> List<R> convertList(List<T> list, Class<R> targetType) {
        if (list == null) {
            return null;
        } else {
            List<R> newList = new ArrayList(list.size());
            list.forEach((item) -> {
                newList.add(convert(item, targetType));
            });
            return newList;
        }
    }

    public static <K, V, T> Map<K, V> buildMap(List<T> list, Function<T, K> key, Function<T, V> value) {
        Map<K, V> m = new HashMap(list.size());
        list.forEach((i) -> {
            m.put(key.apply(i), value.apply(i));
        });
        return m;
    }

    @FunctionalInterface
    public interface CallBack<S, T> {
        /**
         * 回调方法
         *
         * @param s 源对象
         * @param t 目标对象
         */
        void callBack(S s, T t);
    }

    /**
     * 转换list 对象
     *
     * @param sources        源对象
     * @param targetSupplier 目标对象供应方
     * @param callBack       回调方法
     * @param <S>            源对象类型
     * @param <T>            目标对象类型
     * @Return 转换对象
     */
    public static <S, T> List<T> convertListTo(List<S> sources, Supplier<T> targetSupplier, CallBack<S, T> callBack) {
        if (null == sources || null == targetSupplier) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>(sources.size());
        for (S source : sources) {
            T target = targetSupplier.get();
            copyProperties(source, target);
            if (callBack != null) {
                callBack.callBack(source, target);
            }
            list.add(target);
        }
        return list;
    }

    /**
     * 转换list 对象
     *
     * @param sources        源对象
     * @param targetSupplier 目标对象供应方
     * @param <S>            源对象类型
     * @param <T>            目标对象类型
     */
    public static <S, T> List<T> convertListTo(List<S> sources, Supplier<T> targetSupplier) {
        return convertListTo(sources, targetSupplier, null);
    }

    /**
     * 转换对象
     *
     * @param source         源对象
     * @param targetSupplier 目标对象供应方
     * @param <S>            源对象类型
     * @param <T>            目标对象类型
     * @return 目标对象
     */
    public static <S, T> T convertTo(S source, Supplier<T> targetSupplier) {
        return convertTo(source, targetSupplier, null);
    }

    /**
     * 转换对象
     *
     * @param source         源对象
     * @param targetSupplier 目标对象供应方
     * @param callBack       回调方法
     * @param <S>            源对象类型
     * @param <T>            目标对象类型
     * @return 目标对象
     */
    public static <S, T> T convertTo(S source, Supplier<T> targetSupplier, CallBack<S, T> callBack) {
        if (null == source || null == targetSupplier) {
            return null;
        }
        T target = targetSupplier.get();
        copyProperties(source, target);
        if (callBack != null) {
            callBack.callBack(source, target);
        }
        return target;
    }

}
