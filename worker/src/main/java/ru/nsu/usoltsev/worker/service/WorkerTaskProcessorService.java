package ru.nsu.usoltsev.worker.service;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.paukov.combinatorics3.Generator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.nsu.usoltsev.worker.model.request.WorkerTaskRequest;
import ru.nsu.usoltsev.worker.model.response.WorkerTaskResponse;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerTaskProcessorService {
    private final ManagerClientService managerClientService;
    private final ObjectMapperClient objectMapperClient;
    private static final char[] SYMBOLS = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    @Async("taskHandlerExecutor")
    public void processTaskAsync(WorkerTaskRequest workerTaskRequest, long tag, Channel channel) throws IOException {
        BigInteger totalCombinations = BigInteger.ZERO;
        BigInteger base = BigInteger.valueOf(SYMBOLS.length);
        for (int k = 1; k <= workerTaskRequest.getMaxLength(); k++) {
            totalCombinations = totalCombinations.add(base.pow(k));
        }

        int chunkNumber = workerTaskRequest.getChunkNumber();
        int totalChunks = workerTaskRequest.getTotalChunks();

        BigInteger chunkSize = totalCombinations.divide(BigInteger.valueOf(totalChunks));
        BigInteger startIndex = chunkSize.multiply(BigInteger.valueOf(chunkNumber - 1));
        BigInteger endIndex = (chunkNumber == totalChunks)
                ? totalCombinations
                : chunkSize.multiply(BigInteger.valueOf(chunkNumber));

        List<String> words = processTask(workerTaskRequest.getMaxLength(),
                startIndex.longValue(),
                endIndex.longValue(),
                workerTaskRequest.getHash()
        );
        log.info("Matching words: {}", objectMapperClient.objectToJson(words));

        managerClientService.sendResultToManger(WorkerTaskResponse
                .builder()
                .requestId(workerTaskRequest.getRequestId())
                .chunkNumber(chunkNumber)
                .matchingWords(words)
                .build()
        );
    }

    private List<String> processTask(int maxWordLength, long startIndex, long endIndex, String targetHash) {
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
