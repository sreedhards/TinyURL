package tinyurl.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class URLUtil {
    public static final URLUtil INSTANCE = new URLUtil();
    private static final String URL_REGEX = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$";

    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    private URLUtil() {
        initCharToIndexTable();
        initIndexToCharTable();
    }

    private static HashMap<Character, Integer> charToIndexTable;
    private static List<Character> indexToCharTable;

    private void initCharToIndexTable() {
        charToIndexTable = new HashMap<>();
        // 0->a, 1->b, ..., 25->z, ..., 52->0, 61->9
        for (int i = 0; i < 26; ++i) {
            char c = 'a';
            c += i;
            charToIndexTable.put(c, i);
        }
        for (int i = 26; i < 52; ++i) {
            char c = 'A';
            c += (i - 26);
            charToIndexTable.put(c, i);
        }
        for (int i = 52; i < 62; ++i) {
            char c = '0';
            c += (i - 52);
            charToIndexTable.put(c, i);
        }
    }

    private void initIndexToCharTable() {
        // 0->a, 1->b, ..., 25->z, ..., 52->0, 61->9
        indexToCharTable = new ArrayList<>();
        for (int i = 0; i < 26; ++i) {
            char c = 'a';
            c += i;
            indexToCharTable.add(c);
        }
        for (int i = 26; i < 52; ++i) {
            char c = 'A';
            c += (i - 26);
            indexToCharTable.add(c);
        }
        for (int i = 52; i < 62; ++i) {
            char c = '0';
            c += (i - 52);
            indexToCharTable.add(c);
        }
    }

    public static String createUrlID(Long id) {
        List<Integer> base62ID = convertBase10ToBase62ID(id);
        StringBuilder uniqueURLID = new StringBuilder();
        for (int digit : base62ID) {
            uniqueURLID.append(indexToCharTable.get(digit));
        }
        return uniqueURLID.toString();
    }

    private static List<Integer> convertBase10ToBase62ID(Long id) {
        List<Integer> digits = new LinkedList<>();
        while (id > 0) {
            int remainder = (int) (id % 62);
            ((LinkedList<Integer>) digits).addFirst(remainder);
            id /= 62;
        }
        return digits;
    }

    public static Long getDictionaryKeyFromUniqueID(String uniqueID) {
        List<Character> base62IDs = new ArrayList<>();
        for (int i = 0; i < uniqueID.length(); ++i) {
            base62IDs.add(uniqueID.charAt(i));
        }
        Long dictionaryKey = convertBase62ToBase10ID(base62IDs);
        return dictionaryKey;
    }

    private static Long convertBase62ToBase10ID(List<Character> ids) {
        long id = 0L;
        for (int i = 0, exp = ids.size() - 1; i < ids.size(); ++i, --exp) {
            int base10 = charToIndexTable.get(ids.get(i));
            id += (base10 * Math.pow(62.0, exp));
        }
        return id;
    }

    public boolean validateURL(String url) {
        Matcher m = URL_PATTERN.matcher(url);
        return m.matches();
    }
}
