package ua.bala.project_reactor.functional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionalExample {
    public static void main(String[] args) {
        List<String> namesList = List.of("Alex", "Alex", "Bob", "Ninette", "Ihor");
        List<String> filteredNamesList = namesGreaterThanSize(namesList, 3);
        System.out.println("filteredNamesList:" + filteredNamesList);
    }

    private static List<String> namesGreaterThanSize(List<String> namesList, int size) {
        return namesList
            .parallelStream()
            .filter(name -> name.length() > size)
            .distinct()
            .map(String::toUpperCase)
            .sorted()
            .collect(Collectors.toList());
    }
}
