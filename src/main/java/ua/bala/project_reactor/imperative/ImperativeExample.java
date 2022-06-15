package ua.bala.project_reactor.imperative;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImperativeExample {

    public static void main(String[] args) {
        List<String> namesList = List.of("Alex", "Alex", "Bob", "Ninette", "Ihor");
        List<String> filteredNamesList = namesGreaterThanSize(namesList, 3);
        System.out.println("filteredNamesList:" + filteredNamesList);
    }

    private static List<String> namesGreaterThanSize(List<String> namesList, int size) {
        ArrayList<String> newNamesList = new ArrayList<>();

        for (String name: namesList) {
            if (name.length() > size && !newNamesList.contains(name.toUpperCase()))
                newNamesList.add(name.toUpperCase());
        }

        Collections.sort(newNamesList);

        return newNamesList;
    }
}
