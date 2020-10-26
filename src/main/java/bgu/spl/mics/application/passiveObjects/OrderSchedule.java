package bgu.spl.mics.application.passiveObjects;

public class OrderSchedule {
	
		private String bookTitle; 
		private Integer tick; 

		public OrderSchedule(String bookTitle, Integer tick) {
		    this.bookTitle = bookTitle;
		    this.tick = tick;
		}

		public void setBookTitle(String bookTitle) {
		    this.bookTitle = bookTitle;
		}
		public void setTick(Integer tick) {
		    this.tick = tick;
		}

		public String getBookTitle() {
		    return bookTitle;
		}
		public Integer getTick() {
		    return tick;
		}
}

