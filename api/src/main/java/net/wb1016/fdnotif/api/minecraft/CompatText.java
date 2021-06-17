package net.wb1016.fdnotif.api.minecraft;

import com.google.common.collect.Lists;

import java.util.List;

public interface CompatText {

    String getMessage();

    default String getTranslationKey() {
        return "";
    }

    default List getArgs() {
        return Lists.newArrayList();
    }


}
