import java.util.List;
import java.util.stream.Collectors;


public class TenLauncher {

    public static void main(String[] args) {

        final var lorem = " Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                " Praesent semper tincidunt erat, vitae bibendum mauris congue et." +
                " Suspendisse tincidunt condimentum pharetra. Suspendisse eu augue sed sem posuere gravida vel sit amet mi" +
                ". Proin non lacinia ipsum. Donec blandit sed massa at tincidunt. " +
                "Aliquam erat volutpat. Duis tristique, tellus nec porttitor pulvinar, quam nulla vestibulum velit, non tempor metus elit tempus ligula. " +
                "Integer tempus justo enim, sit amet tempor mauris viverra nec. " +
                "Donec maximus tempor ante ut tincidunt. ";

        // imutable list
        final var words = List.of(lorem.split(" "));
        final var matches = words.stream()
                    .takeWhile(s -> s.length() != 4) // finally a take-while!
                    .collect(Collectors.toUnmodifiableList());
        matches.stream().forEach(System.out::println);

    }
}
