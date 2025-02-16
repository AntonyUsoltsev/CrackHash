package ru.nsu.usoltsev.worker.service;

import org.paukov.combinatorics3.Generator;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

@Service
public class WorkerTaskProcessorService {
    public void processTask(String alphabet, int wordLength, BigInteger startIndex, BigInteger endIndex, String targetHash) {
        // Генерируем итератор по словам заданной длины
        List<Character> symbols = alphabet.chars().mapToObj(c -> (char) c).toList();
        Iterator<List<Character>> iterator = Generator
                .generateSequence(symbols)
                .withRepetition(wordLength)
                .iterator();

        BigInteger currentIndex = BigInteger.ZERO;
        while (iterator.hasNext() && currentIndex.compareTo(endIndex) <= 0) {
            List<Character> wordChars = iterator.next();
            // Если мы ещё не достигли нужного диапазона, пропускаем
            if (currentIndex.compareTo(startIndex) < 0) {
                currentIndex = currentIndex.add(BigInteger.ONE);
                continue;
            }

            StringBuilder candidateBuilder = new StringBuilder();
            wordChars.forEach(candidateBuilder::append);
            String candidate = candidateBuilder.toString();

            // Вычисляем MD5 хэш для candidate и сравниваем с targetHash
            // (здесь предполагается, что реализована функция computeMD5)
            if (computeMD5(candidate).equalsIgnoreCase(targetHash)) {
                // Если найдено совпадение, можно отправить результат менеджеру
                reportResultToManager(candidate);
            }
            currentIndex = currentIndex.add(BigInteger.ONE);
        }
    }

    private String computeMD5(String input) {
        // Реализуйте вычисление MD5 (например, используя MessageDigest)
        return "";
    }

    private void reportResultToManager(String candidate) {
        // Реализуйте вызов REST API менеджера для передачи найденного результата,
        // например, с использованием RestTemplate
    }
}
