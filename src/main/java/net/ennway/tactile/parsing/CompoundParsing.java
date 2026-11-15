package net.ennway.tactile.parsing;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CompoundParsing {
    public static String debug;
    public static Tag foundTag;
    public static String[] separateTagObjectString(String location) {
        if (location.contains("."))
        {
            return new String[] { location.substring(0, location.indexOf(".")), location.substring(location.indexOf(".") + 1) };
        }
        return new String[] {location, ""};
    }
    // Should convert a CompoundTag with {extra = { One = 1, Two = 2 }} with location "extra.one" into 1, etc.
    public static Tag getTagObject(CompoundTag tag, String location)
    {
        List<String> subtags = new ArrayList<>();

        while (location.contains("."))
        {
            subtags.add(location.substring(0, location.indexOf(".")));
            location = location.substring(location.indexOf(".") + 1);
        }
        subtags.add(location);

        foundTag = null;

        tag.getAllKeys().forEach(T -> {
            if (Objects.equals(T.toLowerCase(), subtags.getFirst().toLowerCase())) {
                foundTag = tag.get(T);
                debug = subtags.getFirst() + " .. " + T;
            }
            });

        return foundTag;
    }
}
