# BookHub

BookHub – Library Borrow Tracker with Gamification and Review System

**Project Description:**
  BookHub is a Java-based library management system designed to help users efficiently borrow and return books while adding engaging and interactive features. Members can log in to the system, borrow books, and track important dates such as the borrow date, the maximum return date (7 days), and the actual return date when the book is returned. After returning a book, users can submit reviews and ratings, which can be viewed by other members to help guide reading choices. To make the system more interactive, BookHub incorporates a gamification feature where timely returns earn points and badges like “Speedy Reader” or “Bookworm,” while late returns result in point deductions. The application ensures input validation to prevent errors, maintains a history of borrowed books, and encapsulates date-related calculations through a dedicated Date class. Overall, BookHub combines library management with gamification and social interaction, making borrowing books a more engaging and rewarding experience.

  The main entities in BookHub include Member, Book, BorrowRecord, BorrowRecordList, Review, and Date. The Member entity stores information such as member ID, name, email, password, role, points, and badges, and is responsible for user authentication, profile management, tracking points and badges, and viewing borrow history. The Book entity contains the book ID, title, author, category, and available copies, and manages book information and availability. Each borrowing transaction is represented by a BorrowRecord, which tracks the record ID, member ID, book ID, borrow date, maximum return date, actual return date, and whether the book has been returned, with methods to calculate return dates, mark books as returned, check borrow duration, and determine overdue status. BorrowRecordList manages collections of borrow records, including all records, active borrows, and returned books, providing functionality to add new records and retrieve records by status or member. The Review entity captures reviews through review ID, borrower ID, book ID, rating, and comment, and allows users to add reviews, retrieve reviews by book or user, and display review content. Finally, the Date entity encapsulates date-related logic with day, month, and year attributes, offering methods to add days, compare dates, and format dates as strings, ensuring accurate handling of borrowing and return periods.

**Project Flow:**
Login: Members log in using their credentials.

Borrow Book: Members select and confirm a book to borrow via the GUI.

Record Management: A BorrowRecord is created and the maximum return date is automatically set.

Return Book: Members press the “RETURNED” button; points are awarded or deducted based on timeliness.

Review Submission: After returning a book, members can submit a review and rating.

View History: Members can view their borrowing history, reviews, earned points, and badges.


**Key Features:**
User Authentication: Login, registration, and profile management for members.

Borrowing System: Track borrowed books, return deadlines, and manage returns.

Review System: Submit and view book reviews and ratings.

Gamification: Award points for timely returns, assign badges, and encourage responsible borrowing.

Validation: Prevent invalid inputs, such as empty book titles or invalid dates, and provide friendly error messages.

Date Management: Encapsulate all date-related logic in a Date class for accurate borrow and return tracking.

**Unique Twist:**
BookHub combines a gamification system with a review system, making the library experience more interactive, engaging, and community-oriented. Users are motivated to return books on time and actively participate in sharing book feedback, which makes borrowing books not only efficient but rewarding and fun.
