package de.upb.crypto.math.standalone.test;

import de.upb.crypto.math.serialization.Representation;
import de.upb.crypto.math.serialization.StandaloneRepresentable;
import de.upb.crypto.math.serialization.annotations.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RepresentedEnumParams {

    public static StandaloneTestParams get() {
        return new StandaloneTestParams(RepresentedEnum.class, new RepresentedEnum());
    }

    public enum RepresentableEnum {
        ENUM_VALUE1, ENUM_VALUE2, ENUM_VALUE3
    }

    public static class RepresentedEnum implements StandaloneRepresentable {

        @Represented
        RepresentableEnum testEnum;
        @Represented
        RepresentableEnum nullValue;
        @RepresentedArray(elementRestorer = @Represented)
        RepresentableEnum[] enumArray;
        @RepresentedList(elementRestorer = @Represented)
        List<RepresentableEnum> enumList;
        @RepresentedSet(elementRestorer = @Represented)
        Set<RepresentableEnum> enumSet;
        @RepresentedMap(keyRestorer = @Represented, valueRestorer = @Represented)
        Map<RepresentableEnum, RepresentableEnum> enumMap;

        public RepresentedEnum() {
            testEnum = RepresentableEnum.ENUM_VALUE1;
            nullValue = null;
            enumArray = RepresentableEnum.values();
            enumList = new ArrayList<>(Arrays.asList(RepresentableEnum.values()));
            enumSet = new HashSet<>(Arrays.asList(RepresentableEnum.values()));
            enumMap = new HashMap<>(
                    Arrays.stream(RepresentableEnum.values())
                            .collect(Collectors.toMap(Function.identity(), Function.identity())));
        }

        public RepresentedEnum(Representation representation) {
            AnnotatedRepresentationUtil.restoreAnnotatedRepresentation(representation, this);
        }

        @Override
        public Representation getRepresentation() {
            return AnnotatedRepresentationUtil.putAnnotatedRepresentation(this);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RepresentedEnum that = (RepresentedEnum) o;
            return testEnum == that.testEnum &&
                    nullValue == that.nullValue &&
                    Arrays.equals(enumArray, that.enumArray) &&
                    Objects.equals(enumList, that.enumList) &&
                    Objects.equals(enumSet, that.enumSet) &&
                    Objects.equals(enumMap, that.enumMap);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(testEnum, nullValue, enumList, enumSet, enumMap);
            result = 31 * result + Arrays.hashCode(enumArray);
            return result;
        }
    }
}
