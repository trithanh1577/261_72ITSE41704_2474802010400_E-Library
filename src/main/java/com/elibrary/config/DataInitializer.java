package com.elibrary.config;

import com.elibrary.domain.Book;
import com.elibrary.domain.BookCopy;
import com.elibrary.domain.Role;
import com.elibrary.domain.UserAccount;
import com.elibrary.repository.BookCopyRepository;
import com.elibrary.repository.BookRepository;
import com.elibrary.repository.UserAccountRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("!prod")
public class DataInitializer {

    private static final List<SeedTitle> SAMPLE_TITLES = List.of(
            new SeedTitle("Software Systems Architecture: Working With Stakeholders Using Viewpoints and Perspectives", "9780321718334", "Nick Rozanski, Eoin Woods", "Software Engineering"),
            new SeedTitle("Software Engineering", "9780201398151", "Ian Sommerville", "Software Engineering"),
            new SeedTitle("UML Distilled: A Brief Guide to the Standard Object Modeling Language", "9780321193681", "Martin Fowler", "Software Engineering"),
            new SeedTitle("Software Estimation: Demystifying the Black Art", "9780735605350", "Steve McConnell", "Software Engineering"),
            new SeedTitle("User Stories Applied: For Agile Software Development", "9780321205681", "Mike Cohn", "Software Engineering"),
            new SeedTitle("Object-Oriented Design Heuristics", "9780201633856", "Arthur J. Riel", "Software Engineering"),
            new SeedTitle("Generative Programming: Methods, Tools, and Applications", "9780201309775", "Krysztof Czarnecki, Ulrich Eisenecker", "Software Engineering"),
            new SeedTitle("The Art of Systems Architecting", "9781420079135", "Mark W. Maier", "Systems Engineering"),
            new SeedTitle("System Engineering Management", "9781119047827", "Benjamin S. Blanchard", "Systems Engineering"),
            new SeedTitle("The Mythical Man-Month", "9780201835953", "Frederick P. Brooks Jr.", "Software Engineering"),
            new SeedTitle("The Design of Design", "9780201362985", "Frederick P. Brooks Jr.", "Software Engineering"),
            new SeedTitle("Design Patterns: Elements of Reusable Object-Oriented Software", "9780201633610", "Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides", "Software Design"),
            new SeedTitle("Pattern-Oriented Software Architecture: A System of Patterns", "9780471958697", "Frank Buschmann, Regine Meunier, Hans Rohnert, Peter Sommerlad, Michael Stal", "Software Design"),
            new SeedTitle("Pattern-Oriented Software Architecture: Patterns for Concurrent and Networked Objects", "9780471606956", "Douglas Schmidt, Michael Stal, Hans Rohnert, Frank Buschmann", "Software Design"),
            new SeedTitle("Pattern-Oriented Software Architecture: Patterns for Resource Management", "9780470845257", "Michael Kircher, Prashant Jain", "Software Design"),
            new SeedTitle("Pattern-Oriented Software Architecture: A Pattern Language for Distributed Computing", "9780470059029", "Frank Buschmann, Kevlin Henney, Douglas C. Schmidt", "Software Design"),
            new SeedTitle("Patterns for Fault Tolerant Software", "9780470319796", "Robert S. Hanmer", "Software Design"),
            new SeedTitle("Security Patterns: Integrating Security and Systems Engineering", "9780470858844", "Markus Schumacher, Eduardo Fernandez-Buglioni, Duane Hybertson, Frank Buschmann, Peter Sommerlad", "Security"),
            new SeedTitle("Patterns for Parallel Software Design", "9780470697344", "Jorge Luis Ortega-Arjona", "Software Design"),
            new SeedTitle("Elemental Design Patterns", "9780321711922", "Jason Mc C. Smith", "Software Design"),
            new SeedTitle("Patterns of Enterprise Application Architecture", "9780321127426", "Martin Fowler", "Software Design"),
            new SeedTitle("The Art of Computer Programming, Volumes 1-4A", "9780321751041", "Donald Knuth", "Computer Science"),
            new SeedTitle("Introduction to Automata Theory, Languages and Computation", "9780201029888", "John E. Hopcroft, Jeffery D. Ullman", "Computer Science"),
            new SeedTitle("Principles of Concurrent and Distributed Programming", "9780321312839", "M. Ben-Ari", "Computer Science"),
            new SeedTitle("Distributed Algorithms", "9781558603486", "Nancy A. Lynch", "Computer Science"),
            new SeedTitle("Concrete Mathematics: A Foundation for Computer Science", "9780201558029", "Ronald Graham, Donald Knuth, Oren Patashnik", "Computer Science"),
            new SeedTitle("Discrete and Combinatorial Mathematics", "9780321385024", "Ralph P. Grimaldi", "Mathematics"),
            new SeedTitle("Data Structures and Algorithm Analysis in C++", "9780201361223", "Mark A. Weiss", "Computer Science"),
            new SeedTitle("Code Complete", "9780735619678", "Steve McConnell", "Programming"),
            new SeedTitle("Refactoring: Improving the Design of Existing Code", "9780201485677", "Martin Fowler, Kent Beck, John Brant, William Opdyke, Don Roberts", "Software Engineering"),
            new SeedTitle("Working Effectively with Legacy Code", "9780131177055", "Michael C. Feathers", "Software Engineering"),
            new SeedTitle("Test Driven Development: By Example", "9780321146533", "Kent Beck", "Software Testing"),
            new SeedTitle("The C Programming Language", "9780131103702", "Brian W. Kernighan, Dennis M. Ritchie", "Programming"),
            new SeedTitle("The C++ Programming Language", "9780321958327", "Bjarne Stroustrup", "Programming"),
            new SeedTitle("The C++ Standard Library: A Tutorial and Reference", "9780321623218", "Nicolai M. Josuttis", "Programming"),
            new SeedTitle("C++ Templates: The Complete Guide", "9780201734843", "David Vandevoorde, Nicolai M. Josuttis", "Programming"),
            new SeedTitle("Inside the C++ Object Model", "9780201834543", "Stanley B. Lippman", "Programming"),
            new SeedTitle("Effective C++: 55 Specific Ways to Improve Your Programs and Designs", "9780321334879", "Scott Meyers", "Programming"),
            new SeedTitle("More Effective C++: 35 New Ways to Improve Your Programs and Designs", "9780201633719", "Scott Meyers", "Programming"),
            new SeedTitle("Effective STL: 50 Specific Ways to Improve Your Use of the Standard Template Library", "9780201749625", "Scott Meyers", "Programming"),
            new SeedTitle("Effective Modern C++", "9781491903995", "Scott Meyers", "Programming"),
            new SeedTitle("Modern C++ Design: Generic Programming and Design Patterns Applied", "9780201704310", "Andrei Alexandrescu", "Programming"),
            new SeedTitle("C++ Concurrency in Action: Practical Multithreading", "9781933988771", "Anthony Williams", "Programming"),
            new SeedTitle("Real-Time C++: Efficient Object-Oriented and Template Microcontroller Programming", "9783662567173", "Christopher Kormanyos", "Programming"),
            new SeedTitle("Optimized C++: Proven Techniques for Heightened Performance", "9781491922064", "Kurt Guntheroth", "Programming"),
            new SeedTitle("The Art of Prolog: Advanced Programming Techniques", "9780262193382", "Leon S. Sterling, Ehud Y. Shapiro", "Programming"),
            new SeedTitle("Programming Perl", "9780596000271", "Larry Wall, Tom Christiansen, Jon Orwant", "Programming")
    );

