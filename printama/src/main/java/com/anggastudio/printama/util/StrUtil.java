package com.anggastudio.printama.util;

public class StrUtil {

    private StrUtil() {

    }

    public static String encodeNonAscii(String text) {
        if (text == null) {
            return null;
        }
        
        return text
                // A characters
                .replace('à', 'a').replace('á', 'a').replace('â', 'a').replace('ã', 'a')
                .replace('ä', 'a').replace('å', 'a').replace('À', 'A').replace('Á', 'A')
                .replace('Â', 'A').replace('Ã', 'A').replace('Ä', 'A').replace('Å', 'A')
                // E characters
                .replace('è', 'e').replace('é', 'e').replace('ê', 'e').replace('ë', 'e')
                .replace('ě', 'e').replace('È', 'E').replace('É', 'E').replace('Ê', 'E')
                .replace('Ë', 'E').replace('Ě', 'E')
                // I characters
                .replace('ì', 'i').replace('í', 'i').replace('î', 'i').replace('ï', 'i')
                .replace('Ì', 'I').replace('Í', 'I').replace('Î', 'I').replace('Ï', 'I')
                // O characters
                .replace('ò', 'o').replace('ó', 'o').replace('ô', 'o').replace('õ', 'o')
                .replace('ö', 'o').replace('ø', 'o').replace('Ò', 'O').replace('Ó', 'O')
                .replace('Ô', 'O').replace('Õ', 'O').replace('Ö', 'O').replace('Ø', 'O')
                // U characters
                .replace('ù', 'u').replace('ú', 'u').replace('û', 'u').replace('ü', 'u')
                .replace('ů', 'u').replace('Ù', 'U').replace('Ú', 'U').replace('Û', 'U')
                .replace('Ü', 'U').replace('Ů', 'U')
                // Y characters
                .replace('ý', 'y').replace('ÿ', 'y').replace('Ý', 'Y').replace('Ÿ', 'Y')
                // Special characters
                .replace('ç', 'c').replace('Ç', 'C')
                .replace('ñ', 'n').replace('Ñ', 'N').replace('ň', 'n').replace('Ň', 'N')
                // Czech characters
                .replace('č', 'c').replace('ď', 'd').replace('ř', 'r').replace('š', 's')
                .replace('ť', 't').replace('ž', 'z').replace('Č', 'C').replace('Ď', 'D')
                .replace('Ř', 'R').replace('Š', 'S').replace('Ť', 'T').replace('Ž', 'Z')
                // Ligatures and special combinations
                .replace("æ", "ae").replace("Æ", "AE")
                .replace("œ", "oe").replace("Œ", "OE")
                .replace("ß", "ss");
    }
}
