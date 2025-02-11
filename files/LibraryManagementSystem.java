import java.util.*;
import java.text.SimpleDateFormat;

//Book class

class Book {
    private String id;
    private String title;
    private String author;
    private boolean isIssued;
    private Date issueDate;
    private Date returnDate;

    public Book(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isIssued = false;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isIssued() {
        return isIssued;
    }

    public void setIssued(boolean issued) {
        isIssued = issued;
    }
    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public double calculateFine() {
        if (returnDate == null || issueDate == null) {
            return 0;
        }
        long diffInMillis = returnDate.getTime() - issueDate.getTime();
        long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);
        if (diffInDays > 7) { // assuming a 7-day borrowing limit
            return (diffInDays - 7) * 10.0; // 10 TK. per day fine after 7 days
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", status=" + (isIssued ? "Issued" : "Available") +
                '}';
    }
}

// Member class (Base class)
abstract class Member {
    private String id;
    private String name;
    private List<Book> borrowedBooks;

    public Member(String id, String name) {
        this.id = id;
        this.name = name;
        this.borrowedBooks = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public abstract int getBorrowingLimit();

public void borrowBook(Book book) {
    if (borrowedBooks.size() < getBorrowingLimit()) {
        borrowedBooks.add(book);
        book.setIssued(true);
        book.setIssueDate(new Date());  // Set current date as the issue date
    } else {
        System.out.println("Your borrowing limit has been reached, so you won't be able to borrow any more books at the moment " + name);
        return; // Stop execution here
    }
}


    public void returnBook(Book book) {
        if (borrowedBooks.contains(book)) {
            borrowedBooks.remove(book);
            book.setIssued(false);
            book.setReturnDate(new Date());  // Set current date as return date
        } else {
            System.out.println("Book not borrowed by " + name);
        }
    }
}

// Student class (inherits Member)
class Student extends Member {
    public Student(String id, String name) {
        super(id, name);
    }

    @Override
    public int getBorrowingLimit() {
        return 3; // Students can borrow up to 3 books
    }
}

// Teacher class (inherits Member)
class Teacher extends Member {
    public Teacher(String id, String name) {
        super(id, name);
    }

    @Override
    public int getBorrowingLimit() {
        return 5; // Teachers can borrow up to 5 books
    }
}

// Library class
class Library {
    private List<Book> books;
    private List<Member> members;
    private Map<Book, Member> issuedBooks;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public Library() {
        books = new ArrayList<>();
        members = new ArrayList<>();
        issuedBooks = new HashMap<>();
    }

    // Book Management
    public void addBook(String id, String title, String author) {
        if (id.isEmpty() || title.isEmpty() || author.isEmpty()) {
            System.out.println("Book ID, title, and author cannot be empty!");
            return;
        }
        books.add(new Book(id, title, author));
        System.out.println("Book added: " + title);
    }

    public void deleteBook(String id) {
        Book book = findBookById(id);
        if (book != null) {
            books.remove(book);
            System.out.println("Book deleted: " + book.getTitle());
        } else {
            System.out.println("Book not found with ID: " + id);
        }
    }

    public Book findBookById(String id) {
        for (Book book : books) {
            if (book.getId().equals(id)) {
                return book;
            }
        }
        return null;
    }

    public Book findBookByTitle(String title) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null;
    }

    public Book findBookByAuthor(String author) {
        for (Book book : books) {
            if (book.getAuthor().equalsIgnoreCase(author)) {
                return book;
            }
        }
        return null;
    }
}   


// Main class to test the library system
public class LibraryManagementSystem {
    public static void main(String[] args) {
        System.out.println("Shimul Sarker");
    }
}