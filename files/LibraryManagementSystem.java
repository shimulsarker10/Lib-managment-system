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

    public void searchBook(String query) {
        Book book = findBookById(query);
        if (book == null) book = findBookByTitle(query);
        if (book == null) book = findBookByAuthor(query);

        if (book != null) {
            System.out.println("Book Found: " + book);
        } else {
            System.out.println("No book found with the given information.");
        }
    }

    // Member Management
    public void registerMember(Member member) {
        if (member.getId().isEmpty() || member.getName().isEmpty()) {
            System.out.println("Member ID and name cannot be empty!");
            return;
        }
        members.add(member);
        System.out.println("Member registered: " + member.getName());
    }

    public Member findMemberById(String id) {
        for (Member member : members) {
            if (member.getId().equals(id)) {
                return member;
            }
        }
        return null;
    }
    
    // Transaction Management
    public void issueBook(String memberId, String bookId) {
        Member member = findMemberById(memberId);
        Book book = findBookById(bookId);
        if (member != null && book != null) {
            if (!book.isIssued()) {
                int previousBorrowedCount = member.getBorrowedBooks().size();
                member.borrowBook(book);

                // If the number of books borrowed has not changed, then the book has not been issued
                if (previousBorrowedCount == member.getBorrowedBooks().size()) {
                    return;
                }

                issuedBooks.put(book, member);

                // Calculate due date (7 days from issue date)
                Calendar cal = Calendar.getInstance();
                cal.setTime(book.getIssueDate());
                cal.add(Calendar.DAY_OF_MONTH, 7);
                Date dueDate = cal.getTime();

                System.out.println("Book Issued Successfully!");
                System.out.println("Member: " + member.getName());
                System.out.println("Book: " + book.getTitle());
                System.out.println("Due Date: " + dateFormat.format(dueDate));
            } else {
                System.out.println("Book is already issued.");
            }
        } else {
            System.out.println("Member or Book not found.");
        }
    }



    public void returnBook(String memberId, String bookId) {
        Member member = findMemberById(memberId);
        Book book = findBookById(bookId);
        if (member != null && book != null) {
            if (issuedBooks.containsKey(book)) {
                member.returnBook(book);
                issuedBooks.remove(book);

                double fine = book.calculateFine();
                System.out.println("Book Returned Successfully!");
                System.out.println("Member: " + member.getName());
                System.out.println("Book: " + book.getTitle());
                if (fine > 0) {
                    System.out.println("Overdue Fine: $" + fine);
                } else {
                    System.out.println("No fine.");
                }
            } else {
                System.out.println("Book was not issued to this member.");
            }
        } else {
            System.out.println("Member or Book not found.");
        }
    }

    // Report Generation
    public void generateBooksReport() {
        System.out.println("Books Report:");
        for (Book book : books) {
            System.out.println(book);
        }
    }

}   


// Main class to test the library system
public class LibraryManagementSystem {
    public static void main(String[] args) {
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n--- Library Management System ---");
            System.out.println("1. Add Book");
            System.out.println("2. Delete Book");
            System.out.println("3. Search Book");
            System.out.println("4. Register Member");
            System.out.println("5. Issue Book");
            System.out.println("6. Return Book");
            System.out.println("7. Generate Books Report");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter Book ID: ");
                    String bookId = scanner.nextLine();
                    System.out.print("Enter Book Title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter Book Author: ");
                    String author = scanner.nextLine();
                    library.addBook(bookId, title, author);
                    break;
                case 2:
                    System.out.print("Enter Book ID to delete: ");
                    String deleteBookId = scanner.nextLine();
                    library.deleteBook(deleteBookId);
                    break;
                case 3:
                    System.out.print("Enter Book ID, Title, or Author to search: ");
                    String query = scanner.nextLine();
                    library.searchBook(query);
                    break;
                case 4:
                    System.out.print("Enter Member ID: ");
                    String memberId = scanner.nextLine();
                    System.out.print("Enter Member Name: ");
                    String memberName = scanner.nextLine();
                    System.out.print("Enter Member Type (1. Student / 2. Teacher): ");
                    int memberType = scanner.nextInt();
                    scanner.nextLine();
                    if (memberType == 1) {
                        library.registerMember(new Student(memberId, memberName));
                    } else if (memberType == 2) {
                        library.registerMember(new Teacher(memberId, memberName));
                    } else {
                        System.out.println("Invalid member type.");
                    }
                    break;
                case 5:
                    System.out.print("Enter Member ID: ");
                    String issueMemberId = scanner.nextLine();
                    System.out.print("Enter Book ID: ");
                    String issueBookId = scanner.nextLine();
                    library.issueBook(issueMemberId, issueBookId);
                    break;
                case 6:
                    System.out.print("Enter Member ID: ");
                    String returnMemberId = scanner.nextLine();
                    System.out.print("Enter Book ID: ");
                    String returnBookId = scanner.nextLine();
                    library.returnBook(returnMemberId, returnBookId);
                    break;
                case 7:
                    library.generateBooksReport();
                    break;
                case 8:
                    System.out.print("Are you sure you want to exit? (yes/no): ");
                    String confirm = scanner.nextLine().trim();
                    if (confirm.equalsIgnoreCase("yes")) {
                        System.out.println("Exiting...");
                        choice = 8;
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 8);

        scanner.close();
    }
}