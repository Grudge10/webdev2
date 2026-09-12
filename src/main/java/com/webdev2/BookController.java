package com.webdev2;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/books")
public class BookController {
    private final List<Book> books = new ArrayList<>();

    public BookController() {
        books.add(new Book(1L, "Harry Potter", "J. K. Rowling"));
        books.add(new Book(2L, "Winnie-the-Pooh", "A. A. Milne"));
        books.add(new Book(3L, "Solo Leveling", "Chu-gong"));
        books.add(new Book(4L, "Sakamoto Days", "Yuto Suzuki"));
        books.add(new Book(5L, "Study Group", "Shin Hyung-wook"));
    }

    @GetMapping
    @ResponseBody
    public List<Book> getBooks(@RequestParam(required = false) String author) {
        if (author == null || author.isBlank()) {
            return books;
        }

        return books.stream()
                .filter(book -> book.getAuthor().equalsIgnoreCase(author.trim()))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookDetail(@PathVariable Long id, Model model) {
        Book searchedBook = books.stream()
                .filter(book -> book.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (searchedBook == null) {
            return ResponseEntity.notFound().build();
        }

        model.addAttribute("book", searchedBook);

        return ResponseEntity.ok(searchedBook);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createBook(Book book) {
        if (book.getId() == null) {
            book.setId((long) books.size() + 1);
        }
        books.add(book);

        return "redirect:/books";
    }
}
