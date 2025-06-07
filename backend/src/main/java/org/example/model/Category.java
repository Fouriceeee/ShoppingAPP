package org.example.model;

public enum Category {
    VIDEOCARD,CPU,FRUIT,DRINK,SNACK,PAD,LAPTOP;

    public static boolean containsEnumValue(Class<? extends Enum<?>> enumClass, String value) {
        for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
            if (enumConstant.name().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