    @Bean
    CommandLineRunner seedData(UserAccountRepository userRepository,
                               BookRepository bookRepository,
                               BookCopyRepository copyRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                userRepository.save(new UserAccount("admin", passwordEncoder.encode("admin123"), "Demo Librarian", Role.LIBRARIAN));
                userRepository.save(new UserAccount("member", passwordEncoder.encode("member123"), "Demo Member", Role.MEMBER));
            }
            if (bookRepository.count() == 0) {
                Book cleanCode = bookRepository.save(new Book("Clean Code", "9780132350884", "Robert C. Martin", "Software", "A practical introduction to readable and maintainable code."));
                Book domainDriven = bookRepository.save(new Book("Domain-Driven Design", "9780321125217", "Eric Evans", "Software", "A reference for modelling complex business domains."));
                Book java = bookRepository.save(new Book("Effective Java", "9780134685991", "Joshua Bloch", "Programming", "Patterns and practices for modern Java development."));
                copyRepository.save(new BookCopy("CC-001", cleanCode));
                copyRepository.save(new BookCopy("CC-002", cleanCode));
                copyRepository.save(new BookCopy("DDD-001", domainDriven));
                copyRepository.save(new BookCopy("JAVA-001", java));
                int copyNumber = 1;
                for (SeedTitle sample : SAMPLE_TITLES) {
                    Book book = bookRepository.save(new Book(sample.title(), sample.isbn(), sample.author(),
                            sample.category(), "Sample reference title from the E-Library course catalogue."));
                    copyRepository.save(new BookCopy("SEED-%03d".formatted(copyNumber++), book));
                }
            }
        };
    }

    private record SeedTitle(String title, String isbn, String author, String category) { }
}
