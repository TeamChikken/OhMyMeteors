package me.emafire003.dev.ohmymeteors.compat.schemconvert;

import pitheguy.schemconvert.converter.Converter;
import pitheguy.schemconvert.converter.formats.SchematicFormats;

import java.io.File;
import java.io.IOException;

public class SchemConvertCompat {

    public static void convertToNbt(File schem, File outputNbt) throws IOException {
        new Converter().convert(schem, outputNbt, SchematicFormats.NBT);
    }
}
