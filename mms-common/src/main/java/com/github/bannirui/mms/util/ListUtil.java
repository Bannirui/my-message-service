package com.github.bannirui.mms.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;

public class ListUtil {

    public static <T> List<List<T>> subList(List<T> tList, Integer subNum) {
        List<List<T>> tNewList = new ArrayList<>();
        int totalNum = tList.size();
        int insertTimes = totalNum / subNum;
        for(int i = 0; i <= insertTimes; ++i) {
            int priIndex = subNum * i;
            int lastIndex = priIndex + subNum;
            List<T> subNewList;
            if (i == insertTimes) {
                subNewList = tList.subList(priIndex, tList.size());
            } else {
                subNewList = tList.subList(priIndex, lastIndex);
            }
            if (CollectionUtils.isNotEmpty(subNewList)) {
                tNewList.add(subNewList);
            }
        }
        return tNewList;
    }
}
