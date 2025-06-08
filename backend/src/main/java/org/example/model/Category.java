package org.example.model;

public enum Category {
    VIDEOCARD,CPU,MOTHERBOARD,RAM,STORAGE,POWERSUPPLY, COOLING,CASE,PERIPHERALS,PAD,MONITOR,LAPTOP,GRAPHIC_TABLET;

    public static boolean containsEnumValue(Class<? extends Enum<?>> enumClass, String value) {
        for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
            if (enumConstant.name().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
