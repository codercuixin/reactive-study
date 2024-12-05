package org.example.reactor;

import reactor.core.publisher.Flux;
import java.util.Random;

public class DynamicFluxSizeExample {
    public static void main(String[] args) {
        // Original Flux emitting three items
        Flux<String> originalFlux = Flux.just("Item 1", "Item 2", "Item 3");

        // Create a new Flux dynamically with potentially different sizes
        Flux<String> newFlux = originalFlux.flatMap(item -> {
            // Generate a random number of new items (between 1 and 3)
            Random random = new Random();
            int numberOfNewItems = random.nextInt(3); // 1 to 3
            if(numberOfNewItems == 1){
                return Flux.empty();
            }
            // Create a new Flux with the generated number of items
            return Flux.range(1, numberOfNewItems) // Create a range of new items
                       .map(i -> item + " - New " + i); // Transform into new items
        });

        // Subscribe to the new Flux and print the results
        newFlux.subscribe(System.out::println);
    }
}
