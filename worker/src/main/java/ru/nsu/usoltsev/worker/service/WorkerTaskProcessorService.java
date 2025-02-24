package ru.nsu.usoltsev.worker.service;

import lombok.extern.slf4j.Slf4j;
import org.paukov.combinatorics3.Generator;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class WorkerTaskProcessorService {
    private static final char[] SYMBOLS = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    public List<String> processTask(int maxWordLength, long startIndex, long endIndex, String targetHash) {
        List<String> matchingWords = new ArrayList<>();
        long globalCount = 0;

        List<Character> symbolsList = new ArrayList<>();
        for (char c : SYMBOLS) {
            symbolsList.add(c);
        }

        for (int length = 1; length <= maxWordLength; length++) {
            if (globalCount >= endIndex) {
                break;
            }
            Iterator<List<Character>> iterator = Generator.permutation(symbolsList)
                    .withRepetitions(length)
                    .iterator();

            while (iterator.hasNext() && globalCount < endIndex) {
                List<Character> wordChars = iterator.next();

                if (globalCount >= startIndex) {
                    StringBuilder wordBuilder = new StringBuilder();
                    for (Character ch : wordChars) {
                        wordBuilder.append(ch);
                    }
                    String word = wordBuilder.toString();

                    if (compareMD5Hash(targetHash, word)) {
                        matchingWords.add(word);
                    }
                }
                globalCount++;
            }
        }
        return matchingWords;
    }

    private boolean compareMD5Hash(String targetHash, String word) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(word.getBytes(StandardCharsets.UTF_8));
            StringBuilder hashBuilder = new StringBuilder();
            for (byte b : digest) {
                hashBuilder.append(String.format("%02x", b));
            }
            String computedHash = hashBuilder.toString();
            return computedHash.equals(targetHash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Got error while comparing hashes", e);
            throw new RuntimeException(e);
        }
    }
}
