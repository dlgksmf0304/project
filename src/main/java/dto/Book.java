package dto;

import java.io.Serializable;

public class Book implements Serializable {	 // 직렬화
	// dto임으로 자바 빈즈 규약에 맞게 설계해야 함
	// 1. 필드는 private
	// 2. 생성자는 기본생성자 필수(생략시 자동생성)
	// 3. 게터 / 세터로 메서드 처리
	// 4. 직렬화(최근에는 생략하는 방법이 대체되고 있음)
	
	private static final long serialVersionUID = 700971687575271600L;
	// 직렬화시 필요한 sUID 값 자동 생성
	
	
	private String bookId; 		    //도서 ID
	private String name;		    //도서명
	private int  unitPrice; 	    //가격
	private String author;			//저자
	private String description; 	//설명
	private String publisher;	    //출판사
	private String category; 		//분류
	private long   unitsInStock; 	//재고개수
	private String releaseDate;   //출판일(월/년)
	private String condition; 		//신제품 or 구제품 or 리퍼브제품
	
	
	public Book() {
		super(); 
	} // 기본 생성자


	public Book(String bookId, String name, int unitPrice) {
		super();
		this.bookId = bookId;
		this.name = name;
		this.unitPrice = unitPrice;
	} // id와 이름과 가격을 이용한 생성자 


	// 게터, 세터 메서드
	public String getBookId() {
		return bookId;
	}


	public void setBookId(String bookId) {
		this.bookId = bookId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public int getUnitPrice() {
		return unitPrice;
	}


	public void setUnitPrice(int unitPrice) {
		this.unitPrice = unitPrice;
	}


	public String getAuthor() {
		return author;
	}


	public void setAuthor(String author) {
		this.author = author;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getPublisher() {
		return publisher;
	}


	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}


	public String getCategory() {
		return category;
	}


	public void setCategory(String category) {
		this.category = category;
	}


	public long getUnitsInStock() {
		return unitsInStock;
	}


	public void setUnitsInStock(long unitsInStock) {
		this.unitsInStock = unitsInStock;
	}


	public String getReleaseDate() {
		return releaseDate;
	}


	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}


	public String getCondition() {
		return condition;
	}


	public void setCondition(String condition) {
		this.condition = condition;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	@Override
	public String toString() {
		return "Book [bookId=" + bookId + ", name=" + name + ", unitPrice=" + unitPrice + ", author=" + author
				+ ", description=" + description + ", publisher=" + publisher + ", category=" + category
				+ ", unitsInStock=" + unitsInStock + ", releaseDate=" + releaseDate + ", condition=" + condition + "]";
	}
	
	
	
	

	
	

}
